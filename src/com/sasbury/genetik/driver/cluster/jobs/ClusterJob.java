package com.sasbury.genetik.driver.cluster.jobs;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;
import com.sasbury.genetik.reporting.*;
import com.sasbury.util.*;
import com.sasbury.util.json.*;

public abstract class ClusterJob implements RunContext
{
    //Job types
    public static final String PREPROCESS="preprocess";
    public static final String PREPROCESS_RUN="preprocess_run";
    public static final String BUILD="build";
    public static final String SCORE="score";
    public static final String COLLECT="collect";
    public static final String POSTPROCESS_RUN="postprocess_run";
    public static final String POSTPROCESS="postprocess";
    
    /**
     * Command line arg indicating generation for this job
     */
    public static final String GENERATION="generation";
    
    /**
     * Command line arg, used for scoring, to indicate which chunk to score
     */
    public static final String CHUNK="chunk";
    
    protected File root;
    protected Run run;
    protected Run previousRun;
    protected GenerationalStats stats;
    protected HashMap<String,String> args;
    protected Properties properties;
    
    public ClusterJob()
    {
    }
    
    protected void initialize(File rootFolder,String runName) throws Exception
    {
        if(rootFolder == null || !rootFolder.exists()) throw new IllegalArgumentException("Valid Root folder is required.");
        root = rootFolder;

        File runJson = new File(rootFolder,"run.json");
        JSONDecoder decoder = new JSONDecoder();
        BufferedReader read = new BufferedReader(new FileReader(runJson));
        HashMap<String,Object> json = decoder.decode(read);
        read.close();

        properties = new Properties();
        for(String key : json.keySet())
        {
            properties.setProperty(key,(String)json.get(key));
        }
        
        if(runName != null)
        {
            String runStr = properties.get(GenetikConstants.RUNS).toString();
            String defNames[] = {Run.DEFAULT_NAME};
            String runNames[] = (runStr == null)?defNames:runStr.split(",");
            List<String> names = Arrays.asList(runNames);
            int index = names.indexOf(runName);
            
            if(index > 0)
            {
                previousRun = new Run(properties,names.get(index-1),this);
            }
            
            this.stats = new GenerationalStats();
            this.run = new Run(properties,runName,this);
        }
        
        File logFile = getLogFile();
        
        try
        {
            SimpleFileHandler fh = new SimpleFileHandler(logFile);
            fh.setFormatter(new CompactFormatter());
            
            Logger logger = Logger.getLogger(GenetikConstants.LOGGER);
            logger.setUseParentHandlers(false);
            Handler handlers[] = logger.getHandlers();
            
            logger.addHandler(fh);
            
            for(Handler h : handlers)
            {
                logger.removeHandler(h);
            }
        }
        catch(Exception exp)
        {
            throw new IllegalArgumentException("Unable to create log file at "+logFile.getAbsolutePath());
        }
    }
    
    public abstract File getLogFile();

    public File getRunDir(Run run)
    {
        String runName = run.getName();
        if(runName == null) throw new IllegalArgumentException("Run doesn't have a name.");
        
        File runDir = new File(root,runName);
        if(!runDir.exists()) runDir.mkdir();
        
        return runDir;
    }
    
    public File getGenerationFileFor(Run run, int generation)
    {
        File runDir = getRunDir(run);
        String ext = ".json";
        return new File(runDir,"generation."+generation+ext);
    }
    
    public File getChunkFileFor(Run run, int generation,int chunk)
    {
        File runDir = getRunDir(run);
        String ext = ".json";
        return new File(runDir,"chunk."+generation+"."+chunk+ext);
    }
    
    public Population createEmptyPopulation()
    {
        return new InMemoryPopulation();
    }
    
    public Population getPopulationFor(Run run, int generation)
    {
        File file = getGenerationFileFor(run, generation);
        Population pop = null;
        
        if(file!=null)
        {
            pop = JSONPopulationCoder.decode(file, run);
        }
        else
        {
            pop = new InMemoryPopulation();
        }
        
        return pop;
    }

    public void savePopulationFor(Population pop, Run run, int generation)
    {
        File file = getGenerationFileFor(run, generation);
        
        if(file!=null)
        {
            JSONPopulationCoder.encode(pop, file);
        }
    }
    
    public void disposeOfPopulation(Population pop)
    {
        //We are using in memory pops, so dispose them to help reduce memory
        if(pop instanceof InMemoryPopulation) ((InMemoryPopulation)pop).dispose();
    }
    
    public String getData(String name)
    {
        File file = new File(root,name);
        String retVal = null;
        
        try
        {
            if(file.exists())
            {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                StringBuilder builder = new StringBuilder();
                String line = null;
                
                while((line = reader.readLine())!=null)
                {
                    builder.append(line);
                }
                
                reader.close();
                retVal = builder.toString();
            }
        }
        catch(Exception exp)
        {
            throw new IllegalArgumentException("Error trying to get data from "+file.getAbsolutePath(), exp);
        }
        return retVal;
    }

    public String getRunData(Run run, String name)
    {
        File file = new File(getRunDir(run),name);
        String retVal = null;
        
        try
        {
            if(file.exists())
            {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                StringBuilder builder = new StringBuilder();
                String line = null;
                
                while((line = reader.readLine())!=null)
                {
                    builder.append(line);
                }
                
                reader.close();
                retVal = builder.toString();
            }
        }
        catch(Exception exp)
        {
            throw new IllegalArgumentException("Error trying to get data from "+file.getAbsolutePath(), exp);
        }
        return retVal;
    }

    public void saveData(String name, String data)
    {
        File file = new File(root,name);
        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(data);
            writer.close();
        }
        catch(Exception exp)
        {
            throw new IllegalArgumentException("Error trying to save data to "+file.getAbsolutePath(), exp);
        }
    }

    public void saveRunData(Run run, String name, String data)
    {
        File file = new File(getRunDir(run),name);
        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(data);
            writer.close();
        }
        catch(Exception exp)
        {
            throw new IllegalArgumentException("Error trying to save data to "+file.getAbsolutePath(), exp);
        }
    }
    
    public int getIntArg(String argName,int defaultValue)
    {
        String val = this.args.get(argName);
        int retVal = defaultValue;
        
        if(val != null)
        {
            try
            {
                retVal = Integer.parseInt(val);
            }
            catch(Exception exp)
            {
                retVal = defaultValue;
            }
        }
        
        return retVal;
    }
    
    public String getArg(String argName)
    {
        return args.get(argName);
    }

    public Run getPreviousRun()
    {
        return previousRun;
    }

    public Run getRun()
    {
        return run;
    }
    
    public Statistics getStats()
    {
        return stats;
    }

    public abstract void execute() throws Exception;
    
    public static void main(String args[])
    {
        try
        {
            String pwdPath = System.getProperty("user.dir");
            File pwd = new File(pwdPath);
            String runName = pwd.getName();
            String jobType = args[0];
            ClusterJob job = null;

            if(jobType.equals(PREPROCESS_RUN)) job = new PreProcessRun();
            else if(jobType.equals(BUILD)) job = new Build();
            else if(jobType.equals(SCORE)) job = new Score();
            else if(jobType.equals(COLLECT)) job = new CollectAndScale();
            else if(jobType.equals(POSTPROCESS_RUN)) job = new PostProcessRun();

            
            job.args = new HashMap<String,String>();
            
            for(int i=1;i<args.length;i++)
            {
                String argStr = args[i];
                String kv[] = argStr.split("=");
                
                if(kv.length == 2) job.args.put(kv[0], kv[1]);
                else job.args.put(kv[0],kv[0]);
            }
                
            job.initialize(pwd.getParentFile(), runName);
            job.execute();
            
        }
        catch(Exception exp)
        {
            Logger.getLogger(GenetikConstants.LOGGER).log(Level.SEVERE,"Error in ClusterJob.",exp);
        }
    }
}
