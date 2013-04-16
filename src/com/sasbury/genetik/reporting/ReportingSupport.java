package com.sasbury.genetik.reporting;

import java.io.*;
import java.util.*;

import com.sasbury.genetik.*;

public class ReportingSupport
{

    protected static final String templateBase = "/com/sasbury/genetik/reporting/template/";
    protected static final String[] templateFiles = {"web/","web/js/","web/css/"
         ,"index.html"
         ,"web/run.html"
         ,"web/summary.html"
         ,"web/configuration.html"
         ,"web/individual.html"
         ,"web/loading.gif"
         ,"web/js/genetik.js"
         ,"web/js/jquery.ajaxqueue.js"
         ,"web/js/jquery.history.js"
         ,"web/js/jquery.jqplot.js"
         ,"web/js/jquery.jqplot.toimg.js"
         ,"web/js/jquery.colorhelpers.js"
         ,"web/js/jquery.flot.js"
         ,"web/js/jquery.flot.stack.js"
         ,"web/js/jquery.flot.errorbars.js"
         ,"web/js/jquery.flot.text.js"
         ,"web/js/jquery-1.4.4.min.js"
         ,"web/css/genetik.css"
    };
    
    public static void copyResources(String[] files,String base,Class<?> clz,boolean addWeb,File root)
    {
        for(String file : files)
        {
            file = file.replace("..", "");//get rid of any attempts to go up the tree
            
            if(file.endsWith("/"))
            {
                File f;
                
                if(addWeb) f = new File(root,"web/"+file.substring(0,file.length()-1));
                else  f = new File(root,file.substring(0,file.length()-1));
                
                f.mkdir();
                continue;
            }
            
            String resource = base + file;
            File f;
            
            if(addWeb) f = new File(root,"web/"+file);
            else f = new File(root,file);
            
            try
            {
                InputStream in = clz.getResourceAsStream(resource);
                BufferedInputStream bufIn = new BufferedInputStream(in);
                BufferedOutputStream bufOut = new BufferedOutputStream(new FileOutputStream(f));
                int b;
                
                while((b = bufIn.read())>=0)
                {
                    bufOut.write(b);
                }
                
                bufOut.close();
                bufIn.close();
            }
            catch(Exception exp)
            {
                System.out.println(resource);
                throw new IllegalArgumentException("Unable to copy reporting files.",exp);
            }
        }
    }
    
    public static void saveReportingFiles(Properties configurationProps,File root)
    {
        copyResources(templateFiles,templateBase,ReportingSupport.class,false,root);

        String customClass = configurationProps.getProperty(GenetikConstants.CUSTOM_REPORTING_RELATIVE_CLASS);
        String customBase = configurationProps.getProperty(GenetikConstants.CUSTOM_REPORTING_RESOURCE_BASE);
        String customFileStr = configurationProps.getProperty(GenetikConstants.CUSTOM_REPORTING_RESOURCES);
        
        if(customBase!=null && customFileStr != null && customClass!=null)
        {
            String[] customFiles = customFileStr.split(",");
            
            try
            {
                copyResources(customFiles,customBase,Class.forName(customClass),true,root);
            }
            catch(ClassNotFoundException ex)
            {
                throw new IllegalArgumentException("Illegal reporting relative class.",ex);
            }
        }
    }
}
