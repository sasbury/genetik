package com.sasbury.genetik.chromosomes;

import java.util.*;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;

/**
 * Chromosome where each gene is an double between the values of the minimum_gen and maximum_gene properties. Values are inclusive.
 */
public class DoubleChromosome implements Chromosome
{
    /**
     * The Minimum double value for a gene, inclusive.
     */
    public static final String MIN_GENE="minimum_gene";
    
    /**
     * The Maximum double value for a gene, inclusive.
     */
    public static final String MAX_GENE="maximum_gene";
    
    protected double[] genes;
    
    public String[] validate(Run run)
    {
        double min=0,max=0;
        try
        {
            min = Double.parseDouble(run.getProperty(MIN_GENE));
        }
        catch(Exception exp)
        {
            String err[] = {"Run with floating point chromosome is missing "+MIN_GENE+" property."};
            return err;
        }

        try
        {
            max = Double.parseDouble(run.getProperty(MAX_GENE));
        }
        catch(Exception exp)
        {
            String err[] = {"Run with floating point chromosome is missing "+MAX_GENE+" property."};
            return err;
        }

        if(min>=max)
        {
            String err[] = {"Run with double chromosome has min that is >= max."};
            return err;
        }
        
        return new String[0];
    }
    
    public void initalizeFromArchive(String data)
    {
        String[] strings = data.split(",");
        
        genes = new double[strings.length];
        
        for(int i=0,max=strings.length;i<max;i++)
        {
            genes[i] = Double.parseDouble(strings[i]);
        }
    }

    public void initalizeFromGenes(String[] genes)
    {
        this.genes = new double[genes.length];
        
        for(int i=0,max=genes.length;i<max;i++)
        {
            this.genes[i] = Double.parseDouble(genes[i]);
        }
    }
    
    public double[] getGenes()
    {
        return genes;
    }
    
    public void setGenes(double[] genes)
    {
        this.genes = genes;
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
        
        for(int i=0,max=genes.length;i<max;i++)
        {
            if(i!=0) builder.append(",");
            builder.append(genes[i]);
        }
        
        //Make this easier to debug ;-)
        String archive = builder.toString();
        
        return archive;
    }

    public String randomGene(Run run)
    {
        double min = Double.parseDouble(run.getProperty(MIN_GENE));
        double max = Double.parseDouble(run.getProperty(MAX_GENE));
        double rand = min + (run.pickDouble() * (max-min));
        return String.valueOf(rand);
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
        final DoubleChromosome other = (DoubleChromosome) obj;
        if(!Arrays.equals(genes, other.genes))
            return false;
        return true;
    }
}
