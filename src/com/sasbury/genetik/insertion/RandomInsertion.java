package com.sasbury.genetik.insertion;

import com.sasbury.genetik.*;
import com.sasbury.genetik.population.*;

/**
 * Randomly inserts the program into the population.
 */
public class RandomInsertion extends AbstractInsertionScheme
{
    public void insertProgramIntoPopulation(Individual ind, Population population)
    {
        int index = getRun().pickRandom(population.size()-1);
        population.set(index,ind);
    }
}
