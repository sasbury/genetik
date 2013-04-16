package test.com.sasbury.genetik;

import java.util.*;

import com.sasbury.genetik.*;
import com.sasbury.genetik.chromosomes.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.generator.*;

import junit.framework.*;

public class GeneratorTests extends TestCase
{
    public void testRandomGenerator() throws Exception
    {
        Properties props = new Properties();
        props.setProperty(GenetikConstants.CHROMOSOME, CharChromosome.class.getCanonicalName());
        props.setProperty(GenetikConstants.GENES, "5");
        props.setProperty(CharChromosome.AVAILABLE_GENES, "abcdefghijklmnopqrstuvwxyz");
        props.setProperty(PerturbationBasedGenerator.MUTATIONS_ON_GENERATION,"0");
        
        Run run = new Run(props,null);
        RandomGenerator generator = new RandomGenerator();
        
        for(int i=0,max=100;i<max;i++)
        {
            Chromosome chromo = generator.generateIndividual(run).getChromosome();
            String archive = chromo.toArchiveFormat();
            assertEquals(archive.length(),Integer.parseInt(props.getProperty(GenetikConstants.GENES)));
            
            String available = props.getProperty(CharChromosome.AVAILABLE_GENES);
            for(int j=0,maxj=archive.length();j<maxj;j++)
            {
                char d = archive.charAt(j);
                assertTrue(available.indexOf(d)>=0);
            }
        }
    }
    
    public void testPerturbationGeneratorNoBaseProgram() throws Exception
    {
        Properties props = new Properties();
        props.setProperty(GenetikConstants.CHROMOSOME, CharChromosome.class.getCanonicalName());
        props.setProperty(GenetikConstants.GENES, "5");
        props.setProperty(CharChromosome.AVAILABLE_GENES, "abcdefghijklmnopqrstuvwxyz");
        props.setProperty(PerturbationBasedGenerator.MUTATIONS_ON_GENERATION,"0");
        
        Run run = new Run(props,null);
        PerturbationBasedGenerator generator = new PerturbationBasedGenerator();
        
        for(int i=0,max=100;i<max;i++)
        {
            Chromosome chromo = generator.generateIndividual(run).getChromosome();
            String archive = chromo.toArchiveFormat();
            assertEquals(archive.length(),Integer.parseInt(props.getProperty(GenetikConstants.GENES)));

            String available = props.getProperty(CharChromosome.AVAILABLE_GENES);
            for(int j=0,maxj=archive.length();j<maxj;j++)
            {
                char d = archive.charAt(j);
                assertTrue(available.indexOf(d)>=0);
            }
        }
    }
    
    public void testPerturbationGeneratorZeroMutations() throws Exception
    {
        Properties props = new Properties();
        props.setProperty(GenetikConstants.CHROMOSOME, CharChromosome.class.getCanonicalName());
        props.setProperty(GenetikConstants.GENES, "5");
        props.setProperty(CharChromosome.AVAILABLE_GENES, "abcdefghijklmnopqrstuvwxyz");
        props.setProperty(PerturbationBasedGenerator.BASE_PROGRAM,"abcde");
        props.setProperty(PerturbationBasedGenerator.MUTATIONS_ON_GENERATION,"0");
        
        Run run = new Run(props,null);
        PerturbationBasedGenerator generator = new PerturbationBasedGenerator();
        String baseProgram = run.getProperty(PerturbationBasedGenerator.BASE_PROGRAM);
        
        for(int i=0,max=100;i<max;i++)
        {
            Chromosome chromo = generator.generateIndividual(run).getChromosome();
            
            assertEquals(chromo.toArchiveFormat(), baseProgram);
        }
    }
    
    public void testPerturbationGeneratorOneMutation() throws Exception
    {
        Properties props = new Properties();
        props.setProperty(GenetikConstants.CHROMOSOME, CharChromosome.class.getCanonicalName());
        props.setProperty(GenetikConstants.GENES, "5");
        props.setProperty(CharChromosome.AVAILABLE_GENES, "abcdefghijklmnopqrstuvwxyz");
        props.setProperty(PerturbationBasedGenerator.BASE_PROGRAM,"aaaaa");
        props.setProperty(PerturbationBasedGenerator.MUTATIONS_ON_GENERATION,"1");
        
        Run run = new Run(props,null);
        PerturbationBasedGenerator generator = new PerturbationBasedGenerator();
        String baseProgram = run.getProperty(PerturbationBasedGenerator.BASE_PROGRAM);
        
        for(int i=0,max=100;i<max;i++)
        {
            Chromosome chromo = generator.generateIndividual(run).getChromosome();
            String archive = chromo.toArchiveFormat();
            int diffs = 0;
            char lastDiff = 'a';
            
            assertEquals(archive.length(),baseProgram.length());
            
            for(int j=0,maxj=baseProgram.length();j<maxj;j++)
            {
                char c = baseProgram.charAt(j);
                char d = archive.charAt(j);
                
                if(c!=d)
                {
                    diffs++;
                    lastDiff = d;
                }
            }
            
            assertTrue(diffs <= 1);//there could be 0 diffs
            assertTrue(props.getProperty(CharChromosome.AVAILABLE_GENES).indexOf(lastDiff)>=0);
        }
    }
    
    public void testUniqueGenerator() throws Exception
    {
        Properties props = new Properties();
        props.setProperty(GenetikConstants.CHROMOSOME, CharChromosome.class.getCanonicalName());
        props.setProperty(GenetikConstants.GENES, "5");
        props.setProperty(CharChromosome.AVAILABLE_GENES, "abcdefghijklmnopqrstuvwxyz");
        
        Run run = new Run(props,null);
        UniqueElementGenerator generator = new UniqueElementGenerator();
        
        for(int i=0,max=100;i<max;i++)
        {
            Chromosome chromo = generator.generateIndividual(run).getChromosome();
            String archive = chromo.toArchiveFormat();
            assertEquals(archive.length(),Integer.parseInt(props.getProperty(GenetikConstants.GENES)));
            HashMap<String,String> unique = new HashMap<String,String>();
            String available = props.getProperty(CharChromosome.AVAILABLE_GENES);
            for(int j=0,maxj=archive.length();j<maxj;j++)
            {
                char d = archive.charAt(j);
                assertTrue(available.indexOf(d)>=0);
                
                unique.put(String.valueOf(d),String.valueOf(d));
            }
            
            assertEquals(archive.length(),unique.size());
        }
    }
}
