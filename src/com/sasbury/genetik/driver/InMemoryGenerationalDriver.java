package com.sasbury.genetik.driver;

import java.util.*;

import com.sasbury.genetik.*;
import com.sasbury.genetik.population.*;

public class InMemoryGenerationalDriver extends SimpleGenerationalDriver
{
    protected HashMap<Run,ArrayList<InMemoryPopulation>> generations;
    protected HashMap<String,String> globalData;
    protected HashMap<String,HashMap<String,String>> runData;

    public void preProcess()
    {
        generations = new HashMap<Run,ArrayList<InMemoryPopulation>>();
        globalData = new HashMap<String,String>();
        runData = new HashMap<String,HashMap<String,String>>();
    }

    public Population createEmptyPopulation()
    {
        return new InMemoryPopulation();
    }
    
    public void disposeOfPopulation(Population pop)
    {
        //we keep them all in memory, so do nothing
    }

    public Population getPopulationFor(Run run,int generation)
    {
        ArrayList<InMemoryPopulation> list = generations.get(run);
        Population pop = null;
        
        if(list == null)
        {
            list = new ArrayList<InMemoryPopulation>();
            generations.put(run, list);
        }
        
        if(generation>=0) pop = list.get(generation);
        
        return pop;
    }
    
    public void savePopulationFor(Population pop,Run run,int generation)
    {
        ArrayList<InMemoryPopulation> list = generations.get(run);
        
        if(list == null)
        {
            list = new ArrayList<InMemoryPopulation>();
            generations.put(run, list);
        }
        
        list.set(generation, (InMemoryPopulation) pop);
    }

    public String getData(String name)
    {
        String retVal = globalData.get(name);
        return retVal;
    }

    public String getRunData(Run run, String name)
    {
        String retVal = null;
        HashMap<String,String> rData = runData.get(run.getName());
        
        if(rData != null) retVal = rData.get(name);
        
        return retVal;
    }

    public void saveData(String name, String data)
    {
        globalData.put(name,data);
    }

    public void saveRunData(Run run, String name, String data)
    {
        HashMap<String,String> rData = runData.get(run.getName());
        
        if(rData == null)
        {
            rData = new HashMap<String,String>();
            runData.put(run.getName(), rData);
        }
        
        rData.put(name,data);
    }

    public boolean needsToRun(Run run, int generation)
    {
        return getPopulationFor(run, generation)!=null;
    }
}
