package test.com.sasbury.genetik;

import java.io.*;
import java.util.*;

import com.sasbury.genetik.*;
import com.sasbury.genetik.chromosomes.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.generator.*;
import com.sasbury.genetik.population.*;

import junit.framework.*;

public class XMLPopulationTests extends TestCase
{
    public static final int TEST_SIZE=1024;
    
    public void runTest(Properties props) throws Exception
    {
        Run run = new Run(props,null);
        RandomGenerator gen = new RandomGenerator();
        File f = new File("/tmp/poptest.xml");
        f.delete();
        Population pop = new InMemoryPopulation();
        
        for(int i=0;i<TEST_SIZE;i++)
        {
            Individual ind = gen.generateIndividual(run);
            ind.setParentOne(null);
            ind.setParentTwo(null);
            ind.setUserData("item "+i);
            
            double[] scores = {((double)i)/100.0,((double)i)/30.0,((double)i)/20.0};
            ind.setFitness(scores[0]);
            ind.setRawScores(scores);
            pop.set(-1, ind);
        }
        
        assertEquals(pop.size(),TEST_SIZE);
        
        XMLPopulationCoder.encode(pop,f);
        
        Population pop2 = XMLPopulationCoder.decode(new File("/tmp/poptest.xml"),run);
        
        assertEquals(pop,pop2);
    }
    
    public void testHugeBinaryPopulation() throws Exception
    {
        Properties props = new Properties();
        props.setProperty(GenetikConstants.CHROMOSOME, BinaryChromosome.class.getCanonicalName());
        props.setProperty(GenetikConstants.GENES, "1024");
        runTest(props);
    }
    
    public void testRandomBinaryPopulation() throws Exception
    {
        Properties props = new Properties();
        props.setProperty(GenetikConstants.CHROMOSOME, BinaryChromosome.class.getCanonicalName());
        props.setProperty(GenetikConstants.GENES, "100");
        runTest(props);
    }
    
    public void testRandomIntegerPopulation() throws Exception
    {
        Properties props = new Properties();
        props.setProperty(GenetikConstants.CHROMOSOME, IntegerChromosome.class.getCanonicalName());
        props.setProperty(GenetikConstants.GENES, "100");
        props.setProperty(IntegerChromosome.MIN_GENE, "0");
        props.setProperty(IntegerChromosome.MAX_GENE, "10");
        runTest(props);
    }
    
    public void testRandomFloatingPointPopulation() throws Exception
    {
        Properties props = new Properties();
        props.setProperty(GenetikConstants.CHROMOSOME, DoubleChromosome.class.getCanonicalName());
        props.setProperty(GenetikConstants.GENES, "100");
        props.setProperty(IntegerChromosome.MIN_GENE, "-2.5");
        props.setProperty(IntegerChromosome.MAX_GENE, "2.5");
        runTest(props);
    }
    
    public void testRandomCharPopulation() throws Exception
    {
        Properties props = new Properties();
        props.setProperty(GenetikConstants.CHROMOSOME, CharChromosome.class.getCanonicalName());
        props.setProperty(GenetikConstants.GENES, "100");
        props.setProperty(CharChromosome.AVAILABLE_GENES, "abcdefghijklmnopqrstuvwxyz");
        runTest(props);
    }
}
