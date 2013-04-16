package com.sasbury.genetik.operations;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;

/**
 * Takes two parents and returns two children created with single point crossover. The parent's can have chromosomes
 * of different lengths, which may result in the children having chromosomes of different lengths.
 *
 */
public class SinglePointCrossover implements Operation
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
        
        int cut = run.pickRandom(Math.min(pOne.length,pTwo.length)-1);//pick a random from the shortest parent
        
        int lOne = cut+(pTwo.length - cut);
        int lTwo = cut+(pOne.length - cut);

        String genesOne[] = new String[lOne];
        String genesTwo[] = new String[lTwo];
        
        for(int i=0;i<cut;i++)
        {
            genesOne[i] = pOne[i];
        }
        
        for(int i=cut;i<pTwo.length;i++)
        {
            genesOne[i] = pTwo[i];
        }
        
        one.initalizeFromGenes(genesOne);
        
        for(int i=0;i<cut;i++)
        {
            genesTwo[i] = pTwo[i];
        }
        
        for(int i=cut;i<pOne.length;i++)
        {
            genesTwo[i] = pOne[i];
        }
        
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
