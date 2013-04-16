package com.sasbury.genetik.driver.cluster.jobs;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.reporting.*;
import com.sasbury.util.json.*;

public class PostProcessRun extends ClusterJob
{
    @SuppressWarnings("unchecked")
    public void execute() throws Exception
    {
        PostProcessor postprocessor = null;
        
        if(run.getProperty(GenetikConstants.POSTPROCESSOR)!=null)
        {
            postprocessor = (PostProcessor) Run.createObjectForClassName(run.getProperty(GenetikConstants.POSTPROCESSOR));
        }
        
        if(postprocessor != null)
        {
            postprocessor.postprocess(getRun());
        }
        
        String statsJson = getRunData(getRun(),"statistics.json");
        HashMap<String,Object> jsonStats = null;
        
        if(statsJson != null)
        {
            jsonStats = (new JSONDecoder()).decode(statsJson);
            
            ArrayList<HashMap<String,Object>> generationalStatistics = (ArrayList<HashMap<String,Object>>)jsonStats.get(GenerationalStats.GENERATION_STATISTICS);
            HashMap<String,Object> runStats = stats.generateRunStatistics(generationalStatistics);

            runStats.put(GenerationalStats.RUN_NAME, run.getName());
            runStats.put(GenetikConstants.OPTIMIZER, run.getProperty(GenetikConstants.OPTIMIZER));
            
            jsonStats.put(GenerationalStats.RUN_STATISTICS, runStats);
            
            try
            {
                JSONEncoder encoder = new JSONEncoder();
                String json = encoder.encode(jsonStats);
                saveRunData(getRun(), "statistics.json", json);
            }
            catch(Exception exp)
            {
                throw new RuntimeException("Error saving run stats.",exp);
            }
        }
        
        //Clean up tmp files
        try
        {
            File runDir = getRunDir(run);
            File files[] = runDir.listFiles();
            
            for(File f : files)
            {
                String nm = f.getName();
                
                if(nm.startsWith("build_stats.") && nm.endsWith(".json"))
                {
                    f.delete();
                }
                else if(nm.startsWith("chunk_stats.") && nm.endsWith(".json"))
                {
                    f.delete();
                }
                else if(nm.endsWith(".log"))
                {
                    if(f.length() == 0) f.delete();
                }
                else if(nm.startsWith("pbs.") && nm.endsWith(".err"))
                {
                    if(f.length() == 0) f.delete();
                }
                else if(nm.startsWith("pbs.") && nm.endsWith(".out"))
                {
                    if(f.length() == 0) f.delete();
                }
                else if(nm.endsWith(".sh"))
                {
                    f.delete();
                }
            }
        }
        catch(Exception exp)
        {
            Logger.getLogger(GenetikConstants.LOGGER).warning("Error cleaning up run directory in post processing.");
        }

        ReportingSupport.saveReportingFiles(properties, root);
    }

    public File getLogFile()
    {
        return new File((getRun()!=null)?getRunDir(getRun()):root,"postprocess.log");
    }
}