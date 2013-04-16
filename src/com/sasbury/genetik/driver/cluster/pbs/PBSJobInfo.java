package com.sasbury.genetik.driver.cluster.pbs;

import java.io.*;
import java.util.*;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.driver.cluster.jobs.*;
import com.sasbury.genetik.population.*;
import com.sasbury.genetik.reporting.*;
import com.sasbury.util.*;
import com.sasbury.util.json.*;

public class PBSJobInfo
{
    /**
     * Template Keys
     */
    public static final String JOB_TYPE="JOB_TYPE";
    public static final String RUN_NAME="RUN_NAME";
    public static final String RUN_DIR="RUN_DIR";
    public static final String TIME="TIME";
    public static final String DEPENDS="DEPENDS";
    public static final String CLASS_PATH="CLASS_PATH";
    public static final String ARGS="ARGS";
    public static final String PROCS="PROCS";
    public static final String NODES="NODES";
    public static final String PPN="PPN";
    public static final String GENERATION="GENERATION";
    public static final String CHUNK="CHUNK";
    public static final String MAIL="MAIL";
    public static final String MAIL_FLAG="MAIL_FLAG";
    public static final String QUEUE="QUEUE";
    public static final String QOS="QOS";
    public static final String ACCOUNT="ACCOUNT";

    public static final String JOIN_TYPE="join";
    public static final String NOTIFY_TYPE="notify";
    
    protected HashMap<String,String> templateValues;
    protected Run run;
    protected File runDir;
    protected String type;
    protected int generation;
    protected int chunk;
    protected String jobId;
    protected ArrayList<PBSJobInfo> dependencies;

    protected static final String template;
    protected static final String joinTemplate;
    protected static final String notifyTemplate;
    
    static
    {
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(PBSJobInfo.class.getResourceAsStream("/com/sasbury/genetik/driver/cluster/pbs/genetik_pbs.sh")));
            StringBuilder builder = new StringBuilder();
            String line = null;
            
            while((line = reader.readLine())!=null)
            {
                builder.append(line);
                builder.append("\n");
            }
            
            reader.close();
            template = builder.toString();

            reader = new BufferedReader(new InputStreamReader(PBSJobInfo.class.getResourceAsStream("/com/sasbury/genetik/driver/cluster/pbs/genetik_pbs_join.sh")));
            builder = new StringBuilder();
            line = null;
            
            while((line = reader.readLine())!=null)
            {
                builder.append(line);
                builder.append("\n");
            }
            
            reader.close();
            joinTemplate = builder.toString();

            reader = new BufferedReader(new InputStreamReader(PBSJobInfo.class.getResourceAsStream("/com/sasbury/genetik/driver/cluster/pbs/genetik_pbs_notify.sh")));
            builder = new StringBuilder();
            line = null;
            
            while((line = reader.readLine())!=null)
            {
                builder.append(line);
                builder.append("\n");
            }
            
            reader.close();
            notifyTemplate = builder.toString();
        }
        catch(Exception exp)
        {
            throw new IllegalArgumentException("Unable to load nyx script file.");
        }
    }
    
    public PBSJobInfo(Run run,String jobType,File runDir,int generation,int chunk)
    {
        String acct = run.getProperty(PBSJobDriver.ACCOUNT);
        
        this.run = run;
        this.runDir = runDir;
        this.type = jobType;
        this.generation = generation;
        this.chunk = chunk;
        
        dependencies = new ArrayList<PBSJobInfo>();
        templateValues = new HashMap<String,String>();
        
        templateValues.put(JOB_TYPE, jobType);
        templateValues.put(RUN_NAME,run.getName());
        templateValues.put(RUN_DIR,runDir.getAbsolutePath());
        templateValues.put(GENERATION,String.valueOf(generation));
        templateValues.put(CHUNK,String.valueOf(chunk));
        templateValues.put(MAIL_FLAG, "PBS -m n");//default to no mail
        
        if(jobType.equals(ClusterJob.SCORE)) templateValues.put(TIME,run.getProperty(PBSJobDriver.EST_SCORE_TIME));
        else templateValues.put(TIME,run.getProperty(PBSJobDriver.EST_JOB_TIME));

        if(jobType.equals(ClusterJob.SCORE)) templateValues.put(NODES,run.getPropertyWithDefault(PBSJobDriver.SCORE_NODES,"1"));
        else templateValues.put(NODES,run.getPropertyWithDefault(PBSJobDriver.JOB_NODES,"1"));

        if(jobType.equals(ClusterJob.SCORE)) templateValues.put(PPN,run.getPropertyWithDefault(PBSJobDriver.SCORE_PPN,"1"));
        else templateValues.put(PPN,run.getPropertyWithDefault(PBSJobDriver.JOB_PPN,"1"));

        templateValues.put(QUEUE, run.getPropertyWithDefault(PBSJobDriver.JOB_QUEUE, "route"));
        templateValues.put(QOS, run.getPropertyWithDefault(PBSJobDriver.JOB_QOS, acct));
        
        if(acct!=null) templateValues.put(ACCOUNT,"PBS -A "+acct);
        
        templateValues.put(CLASS_PATH,run.getProperty(PBSJobDriver.CLASS_PATH));
    }
    
    public void addDependency(PBSJobInfo job)
    {
        dependencies.add(job);
    }
    
    public void calculateDepends()
    {
        if(dependencies.size()==0) return;//no depends
        
        boolean gotOne = false;
        StringBuilder depends = new StringBuilder();
        
        //Join can go after score regardless of success, others require success
        if(PBSJobInfo.JOIN_TYPE.equals(type) || PBSJobInfo.NOTIFY_TYPE.equals(type)) depends.append("PBS -W depend=afterany");//no colon so we can add later
        else depends.append("PBS -W depend=afterok");//no colon so we can add later
        
        for(PBSJobInfo job : dependencies)
        {
            String jobId = job.getJobId();
            
            if(jobId != null && jobId.length()>0)
            {
                depends.append(":");
                depends.append(jobId);
                gotOne = true;
            }
        }
        
        if(gotOne) templateValues.put(DEPENDS,depends.toString());
    }
    
    public boolean hasRun()
    {
        boolean retVal = false;
        File genFile = new File(runDir,"generation."+generation+".json");
        int popSize = run.getIntProperty(GenetikConstants.POPULATION, 0, false);
        
        if(genFile.exists())
        {
            Population pop = JSONPopulationCoder.decode(genFile, run);
            
            if(pop.size() == popSize)
            {
                Individual ind = pop.get(popSize-1);
                if(ind.getFitness() != Integer.MIN_VALUE)
                {
                    retVal = true;
                }
            }
        }
        
        if(retVal && ClusterJob.POSTPROCESS_RUN.equals(type))
        {
            File stats = new File(runDir, "statistics.json");
            try
            {
                BufferedReader reader = new BufferedReader(new FileReader(stats));
                StringBuilder builder = new StringBuilder();
                String line = null;
                
                while((line = reader.readLine())!=null)
                {
                    builder.append(line);
                }
                
                reader.close();
                String json = builder.toString();
                JSONDecoder decoder = new JSONDecoder();
                HashMap<String,Object> jsonObj = decoder.decode(json);
                
                Object test = jsonObj.get(GenerationalStats.RUN_STATISTICS);
                
                if(test == null) retVal = false;//gen done but stats aren't
            }
            catch(Exception exp)
            {
                throw new IllegalArgumentException("Error trying to check stats at "+stats.getAbsolutePath(), exp);
            }
        }
        
        return retVal;
    }
    
    public void runInDebugMode()
    {
        String runDir = templateValues.get(RUN_DIR);
        String generation = templateValues.get(GENERATION);
        String chunk = templateValues.get(CHUNK);
        String args[] = {type,"generation="+generation,"chunk="+chunk};
        
        System.setProperty("user.dir", runDir);
        
        if(!JOIN_TYPE.equals(type) && !NOTIFY_TYPE.equals(type))
        {
            ClusterJob.main(args);
        }
    }
    
    public void saveScriptToFile(File file)
    {
        calculateDepends();
        String text;
        
        templateValues.put(RUN_NAME, run.getName()+"_"+file.getName().replace(".sh",""));
        
        if(JOIN_TYPE.equals(type))
        {
            text = Templating.templatize(joinTemplate, templateValues);
        }
        else if(NOTIFY_TYPE.equals(type))
        {
            text = Templating.templatize(notifyTemplate, templateValues);
        }
        else
        {
            text = Templating.templatize(template, templateValues);
        }
        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(text);
            writer.close();
        }
        catch(Exception exp)
        {
            throw new IllegalArgumentException("Error trying to save script to "+file.getAbsolutePath(), exp);
        }
    }

    public String getType()
    {
        return type;
    }

    public Run getRun()
    {
        return run;
    }

    public File getRunDir()
    {
        return runDir;
    }

    public int getGeneration()
    {
        return generation;
    }

    public String getJobId()
    {
        return jobId;
    }

    public void setJobId(String jobId)
    {
        this.jobId = jobId;
    }
    
    public void setMailTarget(String email)
    {
        if(email!=null)
        {
            templateValues.put(MAIL, "PBS -M "+email);
            templateValues.put(MAIL_FLAG, "PBS -m ae");//add flag to get mail
        }
    }
}
