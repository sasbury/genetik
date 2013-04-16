package com.sasbury.genetik.reporting;

import java.io.*;
import java.util.*;

public class UpdateReporting
{
    public static void main(String args[])
    {
        if(args.length==0)
        {
            System.err.println("Usage: java UpdateReporting directory");
            System.exit(1);
        }
        
        File top = new File(args[0]);
        processDirectory(top);
    }
    
    public static void processDirectory(File dir)
    {
        File run = new File(dir,"run.properties");
        
        if(run.exists())
        {
            try
            {
                Properties config = new Properties();
                FileReader reader = new FileReader(run);
                config.load(reader);
                reader.close();
                
                ReportingSupport.saveReportingFiles(config, dir);
                
                System.out.println("Processed run at "+run.getAbsolutePath());
            }
            catch(Exception exp)
            {
                System.err.println("Error processing "+run.getAbsolutePath());
                exp.printStackTrace();
            }
        }
        else
        {
            System.out.println("Processing folder "+dir.getAbsolutePath());
            
            File[] children = dir.listFiles();
            
            for(File f : children)
            {
                if(f.isDirectory())
                {
                    processDirectory(f);
                }
            }
        }
    }
}
