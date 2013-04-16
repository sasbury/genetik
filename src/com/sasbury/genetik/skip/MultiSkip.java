package com.sasbury.genetik.skip;

import java.util.*;

import com.sasbury.genetik.*;
import com.sasbury.genetik.population.*;

public class MultiSkip implements SkipTest
{
    protected HashMap<Individual,Individual> toSkip;
    
    public MultiSkip(Individual[] toSkip)
    {
        this.toSkip = new HashMap<Individual, Individual>();
        for(Individual i : toSkip)
        {
            if(i!=null) this.toSkip.put(i,i);
        }
    }
    
    public boolean skip(Individual individual)
    {
        return toSkip.containsKey(individual);
    }

}
