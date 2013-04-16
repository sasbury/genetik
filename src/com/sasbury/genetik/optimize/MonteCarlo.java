package com.sasbury.genetik.optimize;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.generator.*;
import com.sasbury.genetik.population.*;

/**
 * Replaces each program with a new random one if the new one has a better fitness.
 */
public class MonteCarlo extends KeepTheBest
{
    public void generatePopulation(Population oldPop, Population newPop, Run run, int generation)
    {
        RandomGenerator generator = new RandomGenerator();

        for(int i=0,max=oldPop.size();i<max;i++)
        {
            Individual oldInd = oldPop.get(i);
            Individual newInd = oldInd.duplicate();
            Chromosome chromo = generator.generateIndividual(run).getChromosome();
            newInd.setChromosome(chromo);
            newInd.clearFitness();
            newPop.set(i,newInd);
        }
    }
}
