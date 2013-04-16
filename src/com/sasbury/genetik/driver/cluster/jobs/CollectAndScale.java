package com.sasbury.genetik.driver.cluster.jobs;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;
import com.sasbury.genetik.reporting.*;
import com.sasbury.util.json.*;

public class CollectAndScale extends ClusterJob
{
    @SuppressWarnings("unchecked")
    public void execute() throws Exception
    {
        long start = System.currentTimeMillis();
        JSONDecoder decoder = new JSONDecoder();
        int generation = getIntArg(GENERATION, 0);
        Run run = getRun();
        Population pop = getPopulationFor(getRun(), generation);
        Population previousPop = (generation>0)?getPopulationFor(getRun(), generation-1):null;
        int i=0,max=0;
        int popSize = pop.size();
        GenerationalOptimizer optimizer = (GenerationalOptimizer) run.createObject(GenetikConstants.OPTIMIZER);
        int chunkSize = run.getIntProperty(ClusterJobDriver.SCORING_CHUNK_SIZE, ClusterJobDriver.DEFAULT_SCORING_CHUNK_SIZE, true);
        int chunks = (popSize%chunkSize == 0) ? popSize/chunkSize : (popSize/chunkSize) + 1;
        String buildStats = getRunData(run, "build_stats."+generation+".json");
        stats.mergeStats(buildStats);
        
        //The total size of the chunks should add up to the pop size, so 
        //that all elements are replaced
        for(i=0;i<=chunks;i++)
        {
            File chunkFile = getChunkFileFor(run, generation, i);
            
            if(chunkFile.exists())//may be missing, but keep going
            {
                Population chunk = JSONPopulationCoder.decode(chunkFile, run);
    
                String scoreStats = getRunData(run, "chunk_stats."+generation+"."+i+".json");
                stats.mergeStats(scoreStats);
                
                for(int j=0,maxj=chunk.size();j<maxj;j++)
                {
                    pop.set(max, chunk.get(j));//we are adding in global order, from the ordered chunks
                    max++;
                }
                
                disposeOfPopulation(chunk);
                chunkFile.delete();
            }
        }

        repair(pop,previousPop,generation);
        
        FitnessScaling scale = (FitnessScaling) run.createObject(GenetikConstants.SCALING);
        scale.initialize(pop, run);
        
        for(i=0,max=pop.size();i<max;i++)
        {
            Individual ind = pop.get(i);
            if(ind.hasScores()) ind.setFitness(scale.calculateFitness(ind, pop, run));
            else ind.setFitness(Integer.MIN_VALUE);
        }
        
        scale.cleanup();
        
        optimizer.finalizePopulation(previousPop,pop, run, generation);
        
        long end = System.currentTimeMillis();
        getStats().increment(GenerationalStats.SCALING_TIME,(end-start));
        
        savePopulationFor(pop, run, generation);

        //set up the statistics
        HashMap<String,Object> jsonStats;
        HashMap<String,Object> genStats = stats.captureStatisticsForCurrentGeneration(pop,generation);
        
        String statsJson = getRunData(getRun(),"statistics.json");
        
        if(statsJson != null)
        {
            jsonStats = decoder.decode(statsJson);
        }
        else
        {
            jsonStats = new HashMap<String,Object>();
            jsonStats.put(GenerationalStats.GENERATION_STATISTICS,new ArrayList<Object>());
        }
        
        ((ArrayList<Object>)jsonStats.get(GenerationalStats.GENERATION_STATISTICS)).add(genStats);
        
        saveStats(jsonStats);
        
        disposeOfPopulation(pop);
        if(previousPop!=null) disposeOfPopulation(previousPop);
    }
    
    public void repair(Population pop,Population prevPop,int generation) throws Exception
    {
        if(prevPop == null) return;//can't repair without it
        
        for(int i=0,max=pop.size();i<max;i++)
        {
            Individual p = pop.get(i);
            
            if(!p.hasScores())
            {
                Individual repairP = null;
                
                for(int j=0,maxj=100;j<maxj;j++)//try up to 100 times to get a good individual
                {
                    Individual tempP = prevPop.get(run.pickRandom(prevPop.size()-1));//pick random adds one
                    
                    if(tempP!=null && tempP.hasScores())
                    {
                        repairP = tempP;
                        break;
                    }
                }
                
                if(repairP!=null)
                {
                    Individual np = repairP.duplicate();
                    np.setId(p.getId());
                    pop.set(i, np);
                    
                    getStats().increment(GenerationalStats.REPAIR_COPIES, 1);
                    
                    Logger.getLogger(GenetikConstants.LOGGER).log(Level.WARNING,"Repairing id="+p.getId()+" in generation "+generation+" with id="+repairP.getId()+" from generation "+(generation-1));
                }
                else
                {
                    Logger.getLogger(GenetikConstants.LOGGER).log(Level.WARNING,"Unable to repair id="+p.getId()+" in generation "+generation);
                }
            }
        }
    }
    
    public File getLogFile()
    {
        int generation = getIntArg(GENERATION, 0);
        return new File((getRun()!=null)?getRunDir(getRun()):root,"collect."+generation+".log");
    }
    
    /**
     * Normally just save this, some jobs will pre-process the stats. Score job needs to not save, and
     * do something fancier.
     */
    public void saveStats(HashMap<String,Object> jsonStats)
    {
        if(jsonStats != null)
        {
            try
            {
                JSONEncoder encoder = new JSONEncoder();
                String json = encoder.encode(jsonStats);
                saveRunData(getRun(), "statistics.json", json);
            }
            catch(Exception exp)
            {
                throw new RuntimeException("Error converting stats to json.",exp);
            }
        }
    }
}
