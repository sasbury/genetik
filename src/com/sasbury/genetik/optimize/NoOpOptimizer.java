package com.sasbury.genetik.optimize;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;

/**
 * A simple optimizer that just copies individuals between populations.
 *
 */
public class NoOpOptimizer implements GenerationalOptimizer
{
    public String[] validate(Run run)
    {
        return new String[0];
    }

    public void generatePopulation(Population oldPop, Population newPop,Run run,int generation)
    {
        for(int i=0,max=oldPop.size();i<max;i++)
        {
            Individual oldInd = oldPop.get(i);
            Individual newInd = oldInd.duplicate();
            
            newPop.set(i,newInd);
        }
    }

    /**
     * No op.
     */
    public void finalizePopulation(Population oldPop, Population newPop,Run run,int generation)
    {
    }
}
