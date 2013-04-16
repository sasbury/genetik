package com.sasbury.genetik.driver.cluster.jobs;

import java.io.*;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;

public class PreProcessRun extends ClusterJob
{
    public void execute() throws Exception
    {
        PreProcessor preprocessor = null;
        
        if(run.getProperty(GenetikConstants.PREPROCESSOR)!=null)
        {
            preprocessor = (PreProcessor) Run.createObjectForClassName(run.getProperty(GenetikConstants.PREPROCESSOR));
        }
        
        if(preprocessor != null)
        {
            preprocessor.preprocess(getRun());
        }
    }
    
    public File getLogFile()
    {
        return new File((getRun()!=null)?getRunDir(getRun()):root,"preprocess.log");
    }
}