package com.sasbury.genetik;

import java.io.*;
import java.util.*;

import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.validation.*;

public class Genetik
{
    public static void main(String args[])
    {
        try
        {
            if(args.length<1) usage();
            
            File propsFile = new File(args[0]);
            File rootFolder = propsFile.getParentFile();
            
            //happens if the args isn't a full path on linux
            if(rootFolder == null) rootFolder = new File(System.getProperty("user.dir"));
            
            if(!propsFile.exists()) usage();
            
            Properties props = Run.createDefaultProperties();
            
            BufferedReader reader = new BufferedReader(new FileReader(propsFile));
            props.load(reader);
            reader.close();
            
            GenerationalValidator validator = new GenerationalValidator();
            String[] issues = validator.validate(props);
            
            if(issues.length>0)
            {
                for(String s : issues)
                {
                    System.out.println(s);
                }
                
                System.exit(1);
            }
            
            FileBasedGenerationalDriver driver = new FileBasedGenerationalDriver(rootFolder);
            driver.execute(props);
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
        System.out.println("Genetik takes a single command line argument: the properties file to run.");
        System.out.println("It is assumed that the properties file is in the folder that should be used to store run files.");
        System.exit(0);
    }
}
