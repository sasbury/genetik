package com.sasbury.genetik.driver;

import java.util.*;
import java.util.logging.*;

import com.sasbury.genetik.*;

public abstract class AbstractGenerationalDriver
{
    protected Properties props;
    
    public AbstractGenerationalDriver()
    {
        super();
    }
    
    public abstract Run createRun(String runName);

    public abstract void preprocess();

    public abstract void preprocess(Run run);

    /**
     * Separated from build to allow for phased generation.
     * @param run
     * @param previousRun
     */
    public abstract void initialBuild(Run run,Run previousRun);
    
    public abstract void build(Run run, int generation);

    public abstract boolean score(Run run, int generation);
    
    public abstract void collectAndScale(Run run, int generation);
    
    //called if a scoring fails, can be used to save data
    public abstract void haltingRun(Run run);
    
    public abstract void postprocess(Run run);

    public abstract void postprocess();
    
    public abstract boolean needsToRun(Run run,int generation);
    
    public abstract void startingGeneration(Run run,int generation);

    public abstract void finishedGeneration(Run run,int generation);
    
    public void execute(Properties props)
    {
        this.props = props;

        boolean badScore = false;
        String runStr = props.getProperty(GenetikConstants.RUNS);
        Logger logger = Logger.getLogger(GenetikConstants.LOGGER);
        
        if(runStr != null)
        {
            String runNames[] = runStr.split(",");
            Run prevRun = null;
            boolean runAll = false;

            System.out.println("Starting...");
            System.out.flush();
            preprocess();
            
            for(String runName : runNames)
            {
                Run run = createRun(runName);
                int generations = Integer.parseInt(run.getProperty(GenetikConstants.GENERATIONS));

                System.out.println("Starting run "+runName);
                System.out.flush();
                
                preprocess(run);
                
                for(int i=0;i<generations;i++)
                {
                    if(!needsToRun(run,i) && !runAll)
                    {
                        logger.info("Skipping generation "+i+" it is complete.");
                        continue;
                    }
                    else
                    {
                        runAll = true;
                        logger.info("Starting generation "+i);
                    }
                    
                    startingGeneration(run,i);
                    
                    if(i==0) initialBuild(run,prevRun);
                    else build(run,i);
                    
                    if(!score(run,i))
                    {
                        badScore = true;
                        logger.warning("Error scoring generation "+i+" halting for user intervention.");
                        break;//end the run right here
                    }
                    
                    collectAndScale(run,i);
                    
                    finishedGeneration(run, i);
                }
                
                if(badScore)
                {
                    break;
                }

                postprocess(run);
                prevRun = run;
            }

            if(!badScore)
            {
                System.out.println("Postprocessing...");
                System.out.flush();
                postprocess();
            }
        }
        else
        {
            System.out.println("Starting...");
            System.out.flush();
            preprocess();
            Run run = createRun(null);
            int generations = Integer.parseInt(run.getProperty(GenetikConstants.GENERATIONS));
            System.out.println("Starting run");
            System.out.flush();
            
            preprocess(run);
            
            for(int i=0;i<generations;i++)
            {
                startingGeneration(run,i);
                
                if(i==0) initialBuild(run,null);
                else build(run,i);
                
                if(!score(run,i))
                {
                    badScore = true;
                    break;//end the run right here
                }
                
                collectAndScale(run,i);
                
                finishedGeneration(run, i);
            }

            if(!badScore)
            {
                System.out.println("Postprocessing...");
                System.out.flush();
                postprocess(run);
                postprocess();
            }
        }
    }
    
    public Properties getProperties()
    {
        return props;
    }
}
