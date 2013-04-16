package com.sasbury.genetik.operations;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;

/**
 * Takes two parents and returns two children created with uniform crossover. The parent's should be the same length.
 *
 */
public class UniformCrossover implements Operation
{
    protected String name;
    
    public String[] validate(Run run)
    {
        return new String[0];
    }
    
    
    public int getRequiredParents()
    {
        return 2;
    }

    public Individual[] generateChildren(Individual[] parents, Run run)
    {
        Individual parentOne = parents[0];
        Individual parentTwo = parents[1];
        Individual childOne = null;
        Individual childTwo = null;

        Chromosome one = (Chromosome) run.createObject(GenetikConstants.CHROMOSOME);
        Chromosome two = (Chromosome) run.createObject(GenetikConstants.CHROMOSOME);

        String pOne[] = parentOne.getChromosome().extractGenes();
        String pTwo[] = parentTwo.getChromosome().extractGenes();
        String genesOne[] = new String[pOne.length];
        String genesTwo[] = new String[pTwo.length];
        
        for(int i=0;i<pTwo.length;i++)
        {
            if(run.pickRandom(1) == 0)
            {
                genesOne[i] = pOne[i];
                genesTwo[i] = pTwo[i];
            }
            else
            {
                genesOne[i] = pTwo[i];
                genesTwo[i] = pOne[i];
            }
        }
        
        one.initalizeFromGenes(genesOne);
        two.initalizeFromGenes(genesTwo);
        
        childOne = new Individual();
        childOne.setParentOne(parentOne.getId());
        childOne.setParentTwo(parentTwo.getId());
        childOne.setChromosome(one);
        
        childTwo = new Individual();
        childTwo.setParentOne(parentTwo.getId());
        childTwo.setParentTwo(parentOne.getId());
        childTwo.setChromosome(two);
        
        Individual[] children = {childOne,childTwo};
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
