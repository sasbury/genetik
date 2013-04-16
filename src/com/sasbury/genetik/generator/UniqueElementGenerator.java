package com.sasbury.genetik.generator;

import java.util.*;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;

/**
 * Initializes the individual with random, but unique genes. Can go into an infinite loop if it is unable to fill enough slots uniquely.
 *
 */
public class UniqueElementGenerator implements IndividualGenerator
{
    public void initialize(Run run,Run previousRun,Population previousPopulation)
    {
    }
    
    public void cleanup()
    {
    }

    public String[] validate(Run run)
    { 
        return new String[0];
    }

    public Individual generateIndividual(Run run)
    {
        Chromosome chromo = (Chromosome) run.createObject(GenetikConstants.CHROMOSOME);
        int length = Integer.parseInt(run.getProperty(GenetikConstants.GENES));
        
        String[] genes = new String[length];
        int i=0;
        HashMap<String,String> usedGenes = new HashMap<String,String>();
        
        while(i<length)
        {
             String randGene = chromo.randomGene(run);
             
             if(!usedGenes.containsKey(randGene))
             {
                 genes[i] = randGene;
                 usedGenes.put(randGene, randGene);
                 i++;
             }
        }
        
        chromo.initalizeFromGenes(genes);

        Individual ind = new Individual();
        ind.setChromosome(chromo);
        return ind;
    }

}