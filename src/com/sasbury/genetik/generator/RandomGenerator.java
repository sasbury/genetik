package com.sasbury.genetik.generator;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;

public class RandomGenerator implements IndividualGenerator
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
        
        for(int i=0;i<length;i++)
        {
            genes[i] = chromo.randomGene(run);
        }
        
        chromo.initalizeFromGenes(genes);
        
        Individual ind = new Individual();
        ind.setChromosome(chromo);
        return ind;
    }

}
