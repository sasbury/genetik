package com.sasbury.genetik.driver.cluster.pbs;

import java.io.*;
import java.util.*;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.driver.cluster.jobs.*;

public class PBSJobDriver extends ClusterJobDriver
{
    /**
     * The estimated time for a scoring job
     */
    public static final String EST_SCORE_TIME="estimated_score_time";
    
    /**
     * The estimated time for all other jobs
     */
    public static final String EST_JOB_TIME="estimated_job_time";

    /**
     * The class path for the cluster jobs, generally a colon-separated list of jars,
     * should include the genetik jar as well as any for custom fitness functions.
     */
    public static final String CLASS_PATH="class_path";
    
    /**
     * A property containing the email address to notify when either max jobs are run, or 
     * the experiment finishes.
     */
    public static final String EMAIL="email";
    
    /**
     * The id for the cluster that will appear on job ids and can be stripped off.
     */
    public static final String CLUSTER_ID="cluster_id";
    
    public static final String SCORE_NODES="scoring_nodes";
    public static final String JOB_NODES="job_nodes";
    
    public static final String SCORE_PPN="scoring_ppn";
    public static final String JOB_PPN="job_ppn";

    public static final String JOB_QUEUE="job_queue";
    public static final String JOB_QOS="job_qos";
    
    public static final String ACCOUNT="account";

    public PBSJobDriver(File root)
    {
        super(root);
    }

    public File getRunDir(Run run)
    {
        String runName = run.getName();
        if(runName == null) throw new IllegalArgumentException("Run doesn't have a name.");
        
        File runDir = new File(root,runName);
        
        if(!runDir.exists()) runDir.mkdir();
        
        return runDir;
    }

    public void generateBuildJobs(Run run, int generation)
    {
        PBSJobInfo info = new PBSJobInfo(run,ClusterJob.BUILD, getRunDir(run), generation,0);
        info.addDependency((PBSJobInfo)jobs.get(jobs.size()-1));//build depends on previous job
        jobs.add(info);
    }

    public void generateInitialBuildJobs(Run run, Run previousRun)
    {
        PBSJobInfo info = new PBSJobInfo(run,ClusterJob.BUILD, getRunDir(run), 0,0);
        if(jobs.size()>0) info.addDependency((PBSJobInfo)jobs.get(jobs.size()-1));//build depends on previous job
        jobs.add(info);
    }

    public void generateCollectAndScaleJobs(Run run, int generation)
    {
        PBSJobInfo info = new PBSJobInfo(run,ClusterJob.COLLECT, getRunDir(run), generation,0);
        info.addDependency((PBSJobInfo)jobs.get(jobs.size()-1));//collect depends on previous join job which should group up the previous joins/scores
        jobs.add(info);
    }

    public void generatePreprocessJobs()
    {
        //unusued
    }
    
    public void generatePostProcessJobs()
    { 
    }

    public void generatePostProcessJobs(Run run)
    {
        int generation = run.getIntProperty(GenetikConstants.GENERATIONS, 0, false);//post process goes to last gen
        PBSJobInfo info = new PBSJobInfo(run,ClusterJob.POSTPROCESS_RUN, getRunDir(run), generation-1,0);
        info.addDependency((PBSJobInfo)jobs.get(jobs.size()-1));//post process depends on previous collect job
        jobs.add(info);
    }

    public void generatePreprocessJobs(Run run)
    {
        PBSJobInfo info = new PBSJobInfo(run,ClusterJob.PREPROCESS_RUN, getRunDir(run), 0,0);
        if(jobs.size()>0) info.addDependency((PBSJobInfo)jobs.get(jobs.size()-1));//preprocess depends on previous job,if it isn't first run in set
        jobs.add(info);
    }

    /**
     * Build all of the scoring jobs, for a single generation, as chunks, and include join jobs as needed
     * Joins don't use the absolutely most efficient algorithm, but are minimized reasonably
     */
    public void generateScoreJobs(Run run, int generation)
    {
        PBSJobInfo buildJob = (PBSJobInfo)jobs.get(jobs.size()-1);//score depends on previous build job
        int popSize = run.getIntProperty(GenetikConstants.POPULATION, 0, false);
        int chunk = run.getIntProperty(ClusterJobDriver.SCORING_CHUNK_SIZE, ClusterJobDriver.DEFAULT_SCORING_CHUNK_SIZE, true);
        int chunks = (popSize%chunk == 0) ? popSize/chunk : (popSize/chunk) + 1;
        ArrayList<PBSJobInfo> toJoin = new ArrayList<PBSJobInfo>();
        ArrayList<PBSJobInfo> joins = new ArrayList<PBSJobInfo>();
        File runDir = getRunDir(run);
        
        //Build score jobs and put them in, but don't schedule join jobs until all the score jobs are
        //done, otherwise we could block later scoring jobs, or at least confuse the scheduler
        for(int i=0;i<chunks;i++)
        {
            PBSJobInfo info = new PBSJobInfo(run,ClusterJob.SCORE, runDir,generation,i);
            info.addDependency(buildJob);
            jobs.add(info);
            toJoin.add(info);
            
            if(toJoin.size() >= 8)//insert a join every 8 scores
            {
                PBSJobInfo joinInfo = new PBSJobInfo(run, PBSJobInfo.JOIN_TYPE, getRunDir(run), generation, 0);
                for(PBSJobInfo j : toJoin)
                {
                    joinInfo.addDependency(j);
                }
                joins.add(joinInfo);
                toJoin.clear();
            }
        }
        
        if(toJoin.size() > 0)//insert a join
        {
            PBSJobInfo joinInfo = new PBSJobInfo(run, PBSJobInfo.JOIN_TYPE, getRunDir(run), generation, 0);
            for(PBSJobInfo j : toJoin)
            {
                joinInfo.addDependency(j);
            }
            joins.add(joinInfo);
            toJoin.clear();
        }
        
        //is it ok to chain these in line, it still might confuse the scheduler?
        //this would only happen after 64 scoring jobs
        for(PBSJobInfo jn : joins)
        {
            jobs.add(jn);
            toJoin.add(jn);
            
            if(toJoin.size() >= 8)
            {
                PBSJobInfo joinInfo = new PBSJobInfo(run, PBSJobInfo.JOIN_TYPE, getRunDir(run), generation, 0);
                for(PBSJobInfo j : toJoin)
                {
                    joinInfo.addDependency(j);
                }
                jobs.add(joinInfo);
                toJoin.clear();
            }
        }
        
        if(toJoin.size() > 0)//insert a join at then end so we have a single dependency for the collect/scale job
        {
            PBSJobInfo joinInfo = new PBSJobInfo(run, PBSJobInfo.JOIN_TYPE, getRunDir(run), generation, 0);
            for(PBSJobInfo j : toJoin)
            {
                joinInfo.addDependency(j);
            }
            jobs.add(joinInfo);
            toJoin.clear();
        }
    }

    public boolean isExperimentComplete()
    {
        boolean retVal = false;
        
        for(Object o : jobs)
        {
            PBSJobInfo job = (PBSJobInfo)o;
            if(ClusterJob.POSTPROCESS_RUN.equals(job.getType()))
            {
                if(job.hasRun())
                {
                    retVal = true;
                }
                else
                {
                    retVal = false;//stop with false if any post process isn't done
                    break;
                }
            }
        }
        
        return retVal;
    }

    public int scheduleJobs(int maxJobs)
    {
        int badCollect = -1;
        PBSJobInfo lastJob = null;
        int jobsScheduled = 0;
        
        //find first incomplete collect job
        for(int i=0,max=jobs.size();i<max;i++)
        {
            PBSJobInfo job = (PBSJobInfo)jobs.get(i);
            if(ClusterJob.COLLECT.equals(job.getType()))
            {
                if(!job.hasRun())
                {
                    badCollect = i;//could be the very first job if nothing has run
                    break;
                }
            }
        }
        
        if(badCollect >= 0)
        {
            //roll back to the build for that collect
            int start = badCollect-1;
            while(start>0)
            {
                PBSJobInfo job = (PBSJobInfo)jobs.get(start);
                if(ClusterJob.BUILD.equals(job.getType()))
                {
                    //clean up bad gen file if necessary
                    File genFile = new File(job.getRunDir(),"generation."+job.getGeneration()+".json");
                    if(genFile.exists()) genFile.delete();
                    break;
                }
                start--; 
            }
            
            boolean didOne = false;
            
            if(start == 1) start = 0;//if we are at the first build job, just start from 0, we haven't done this experiment yet
            
            for(int i=start,max = jobs.size();i<max;i++)
            {
                PBSJobInfo job = (PBSJobInfo)jobs.get(i);
                
                //check if we are going to go over max, but not the first time
                if(ClusterJob.BUILD.equals(job.getType()) && didOne)
                {
                    int generationJobs = 0;
                    //recalculate this each time since it could change between runs
                    for(int j=i,maxj=max;j<maxj;j++)
                    {
                        PBSJobInfo jb = (PBSJobInfo)jobs.get(j);
                        generationJobs++;
                        if(ClusterJob.COLLECT.equals(jb.getType()))
                        {
                            break;
                        }
                    }
                    
                    if(maxJobs>0 && ((i - start + generationJobs) > maxJobs))
                    {
                        //will reach the max jobs before we get to collect, break now so we can start on a build next time
                        break;
                    }
                }
                else if(ClusterJob.COLLECT.equals(job.getType()))
                {
                    didOne = true;
                }
                
                lastJob = scheduleJob(job,i);
                jobsScheduled++;
            }
        }
        else if(jobs.size()>0)//we had some good collect jobs, so we must have had all good since there were no bad ones
        {
            //if all collect have run, check that last post process ran
            PBSJobInfo job = (PBSJobInfo)jobs.get(jobs.size()-1);
            if(!job.hasRun())
            {
                lastJob = scheduleJob(job,jobs.size()-1);
                jobsScheduled++;
            }
        }
        
        String email = props.getProperty(EMAIL);
        if(lastJob!=null && email!=null)//schedule an email job
        {
            PBSJobInfo emailJob= new PBSJobInfo(lastJob.getRun(), PBSJobInfo.NOTIFY_TYPE, lastJob.getRunDir(), lastJob.getGeneration(),0);
            emailJob.addDependency(lastJob);
            emailJob.setMailTarget(email);
            scheduleJob(emailJob,jobs.size());//number it past everything
            jobsScheduled++;
        }
        
        return jobsScheduled;
    }
    
    public PBSJobInfo scheduleJob(PBSJobInfo job,int index)
    {
        File runDir = job.getRunDir();
        String clusterId = job.getRun().getProperty(CLUSTER_ID);
        
        //calculate script file name
        String indexStr = null;
        
        if(index<10) indexStr = "00"+index;
        else if(index<100) indexStr = "0" + String.valueOf(index);
        else indexStr = String.valueOf(index);
        
        File scriptFile = new File(runDir,""+indexStr+"_"+job.getType()+".sh");
        String sfId = scriptFile.getName().replace(".sh", "");
        String outFile = (new File(runDir,"pbs."+sfId+".out")).getAbsolutePath();
        String errFile = (new File(runDir,"pbs."+sfId+".err")).getAbsolutePath();

        //save the script to disk
        job.saveScriptToFile(scriptFile);
        
        //execute the command - getting the job id
        String cmdLine = "qsub -e "+errFile+" -o "+outFile+" "+scriptFile.getAbsolutePath();

        //testing
        if(Boolean.getBoolean("pbs.test.cmdline.mode"))
        {
            System.out.println(cmdLine);
            job.setJobId("job_"+indexStr+"_"+clusterId);
            cmdLine = "bash "+scriptFile.getAbsolutePath();
            try
            {
                Process proc = Runtime.getRuntime().exec(cmdLine);
                proc.waitFor();
            }
            catch(Exception exp)
            {
                exp.printStackTrace();
                System.exit(0);
            }
            return job;
        }
        else if(Boolean.getBoolean("pbs.test.mode"))
        {
            job.runInDebugMode();
            return job;
        }
        
        try
        {
            Process p = Runtime.getRuntime().exec(cmdLine);
            StringBuilder buff = new StringBuilder();
            String s;
            String jobId;
            
            BufferedReader stdOutput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            while ((s = stdOutput.readLine()) != null)
            {
                buff.append(s);
            }
            
            jobId = buff.toString();
            
            if(jobId.length()==0)
            {
                while ((s = stdError.readLine()) != null)
                {
                    buff.append(s);
                }
                jobId = buff.toString();
            }
            
            if(jobId != null && jobId.length()>0)
            {
                if(clusterId != null)
                {
                    int ind = jobId.indexOf(clusterId);
                    if(ind>0)
                    {
                        //set the job id on the info so later jobs can depend on it
                        jobId = jobId.substring(0,ind);
                        job.setJobId(jobId);
                    }
                }
                else
                {
                    job.setJobId(jobId);
                }
            }
        }
        catch (IOException e)
        {
            throw new IllegalArgumentException("Unable to schedule job", e);
        }
        
        return job;
    }
}
