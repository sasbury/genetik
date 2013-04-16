package com.sasbury.genetik.insertion;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;

import java.util.*;

public abstract class RankBasedInsertionScheme extends AbstractInsertionScheme
{
    protected ArrayList<String> ranks;

    public void initialize(Population population, Run run)
    {
        super.initialize(population, run);

        ranks = new ArrayList<String>();

        ArrayList<Individual> toRank = new ArrayList<Individual>(population.size());
        for(int i=0,max=population.size();i<max;i++)
        {
            toRank.add(population.get(i));
        }

        Collections.sort(toRank,new IndividualComparator());

        for(int i=0,max=toRank.size();i<max;i++)
        {
            ranks.add(toRank.get(i).getId());
        }
    }
}
