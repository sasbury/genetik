package com.sasbury.genetik.driver.cluster.pbs;

import java.io.*;
import java.util.*;

import com.sasbury.genetik.driver.*;

public class PBSGenetik
{
    public static void main(String args[])
    {
        try
        {
            if(args.length<1) usage();
            
            File propsFile = new File(args[0]);
            
            if(!propsFile.exists())
            {
                System.out.println("Missing Properties File: "+propsFile.getAbsolutePath());
                usage();
            }
            
            propsFile = new File(propsFile.getAbsolutePath());
            
            Properties props = Run.createDefaultProperties();
            BufferedInputStream reader = new BufferedInputStream(new FileInputStream(propsFile));
            props.load(reader);
            reader.close();
            
            PBSValidator validator = new PBSValidator();
            String[] issues = validator.validate(props);
            
            if(issues.length>0)
            {
                for(String s : issues)
                {
                    System.out.println(s);
                }
                
                System.exit(1);
            }
            
            Integer max = Integer.getInteger("maxjobs");
            
            if(max != null) props.put(PBSJobDriver.MAXIMUM_JOBS, max.toString());
            
            PBSJobDriver driver = new PBSJobDriver(propsFile.getParentFile());
            int jobsScheduled = driver.execute(props);
            
            if(jobsScheduled == 0) System.out.println("Work Completed - no jobs to executed.");
            else System.out.println("Scheduled "+jobsScheduled+" jobs.");
        }
        catch(Exception exp)
        {
            exp.printStackTrace();
            System.out.println();
            usage();
        }
    }
    
    public static void usage()
    {
        System.out.println("PBSGenetik takes a single command line argument: the properties file to run.");
        System.out.println("It is assumed that the properties file is in the folder that should be used to store run files.");
        System.exit(0);
    }
}
