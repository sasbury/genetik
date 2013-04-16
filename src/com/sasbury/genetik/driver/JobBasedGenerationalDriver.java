package com.sasbury.genetik.driver;

import java.util.*;

import com.sasbury.genetik.*;

public abstract class JobBasedGenerationalDriver
{
    /**
     * The maximum number of jobs to do, the default is 0 which means no limit.
     */
    public static final String MAXIMUM_JOBS="maximum_jobs";
    
    public static final String SCORING_CHUNK_SIZE="scoring_chunk_size";
    
    public static final int DEFAULT_SCORING_CHUNK_SIZE=10;
    
    protected Properties props;
    protected ArrayList<Object> jobs;
    
    public Run createRun(String runName)
    {
        return new Run(getProperties(),runName,null);
    }

    public abstract void generatePreprocessJobs();

    public abstract void generatePreprocessJobs(Run run);

    /**
     * Separated from build to allow for phased generation.
     * @param run
     * @param previousRun
     */
    public abstract void generateInitialBuildJobs(Run run,Run previousRun);
    
    public abstract void generateBuildJobs(Run run, int generation);

    public abstract void generateScoreJobs(Run run, int generation);
    
    public abstract void generateCollectAndScaleJobs(Run run, int generation);
    
    public abstract void generatePostProcessJobs(Run run);

    public abstract void generatePostProcessJobs();
    
    /**
     * Execute at most maxJobs. Subclasses can stop earlier to clip to generation boundaries. They can also
     * skip into the list of the jobs are already completed. In a worst case a subclass could disobey the max
     * if it was necessary to complete a generation and only per-generation cuts were supported.
     * @param maxJobs
     */
    public abstract int scheduleJobs(int maxJobs);
    
    /**
     * This method will be called before schedule jobs, but after the jobs list if populated.
     * Subclasses can use this method to test if the entire set of runs is complete, and if so
     * no jobs will be scheduled. This method is provided so that execute is reentrant.
     * 
     * Callers of the execute method can call this method after execute returns to see if the set of runs
     * is complete, but it is not valid before execute is called.
     * @return
     */
    public abstract boolean isExperimentComplete();
    
    /**
     * First generate all of the jobs regardless of maximum jobs. Then only 
     * schedule up to the maximum count. Subclasses can decide how they want to handle 
     * the max-jobs value, they can choose to crop by generation or not.
     * 
     * Sub-classes can also support starting in the middle, and skip jobs they don't think they need.
     * @param props
     */
    public int execute(Properties props)
    {
        this.props = props;
        this.jobs = new ArrayList<Object>();
        
        String runStr = props.getProperty(GenetikConstants.RUNS);
        String maxStr = props.getProperty(MAXIMUM_JOBS);
        int maxJobs = (maxStr != null)?Integer.parseInt(maxStr):0;
        
        if(runStr != null)
        {
            String runNames[] = runStr.split(",");
            Run prevRun = null;
            
            generatePreprocessJobs();
            
            for(String runName : runNames)
            {
                Run run = createRun(runName);
                int generations = Integer.parseInt(run.getProperty(GenetikConstants.GENERATIONS));
                
                generatePreprocessJobs(run);
                
                for(int i=0;i<generations;i++)
                {
                    if(i==0) generateInitialBuildJobs(run,prevRun);
                    else generateBuildJobs(run,i);
                    
                    generateScoreJobs(run,i);
                    generateCollectAndScaleJobs(run,i);
                }
                
                generatePostProcessJobs(run);
                prevRun = run;
            }
            
            generatePostProcessJobs();
        }
        else
        {
            generatePreprocessJobs();
            Run run = createRun(null);
            int generations = Integer.parseInt(run.getProperty(GenetikConstants.GENERATIONS));
            
            generatePreprocessJobs(run);
            
            for(int i=0;i<generations;i++)
            {
                if(i==0) generateInitialBuildJobs(run,null);
                else generateBuildJobs(run,i);
                
                generateScoreJobs(run,i);
                generateCollectAndScaleJobs(run,i);
            }
            
            generatePostProcessJobs(run);
            generatePostProcessJobs();
        }
        
        int jobsScheduled = (isExperimentComplete())?0:scheduleJobs(maxJobs);
        return jobsScheduled;
    }
    
    public Properties getProperties()
    {
        return props;
    }
}
