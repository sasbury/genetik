package com.sasbury.genetik;

import com.sasbury.genetik.driver.*;

public interface Chromosome
{
    /**
     * Provided so chromosome can validate the run properties.
     * @param run
     * @return
     */
    public String[] validate(Run run);
    
    /**
     * Break the chromosome into strings that can be manipulated opaquely by crossover, or other operators.
     * @return
     */
    public String[] extractGenes();
    
    /**
     * Initialize a chromosome to a new set of genes. Reset any other values.
     * @param genes
     */
    public void initalizeFromGenes(String[] genes);
    
    /**
     * Initialize a chromosome from it's archive format. Reset any other values.
     * @param genes
     */
    public void initalizeFromArchive(String data);
    
    /**
     * @return A string version of the chromosome fit for storage in an XML file. The smaller the better.
     */
    public String toArchiveFormat();
    
    /**
     * 
     * @return A human friendly version of the chromosome
     */
    public String toString();
    
    /**
     * Generate a random gen using the properties from the run, and the provided additional property prefix
     * @param run
     * @return
     */
    public String randomGene(Run run);
    
    public int geneCount();
}
