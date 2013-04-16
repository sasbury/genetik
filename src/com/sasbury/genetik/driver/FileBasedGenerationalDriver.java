package com.sasbury.genetik.driver;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import com.sasbury.genetik.*;
import com.sasbury.genetik.population.*;
import com.sasbury.genetik.reporting.*;
import com.sasbury.util.*;
import com.sasbury.util.json.*;

public class FileBasedGenerationalDriver extends SimpleGenerationalDriver
{
    public static final int XML_MODE=0;
    public static final int JSON_MODE=1;//the default
    
    protected File root;
    protected int mode;
    
    public FileBasedGenerationalDriver(File rootFolder)
    {
        super();
        
        if(rootFolder == null || !rootFolder.exists()) throw new IllegalArgumentException("Valid Root folder is required "+rootFolder);
        root = rootFolder;
        mode = JSON_MODE;
    }
    
    @Override
    public void execute(Properties props)
    {
        super.execute(props);
        
        HashMap<String,Object> jsonObj = new HashMap<String,Object>();
        for(Object k : props.keySet())
        {
            String key = (String)k;
            String value = props.getProperty(key);
            jsonObj.put(key, value);
        }
        
        try
        {
            File runFile = new File(root,"run.json");
            JSONEncoder encoder = new JSONEncoder();
            BufferedWriter writer = new BufferedWriter(new FileWriter(runFile));
            encoder.encode(jsonObj, writer);
            writer.close();
        }
        catch(Exception exp)
        {
            throw new IllegalArgumentException("Unable to save run properties in root folder.");
        }
    }

    public void preprocess(Run run)
    {
        File logFile = new File(getRunDir(run),"genetik.log");
        
        try
        {
            SimpleFileHandler fh = new SimpleFileHandler(logFile);
            fh.setFormatter(new CompactFormatter());
            
            Logger logger = Logger.getLogger(GenetikConstants.LOGGER);
            logger.setLevel(Level.INFO);
            logger.setUseParentHandlers(false);
            Handler handlers[] = logger.getHandlers();
            
            logger.addHandler(fh);
            
            for(Handler h : handlers)
            {
                logger.removeHandler(h);
                
                if(h instanceof SimpleFileHandler) h.close();//close our old one
            }
        }
        catch(Exception exp)
        {
            throw new IllegalArgumentException("Unable to create log file at "+logFile.getAbsolutePath());
        }
        
        super.preprocess(run);
    }

    public void setMode(int mode)
    {
        this.mode = mode;
    }
    
    public Population createEmptyPopulation()
    {
        return new InMemoryPopulation();
    }
    
    @Override
    public void disposeOfPopulation(Population pop)
    {
        //We are using in memory pops, so dispose them to help reduce memory
        if(pop instanceof InMemoryPopulation) ((InMemoryPopulation)pop).dispose();
    }

    public File getRunDir(Run run)
    {
        String runName = run.getName();
        if(runName == null) throw new IllegalArgumentException("Run doesn't have a name.");
        
        File runDir = new File(root,runName);
        
        if(!runDir.exists()) runDir.mkdir();
        
        return runDir;
    }
    
    public boolean needsToRun(Run run, int generation)
    {
        Population pop = getPopulationFor(run, generation);
        int count = run.getIntProperty(GenetikConstants.POPULATION, Integer.MAX_VALUE, true);
        int scoreCount = 0;
    
        for(int i=0,max=pop.size();i<max;i++)
        {
            Individual ind = pop.get(i);
            
            if(ind.hasFitness() && ind.hasScores())
            {
                scoreCount++;
            }
        }
        
        return scoreCount != count;
    }

    public File getFileFor(Run run, int generation)
    {
        File runDir = getRunDir(run);
        String ext = (mode==JSON_MODE)?".json":".xml";
        return new File(runDir,"generation."+generation+ext);
    }

    public Population getPopulationFor(Run run, int generation)
    {
        File file = getFileFor(run, generation);
        Population pop = null;
        
        if(file!=null)
        {
            if(mode == JSON_MODE) pop = JSONPopulationCoder.decode(file, run);
            else pop = XMLPopulationCoder.decode(file, run);
        }
        else pop = new InMemoryPopulation();
        
        return pop;
    }

    public void savePopulationFor(Population pop, Run run, int generation)
    {
        File file = getFileFor(run, generation);
        
        if(file!=null)
        {
            if(mode==JSON_MODE) JSONPopulationCoder.encode(pop, file);
            else XMLPopulationCoder.encode(pop, file);
        }
    }

    public String getData(String name)
    {
        File file = new File(root,name);
        String retVal = null;
        
        if(!file.exists()) return null;
        
        try
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
        catch(Exception exp)
        {
            throw new IllegalArgumentException("Error trying to read data at "+file.getAbsolutePath(), exp);
        }
        return retVal;
    }

    public String getRunData(Run run, String name)
    {
        File file = new File(getRunDir(run),name);
        String retVal = null;
        
        if(!file.exists()) return null;
        
        try
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
        catch(Exception exp)
        {
            throw new IllegalArgumentException("Error trying to read data at "+file.getAbsolutePath(), exp);
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
    
    public void postprocess()
    {
        super.postprocess();
        ReportingSupport.saveReportingFiles(getProperties(), root);
    }
}
