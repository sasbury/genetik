package com.sasbury.genetik.insertion;

import com.sasbury.genetik.*;
import com.sasbury.genetik.population.*;

/**
 * Looks at parent one in ind and if it is a valid index into population, the program at that index is replaced.
 */
public class ParentInsertion extends AbstractInsertionScheme
{
    public void insertProgramIntoPopulation(Individual ind, Population population)
    {
        String id = ind.getParentOne();
        int index = population.indexFor(id);
        
        if(index>0 && index<population.size())
        {
            population.set(index,ind);
        }
    }
}
