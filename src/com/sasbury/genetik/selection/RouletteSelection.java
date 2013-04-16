package com.sasbury.genetik.selection;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;
import com.sasbury.genetik.skip.*;

/**
 * Picks an item from the generation based on their fitness scaled to a probability. The chance of picking a
 * specific program is its probability divided by the total probability of all programs. Programs with
 * a raw score of <0 are given a value of 0 for this calculation.
 */
public class RouletteSelection implements SelectionScheme
{
    protected static final double MIN_PROBABILITY=0.0;
    protected double total;

    public Individual select(Population population,SkipTest skip,Run run)
    {
        if(skip==null) skip = new NoSkip();
        return select(population,skip,run,run.pickFloat());
    }

    /**
     * Internal method to pick based on a roulette wheel location.
     *  Provided for subclasses that use different "spinning" mechanism. This class just picks a random float from 0 to 1.0
     */
    protected Individual select(Population population, SkipTest skip, Run run,double atValue)
    {
        Individual p,retVal=null;
        double runningTotal=0;

        for(int i=0;i<population.size();i++)
        {
            p = (Individual) population.get(i);
            if(p!=null)
            {
                runningTotal += Math.max(p.getFitness(),MIN_PROBABILITY);//zero fitness still a chance

                double test = runningTotal/total;
                
                if((atValue < test) && !skip.skip(p))
                {
                    retVal = p;
                    break;
                }
            }
        }

        if(retVal == null) retVal = population.get(population.size()-1);

        return retVal;
    }

    public String[] validate(Run run)
    {
        return new String[0];
    }
    
    /**
     * No op.
     */
    public void cleanup()
    {
    }

    /**
     * Caches the total probability for use in selection.
     */
    public void initialize(Population population, Run run)
    {
        Individual p;

        total = 0;

        for(int i=0,max=population.size();i<max;i++)
        {
            p = (Individual) population.get(i);
            if(p!=null)
            {
                total += Math.max(p.getFitness(),MIN_PROBABILITY);
            }
        }
    }
}
