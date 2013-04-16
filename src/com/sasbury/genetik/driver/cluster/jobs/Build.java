package com.sasbury.genetik.driver.cluster.jobs;

import java.io.*;
import java.util.*;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;
import com.sasbury.genetik.reporting.*;

/**
 * Given a run description, evolution description and optional population generates a subpopulation.
 * If the previous population is not provided, an "initial" population is created.
 * 
 */
public class Build extends ClusterJob
{
    public void execute() throws Exception
    {
        long start = System.currentTimeMillis();
        Run run = getRun();
        int generation = getIntArg(GENERATION, 0);
        
        if(generation == 0)
        {
            Population pop = createEmptyPopulation();
            IndividualGenerator generator = (IndividualGenerator) run.createObject(GenetikConstants.GENERATOR);
            int popSize = Integer.parseInt(run.getProperty(GenetikConstants.POPULATION));
            Population prevPop = null;
            Run previousRun = getPreviousRun();
            
            if(previousRun !=null)
            {
                int prevGen = Integer.parseInt(previousRun.getProperty(GenetikConstants.GENERATIONS));
                prevPop = getPopulationFor(previousRun, prevGen-1);//index from 0
                generator.initialize(run, previousRun, prevPop);
            }
            
            for(int i=0;i<popSize;i++)
            {
                Individual ind = generator.generateIndividual(run);
                pop.set(i, ind);
            }
            
            savePopulationFor(pop, run, 0);
            disposeOfPopulation(pop);
            if(prevPop != null) disposeOfPopulation(prevPop);
            
            generator.cleanup();
        }
        else
        {
            Population pop = createEmptyPopulation();
            GenerationalOptimizer optimizer = (GenerationalOptimizer) run.createObject(GenetikConstants.OPTIMIZER);
            Population prevPop = getPopulationFor(run,generation-1);
            optimizer.generatePopulation(prevPop, pop, run, generation);
            
            HashMap<Chromosome,Individual> cache = buildCache(generation);
            for(int i=0;i<pop.size();i++)
            {
                Individual p = pop.get(i);
                Chromosome code = p.getChromosome();
                Individual hit = cache.get(code);
                
                if(hit!=null && hit.hasFitness())
                {
                    p.setRawScores(hit.getRawScores());
                    p.setUserData(hit.getUserData());//copy working data for reporting
                    
                    getStats().increment(GenerationalStats.SCORING_CACHE_HITS, 1);
                }
            }
            
            savePopulationFor(pop, run, generation);
            disposeOfPopulation(pop);
            if(prevPop != null) disposeOfPopulation(prevPop);
        }
 
        long end = System.currentTimeMillis();
        getStats().increment(GenerationalStats.BUILD_TIME,(end-start));
        
        saveStats();
    }

    public File getLogFile()
    {
        int generation = getIntArg(GENERATION, 0);
        return new File((getRun()!=null)?getRunDir(getRun()):root,"build."+generation+".log");
    }
    
    public HashMap<Chromosome,Individual> buildCache(int currentGeneration) throws Exception
    {
        HashMap<Chromosome,Individual> cache = new HashMap<Chromosome,Individual>();
        
        for(int i=0;i<currentGeneration;i++)
        {
            Population pop = getPopulationFor(getRun(), i);
            
            for(int j=0,max=pop.size();j<max;j++)
            {
                Individual p = pop.get(j);
                Chromosome code = p.getChromosome();
                
                if(!cache.containsKey(code) && p.hasFitness())
                {
                    cache.put(code, p);
                }
            }
        }
        
        return cache;
    }

    public void saveStats()
    {
        int generation = getIntArg(GENERATION, 0);
        saveRunData(run, "build_stats."+generation+".json", getStats().asJSON());
    }
}
