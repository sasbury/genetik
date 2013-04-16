package com.sasbury.genetik.optimize;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;

/**
 * HillClimb is a keep the best algorithm that tries replacing one gene in each individual with a random gene.
 *
 */
public class HillClimb extends KeepTheBest implements GenerationalOptimizer
{
    public void generatePopulation(Population oldPop, Population newPop,Run run,int generation)
    {
        for(int i=0,max=oldPop.size();i<max;i++)
        {
            Individual oldInd = oldPop.get(i);
            Individual newInd = oldInd.duplicate();
            Chromosome chromo = newInd.getChromosome();
            
            String[] genes = chromo.extractGenes();
            int index = run.pickRandom(genes.length-1);
            genes[index] = chromo.randomGene(run);
            chromo.initalizeFromGenes(genes);
            newPop.set(i,newInd);
        }
    }
}
