package com.sasbury.genetik.driver.cluster.pbs;

import java.io.*;
import java.util.*;

import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.validation.*;

public class PBSValidator extends GenerationalValidator
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
            else
            {
                System.out.println("Validated without issues.");
            }
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
        System.out.println("PBSValidator takes a single command line argument: the properties file to run.");
        System.out.println("It is assumed that the properties file is in the folder that should be used to store run files.");
        System.exit(0);
    }
    
    protected void customValidate(Properties props, ArrayList<String> issues)
    {
        super.customValidate(props, issues);
        
        /*
        String prop = props.getProperty(PBSJobDriver.EST_SCORE_TIME);
        if(prop == null || prop.length()==0)
        {
            issues.add("Properties for PBS runs do not include: "+PBSJobDriver.EST_SCORE_TIME);
        }
        
        prop = props.getProperty(PBSJobDriver.EST_JOB_TIME);
        if(prop == null || prop.length()==0)
        {
            issues.add("Properties for PBS runs do not include: "+PBSJobDriver.EST_JOB_TIME);
        }*/

        String prop = props.getProperty(PBSBigJobGenetik.EST_TIME);
        if(prop == null || prop.length()==0)
        {
            issues.add("Properties for PBS runs do not include: "+PBSBigJobGenetik.EST_TIME);
        }
        
        prop = props.getProperty(PBSJobDriver.CLASS_PATH);
        if(prop == null || prop.length()==0)
        {
            issues.add("Properties for PBS runs do not include: "+PBSJobDriver.CLASS_PATH);
        }
        
        prop = props.getProperty(PBSJobDriver.EMAIL);
        if(prop == null || prop.length()==0)
        {
            issues.add("Properties for PBS runs do not include: "+PBSJobDriver.EMAIL);
        }
        
        prop = props.getProperty(PBSJobDriver.CLUSTER_ID);
        if(prop == null || prop.length()==0)
        {
            issues.add("Properties for PBS runs do not include: "+PBSJobDriver.CLUSTER_ID);
        }
    }

}
