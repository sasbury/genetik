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
public class Score extends ClusterJob
{
    public void execute() throws Exception
    {
        long start = System.currentTimeMillis();
        Run run = getRun();
        int chunk = getIntArg(CHUNK, 0);
        int generation = getIntArg(GENERATION, 0);
        int chunkSize = run.getIntProperty(JobBasedGenerationalDriver.SCORING_CHUNK_SIZE, JobBasedGenerationalDriver.DEFAULT_SCORING_CHUNK_SIZE, true);
        Population curPopulation = getPopulationFor(run, generation);
        ArrayList<Individual> scored = new ArrayList<Individual>();
        ArrayList<Individual> possibles = new ArrayList<Individual>();
        
        for(int i=0,max=curPopulation.size();i<max;i++)
        {
            Individual check = curPopulation.get(i);
            
            if(!check.hasScores())
            {
                possibles.add(check);
            }
            else
            {
                scored.add(check);
            }
        }

        int popSize = curPopulation.size();
        int maxScoreTaskPopIndex = (popSize%chunkSize == 0) ? popSize/chunkSize : (popSize/chunkSize) + 1;
        ArrayList<ArrayList<Individual>> assignments = new ArrayList<ArrayList<Individual>>();

        for(int i=0;i<maxScoreTaskPopIndex;i++)
        {
            assignments.add(new ArrayList<Individual>());
        }

        for(int i=0;i<possibles.size();i++)
        {
            int assignIndex = i%maxScoreTaskPopIndex;
            
            ArrayList<Individual> a = assignments.get(assignIndex);
            
            a.add(possibles.get(i));
        }
        
        ArrayList<Individual> myAssignments = assignments.get(chunk);
        int curChunkSize = myAssignments.size();
        Population workingChunk = createEmptyPopulation();

        for(int i=0;i<curChunkSize;i++)
        {
            Individual p = (Individual) myAssignments.get(i);
            workingChunk.set(i,p);
        }
        
        String testStr = run.getProperty(GenetikConstants.TESTS);
        String testNames[] = testStr.split(",");
        FitnessTest[] tests = new FitnessTest[testNames.length];
        int testCount = testNames.length;
        
        //load the tests
        for(int i=0;i<testCount;i++)
        {
            String testName = testNames[i];
            FitnessTest test = (FitnessTest) run.createObject(GenetikConstants.CLASS,testName);
            tests[i] = test;
        }
        
        //score the individuals
        for(int i=0,max=curChunkSize;i<max;i++)
        {
            Individual ind = workingChunk.get(i);
            double scores[] = new double[testCount];
            
            for(int j=0;j<testCount;j++)
            {
                FitnessTest test = tests[j];
                scores[j] = test.calculateRawScore(ind, run, testNames[j]);
            }
            
            ind.setRawScores(scores);
        }
        
        //Add back all the pre-scored individuals to the first chunk
        if(chunk == 0)
        {
            for(int i=0,max=scored.size();i<max;i++)
            {
                workingChunk.set(-1, scored.get(i));
            }
        }
        
        File file = getChunkFileFor(run, generation,chunk);
        JSONPopulationCoder.encode(workingChunk, file);
         
        long end = System.currentTimeMillis();
        getStats().increment(GenerationalStats.SCORE_TIME,(end-start));
        saveStats();
    }
    
    public File getLogFile()
    {
        int chunk = getIntArg(CHUNK, 0);
        int generation = getIntArg(GENERATION, 0);
        return new File(getRunDir(getRun()),"chunk."+generation+"."+chunk+".log");
    }

    public void saveStats()
    {
        //score doesn't save main stats file only a chunk
        int chunk = getIntArg(CHUNK, 0);
        int generation = getIntArg(GENERATION, 0);
        saveRunData(run, "chunk_stats."+generation+"."+chunk+".json", getStats().asJSON());
    }
}
