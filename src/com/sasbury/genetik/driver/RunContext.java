package com.sasbury.genetik.driver;

import com.sasbury.genetik.reporting.*;

public interface RunContext
{
    public void saveData(String name,String data);
    public void saveRunData(Run run,String name,String data);
    
    public String getData(String name);
    public String getRunData(Run run,String name);
    
    public Statistics getStats();
}
