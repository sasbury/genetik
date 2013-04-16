package com.sasbury.genetik.reporting;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.logging.*;

import com.sasbury.genetik.*;
import com.sasbury.util.json.*;

public class Statistics
{
    protected HashMap<String,AtomicLong> stats;
    
    public Statistics()
    {
        stats = new HashMap<String,AtomicLong>();
    }
    
    public void initializeStats(String json)
    {
        JSONDecoder decoder = new JSONDecoder();
        HashMap<String,Object> map = decoder.decode(json);
        
        for(String key : map.keySet())
        {
            Number num = (Number)map.get(key);
            set(key, num.longValue());
        }
    }
    
    public void mergeStats(String json)
    {
        JSONDecoder decoder = new JSONDecoder();
        HashMap<String,Object> map = decoder.decode(json);
        
        for(String key : map.keySet())
        {
            Number num = (Number)map.get(key);
            increment(key, num.longValue());
        }
    }
    
    public String asJSON()
    {
        JSONEncoder encoder = new JSONEncoder();
        HashMap<String,Object> json = new HashMap<String,Object>();
        
        for(String key : stats.keySet())
        {
            json.put(key, stats.get(key));
        }
        
        String retVal = "{}";
        
        try
        {
            retVal = encoder.encode(json);
        }
        catch(Exception exp)
        {
            Logger.getLogger(GenetikConstants.LOGGER).log(Level.SEVERE,"Error converting stats to JSON.",exp);
        }
        
        return retVal;
    }
    
    public void increment(String statistic,long delta)
    {
        AtomicLong stat = stats.get(statistic);
        
        if(stat == null)
        {
            stat = new AtomicLong(delta);
            stats.put(statistic, stat);
        }
        else
        {
            stat.getAndAdd(delta);
        }
    }
    
    public void set(String statistic,long delta)
    {
        AtomicLong stat = stats.get(statistic);
        
        if(stat == null)
        {
            stat = new AtomicLong(delta);
            stats.put(statistic, stat);
        }
        else
        {
            stat.getAndSet(delta);
        }
    }
}
