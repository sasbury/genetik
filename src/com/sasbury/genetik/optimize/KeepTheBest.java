package com.sasbury.genetik.optimize;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;

/**
 * Abstract super class for hill climbing type optimizers. Unless noted, these optimizers do not use selection schemes.
 *
 */
public abstract class KeepTheBest implements GenerationalOptimizer
{
    public String[] validate(Run run)
    {
        return new String[0];
    }

    /**
     * Looks at every individual in newPop and replaces with a clone of the same index'd individual in oldPop if the fitness didn't improve.
     */
    public void finalizePopulation(Population oldPop, Population newPop,Run run,int generation)
    {
        if(oldPop == null || oldPop.size()==0) return;
        
        for(int i=0,max=newPop.size();i<max;i++)
        {
            Individual newInd = newPop.get(i);
            Individual oldInd = oldPop.get(i);

            if(oldInd.getFitness()>newInd.getFitness())
            {
                newInd = oldInd.duplicate();
                newPop.set(i, newInd);
            }
        }
    }

}
