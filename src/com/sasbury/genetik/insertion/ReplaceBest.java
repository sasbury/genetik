package com.sasbury.genetik.insertion;

import com.sasbury.genetik.*;
import com.sasbury.genetik.population.*;

/**
 * Replaces ind based on the original ranking. Does not re-rank after each insertion,
 * therefore, at most population.size programs can be replaced with this scheme before
 * calling initialize.
 */
public class ReplaceBest extends RankBasedInsertionScheme
{
    public void insertProgramIntoPopulation(Individual ind, Population population)
    {
        String id = ranks.remove(ranks.size()-1);
        int index = population.indexFor(id);
        
        if(index>=0)
        {
            population.set(index,ind);
        }
    }
}
