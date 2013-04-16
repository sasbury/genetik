package com.sasbury.genetik.generator;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;

/**
 * uses BASE_PROGRAM to create a new program with MUTATIONS_ON_GENERATION mutations. The default number of mutations is 2.
 * 
 * If not base program is available, a random one is created.
 *
 */
public class PerturbationBasedGenerator extends RandomGenerator
{
    public static final String BASE_PROGRAM = "base_program";
    public static final String MUTATIONS_ON_GENERATION = "mutations_on_generation";

    public Individual generateIndividual(Run run)
    {
        String baseProgram = run.getProperty(BASE_PROGRAM);
        String mutationString = run.getProperty(MUTATIONS_ON_GENERATION);
        
        int mutations = (mutationString!=null)?Integer.parseInt(mutationString):2;

        Individual retVal = null;
        
        if(baseProgram==null)
        {
            retVal = super.generateIndividual(run);
        }
        else
        {
            Chromosome chromo = (Chromosome) run.createObject(GenetikConstants.CHROMOSOME);
            chromo.initalizeFromArchive(baseProgram);
            
            String genes[] = chromo.extractGenes();
            
            for(int i=0;i<mutations;i++)
            {
                int index = run.pickRandom(genes.length-1);
                genes[index] = chromo.randomGene(run);
            }
            
            chromo.initalizeFromGenes(genes);
            retVal = new Individual();
            retVal.setChromosome(chromo);
        }
        
        return retVal;
    }
}
