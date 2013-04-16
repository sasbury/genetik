package com.sasbury.genetik.driver.cluster.jobs;

import java.io.*;
import java.util.*;

import com.sasbury.genetik.driver.*;
import com.sasbury.util.json.*;

/**
 * Cluster job driver is intended to be executed on the file system that the jobs will have access to
 * and given the root folder for the job so that the run properties can be saved there.
 *
 */
public abstract class ClusterJobDriver extends JobBasedGenerationalDriver
{
    protected File root;
    
    public ClusterJobDriver(File root)
    {
        this.root = root;
        
        if(!this.root.exists()) throw new IllegalArgumentException("Invalid root folder.");
    }
    
    /**
     * Saves the props to disk in run.json then calls super.
     */
    public int execute(Properties props)
    {
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
        
        return super.execute(props);
    }
}
