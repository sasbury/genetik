package com.sasbury.genetik.chromosomes;

import java.text.*;
import java.util.*;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;

/**
 * Chromosome where each gene is 0 or 1.
 */
public class BinaryChromosome implements Chromosome
{
    protected byte[] genes;

    public String[] validate(Run run)
    {
        return new String[0];
    }
    
    public void initalizeFromArchive(String data)
    {
        genes = new byte[data.length()];
        
        StringCharacterIterator iter = new StringCharacterIterator(data);
        
        int i=0;
        for(char c = iter.first();c != CharacterIterator.DONE;c = iter.next(),i++)
        {
            if(c=='0') genes[i] = 0;
            else genes[i] = 1;
        }
    }

    public void initalizeFromGenes(String[] genes)
    {
        this.genes = new byte[genes.length];
        
        for(int i=0,max=genes.length;i<max;i++)
        {
            String s = genes[i];
            
            if("0".equals(s)) this.genes[i] = 0;
            else this.genes[i] = 1;
        }
    }
    
    public byte[] getGenes()
    {
        return genes;
    }
    
    public void setGenes(byte[] genes)
    {
        this.genes = genes;
    }
    
    public String[] extractGenes()
    {
        String[] strings = new String[genes.length];
        
        for(int i=0,max=genes.length;i<max;i++)
        {
            if(genes[i]==0) strings[i] = "0";
            else strings[i] = "1";
        }
        
        return strings;
    }

    public String toArchiveFormat()
    {
        StringBuilder builder = new StringBuilder(genes.length);
        
        for(int i=0,max=genes.length;i<max;i++)
        {
            if(genes[i]==0) builder.append('0');
            else builder.append('1');
        }
        
        return builder.toString();
    }

    public String randomGene(Run run)
    {
        int rand = run.pickRandom(1);
        
        if(rand == 0)
        {
            return "0";
        }
        else
        {
            return "1";
        }
    }

    public int geneCount()
    {
        return (genes!=null)?genes.length:0;
    }

    @Override
    public int hashCode()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + Arrays.hashCode(genes);
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        final BinaryChromosome other = (BinaryChromosome) obj;
        if(!Arrays.equals(genes, other.genes))
            return false;
        return true;
    }
}
