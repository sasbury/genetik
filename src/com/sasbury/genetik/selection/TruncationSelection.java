package com.sasbury.genetik.selection;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;

import java.util.*;

/**
 * Randomly selects individuals from the top TRUNCATION_SELECTION_PERCENTAGE percentage of the population.
 * The default percentage is 0.25.
 */
public class TruncationSelection implements SelectionScheme
{
    public static final String TRUNCATION_SELECTION_PERCENTAGE = "truncation_selection_percentage";
    
    protected ArrayList<Individual> toSelect;

    public String[] validate(Run run)
    {
        /*
         * uses a default of 0.25 percent
         * double perc = Run.getDoubleProperty(run, TRUNCATION_SELECTION_PERCENTAGE, -1, true);
         
        
        if(perc == -1)
        {
            String err[] = {"Run with Truncation selection has invalid "+TRUNCATION_SELECTION_PERCENTAGE+" property."};
            return err;
        }*/
        
        return new String[0];
    }
    
    /**
     * No op.
     */
    public void cleanup()
    {
    }

    /**
     * Caches the valid programs to select from.
     */
    public void initialize(Population population, Run run)
    {
        toSelect = new ArrayList<Individual>();
        String pStr = run.getProperty(TRUNCATION_SELECTION_PERCENTAGE);
        double percentage = 0.25;
        
        if(pStr != null) percentage = Double.parseDouble(pStr);
        
        ArrayList<Individual> toRank = new ArrayList<Individual>(population.size());
        for(int i=0,max=population.size();i<max;i++)
        {
            toRank.add(population.get(i));
        }

        Collections.sort(toRank,new IndividualComparator());

        int max = toRank.size();
        for(int i=(int)(percentage*max);i<max;i++)
        {
            toSelect.add(toRank.get(i));
        }
    }

    public Individual select(Population population,SkipTest skip,Run run)
    {
        Individual retVal = null;
        int max = toSelect.size()-1;
        
        while(retVal == null)
        {
            int index = run.pickRandom(max);
    
            retVal = toSelect.get(index);
            
            if(skip!=null && skip.skip(retVal)) retVal = null;
        }

        return retVal;
    }
}
