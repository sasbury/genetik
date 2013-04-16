package test.com.sasbury.genetik.raw;

import java.io.*;
import java.util.*;

import com.sasbury.genetik.*;
import com.sasbury.genetik.chromosomes.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.generator.*;
import com.sasbury.genetik.population.*;

public class XMLJSONPerfTest
{
    public static final int TEST_SIZE = 4096;
    public static final int ITERATIONS = 50;
    public static final int GENES = 256;
    
    public static void main(String args[])
    {
        Properties props = new Properties();
        props.setProperty(GenetikConstants.CHROMOSOME, BinaryChromosome.class.getCanonicalName());
        props.setProperty(GenetikConstants.GENES, String.valueOf(GENES));
        Run run = new Run(props,null);
        
        RandomGenerator gen = new RandomGenerator();
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

        File f = new File("/tmp/poptest.xml");
        long startXMLEncode = System.currentTimeMillis();
        
        for(int i=0,max=ITERATIONS;i<max;i++)
        {
            XMLPopulationCoder.encode(pop,f);
        }
        
        long endXMLEncode = System.currentTimeMillis();
        long xmlFileSize = f.length();
        
        long startXMLDecode = System.currentTimeMillis();
        
        for(int i=0,max=ITERATIONS;i<max;i++)
        {
            XMLPopulationCoder.decode(f,run);
        }
        
        long endXMLDecode = System.currentTimeMillis();
        
        long startJSONEncode = System.currentTimeMillis();

        f = new File("/tmp/poptest.json");
        for(int i=0,max=ITERATIONS;i<max;i++)
        {
            JSONPopulationCoder.encode(pop,f);
        }
        
        long endJSONEncode = System.currentTimeMillis();
        long jsonFileSize = f.length();

        long startJSONDecode = System.currentTimeMillis();
        
        for(int i=0,max=ITERATIONS;i<max;i++)
        {
            JSONPopulationCoder.decode(f,run);
        }
        
        long endJSONDecode = System.currentTimeMillis();
        
        System.out.println("Test results with "+TEST_SIZE+" individuals, with "+GENES+" genes and "+ITERATIONS+" encode/decode cycles.");
        System.out.println();

        System.out.println("XML Times - Decode = "+((endXMLDecode-startXMLDecode)/1000)+"(s) Encode = "+((endXMLEncode-startXMLEncode)/1000)+"(s)");
        System.out.println("JSON Times - Decode = "+((endJSONDecode-startJSONDecode)/1000)+"(s) Encode = "+((endJSONEncode-startJSONEncode)/1000)+"(s)");
        
        System.out.println();
        
        System.out.println("XML File Size = "+(xmlFileSize/1024)+"(kb)");
        System.out.println("JSON File Size = "+(jsonFileSize/1024)+"(kb)");
    }
}
