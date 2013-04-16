package com.sasbury.genetik.selection;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;

/**
 * Selects a random member of generation. No ranking or scaling is performed. If the skip tests skips all individuals, this can result in an infinite loop.
 */
public class RandomSelection implements SelectionScheme
{
    public Individual select(Population population,SkipTest skip,Run run)
    {
        Individual retVal = null;
        int max = population.size()-1;
        
        while(retVal == null)
        {
            int index = run.pickRandom(max);
    
            retVal = population.get(index);
            
            if(skip!=null && skip.skip(retVal)) retVal = null;
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
     *  no op.
     * @param population the Generation to be scaled
     * @param run the configuration information
     */
    public void initialize(Population population, Run run)
    {

    }
}
