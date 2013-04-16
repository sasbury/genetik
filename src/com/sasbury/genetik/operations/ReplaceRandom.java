package com.sasbury.genetik.operations;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;

/**
 * Generates a random child to replace an existing child. The child's chromosome will be the same length as the parents.
 *
 */
public class ReplaceRandom implements Operation
{
    protected String name;
    
    public String[] validate(Run run)
    {
        return new String[0];
    }
    
    
    public int getRequiredParents()
    {
        return 1;
    }

    public Individual[] generateChildren(Individual[] parents, Run run)
    {
        Individual parent = parents[0];
        Individual retVal = parent.duplicate();
        
        retVal.setParentOne(parent.getId());
        retVal.setParentTwo(null);
        
        Chromosome chromo = retVal.getChromosome();
        
        String[] genes = chromo.extractGenes();
        
        for(int i=0,max=genes.length;i<max;i++)
        {
            genes[i] = chromo.randomGene(run);
        }
        
        chromo.initalizeFromGenes(genes);
        retVal.clearFitness();

        Individual[] children = {retVal};
        
        return children;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
