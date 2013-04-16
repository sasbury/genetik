package com.sasbury.genetik.selection;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;

/**
 * Selects the best candidate from generation every time, if it is not skipped.
 */
public class RankSelection implements SelectionScheme
{
    public Individual select(Population population,SkipTest skip,Run run)
    {
        Individual p;
        Individual retVal = null;
        double max = -Double.MAX_VALUE;

        for(int i=0;i<population.size();i++)
        {
            p = (Individual) population.get(i);
            if((p!=null) && p.getFitness()>max && (skip==null || !skip.skip(p)))
            {
                max = p.getFitness();
                retVal = p;
            }
        }

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
     * No op.
     */
    public void initialize(Population population, Run run)
    {

    }
}
