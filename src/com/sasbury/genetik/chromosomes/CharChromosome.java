package com.sasbury.genetik.chromosomes;

import java.util.*;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;

/**
 * Chromosome where each gene is a single character from the string property AVAIALBLE_GENES, repeated characters in the property will change the
 * chance for that char to appear in a random gene (including white space).
 */
public class CharChromosome implements Chromosome
{
    /**
     * A string property containing the list of available genes.
     */
    public static String AVAILABLE_GENES = "available_genes";
    
    protected char[] genes;

    public String[] validate(Run run)
    {
        String available = run.getProperty(AVAILABLE_GENES);
        
        if(available == null)
        {
            String err[] = {"Run with char chromosome is missing "+AVAILABLE_GENES+" property."};
            return err;
        }
        else if(available.length() == 0)
        {
            String err[] = {"Run with char chromosome has a zero length "+AVAILABLE_GENES+" property."};
            return err;
        }
        
        return new String[0];
    }
    
    public void initalizeFromArchive(String data)
    {
        genes = new char[data.length()];
        
        for(int i=0,max=data.length();i<max;i++)
        {
            genes[i] = data.charAt(i);
        }
    }

    public void initalizeFromGenes(String[] genes)
    {
        this.genes = new char[genes.length];
        
        for(int i=0,max=genes.length;i<max;i++)
        {
            String s = genes[i];
            
            this.genes[i] = s.charAt(0);
        }
    }
    
    public char[] getGenes()
    {
        return genes;
    }
    
    public void setGenes(char[] genes)
    {
        this.genes = genes;
    }
    
    public String asString()
    {
        return new String(genes);
    }
    
    public String[] extractGenes()
    {
        String[] strings = new String[genes.length];
        
        for(int i=0,max=genes.length;i<max;i++)
        {
            strings[i] = String.valueOf(genes[i]);
        }
        
        return strings;
    }

    public String toArchiveFormat()
    {
        StringBuilder builder = new StringBuilder();
        String archive = null;
        
        for(int i=0,max=genes.length;i<max;i++)
        {
            builder.append(genes[i]);
        }
        
        archive = builder.toString();
        
        return archive;
    }

    public String randomGene(Run run)
    {
        String available = run.getProperty(AVAILABLE_GENES);
        int rand = run.pickRandom(available.length()-1);
        
        char c = available.charAt(rand);
        
        return String.valueOf(c);
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
        final CharChromosome other = (CharChromosome) obj;
        if(!Arrays.equals(genes, other.genes))
            return false;
        return true;
    }
}

