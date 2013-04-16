package test.com.sasbury.genetik;

import com.sasbury.genetik.*;
import com.sasbury.genetik.chromosomes.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.generator.*;
import com.sasbury.genetik.optimize.*;
import com.sasbury.genetik.terminate.*;
import com.sasbury.genetik.validation.*;

import java.util.*;

import test.com.sasbury.genetik.processors.*;

import junit.framework.*;

public class ValidationTests extends TestCase
{
    public static Properties genTestProps()
    {
        Properties props = Run.createDefaultProperties();
        props.put(GenetikConstants.GENERATIONS, "10");
        props.put(GenetikConstants.POPULATION, "10");
        props.put(GenetikConstants.GENES, "10");
        props.put(GenetikConstants.GENERATOR, RandomGenerator.class.getCanonicalName());
        props.put(GenetikConstants.CHROMOSOME, BinaryChromosome.class.getCanonicalName());
        props.put(GenetikConstants.TESTS, "test1,test2");
        props.put("test1."+GenetikConstants.CLASS, "test.com.sasbury.genetik.fittests.ValidFitTest");
        props.put("test2."+GenetikConstants.CLASS, "test.com.sasbury.genetik.fittests.FitTestWithProp");
        props.put("test2.testprop", "aValue");
        return props;
    }
    
    public void testNoErrors()
    {
        Properties props = genTestProps();
        GenerationalValidator validator = new GenerationalValidator();
        String[] issues = validator.validate(props);
        
        assertEquals(issues.length, 0);
    }
    
    public void testDuplicateRunNames()
    {
        Properties props = genTestProps();
        props.put(GenetikConstants.RUNS, "run1,run1");
        GenerationalValidator validator = new GenerationalValidator();
        String[] issues = validator.validate(props);
        
        assertEquals(issues.length, 1);
    }
    
    public void testBadGenerations()
    {
        Properties props = genTestProps();
        props.put(GenetikConstants.GENERATIONS,"badvalue");
        GenerationalValidator validator = new GenerationalValidator();
        String[] issues = validator.validate(props);
        
        assertEquals(issues.length, 1);
    }
    
    public void testBadGenes()
    {
        Properties props = genTestProps();
        props.put(GenetikConstants.GENES,"badvalue");
        GenerationalValidator validator = new GenerationalValidator();
        String[] issues = validator.validate(props);
        
        assertEquals(issues.length, 1);
    }
    
    public void testBadScaling()
    {
        Properties props = genTestProps();
        props.remove(GenetikConstants.SCALING);
        GenerationalValidator validator = new GenerationalValidator();
        String[] issues = validator.validate(props);
        
        assertEquals(issues.length, 1);
    }
    
    public void testBadPopulation()
    {
        Properties props = genTestProps();
        props.put(GenetikConstants.POPULATION, "bad Value");
        GenerationalValidator validator = new GenerationalValidator();
        String[] issues = validator.validate(props);
        
        assertEquals(issues.length, 1);
    }

    public void testMissingTests()
    {
        Properties props = genTestProps();
        props.remove(GenetikConstants.TESTS);
        GenerationalValidator validator = new GenerationalValidator();
        String[] issues = validator.validate(props);
        
        assertEquals(issues.length, 1);
    }
    
    public void testInvalidTest()
    {
        Properties props = genTestProps();
        props.put(GenetikConstants.TESTS, "test1");
        props.put("test1."+GenetikConstants.CLASS, "test.com.sasbury.genetik.fittests.InvalidTest");
        GenerationalValidator validator = new GenerationalValidator();
        String[] issues = validator.validate(props);
        
        assertEquals(issues.length, 1);
    }
    
    public void testInvalidTestProp()
    {
        Properties props = genTestProps();
        props.remove("test2.testprop");
        GenerationalValidator validator = new GenerationalValidator();
        String[] issues = validator.validate(props);
        
        assertEquals(issues.length, 1);
    }
    
    public void testMissingIntProps()
    {
        Properties props = genTestProps();
        props.put(GenetikConstants.CHROMOSOME, IntegerChromosome.class.getCanonicalName());
        props.put(IntegerChromosome.MIN_GENE, "1");
        GenerationalValidator validator = new GenerationalValidator();
        String[] issues = validator.validate(props);
        
        assertEquals(issues.length, 1);
    }
    
    public void testValidIntProps()
    {
        Properties props = genTestProps();
        props.put(GenetikConstants.CHROMOSOME, IntegerChromosome.class.getCanonicalName());
        props.put(IntegerChromosome.MIN_GENE, "1");
        props.put(IntegerChromosome.MAX_GENE, "10");
        GenerationalValidator validator = new GenerationalValidator();
        String[] issues = validator.validate(props);
        
        assertEquals(issues.length, 0);
    }
    
    public void testInValidIntProps()
    {
        Properties props = genTestProps();
        props.put(GenetikConstants.CHROMOSOME, IntegerChromosome.class.getCanonicalName());
        props.put(IntegerChromosome.MIN_GENE, "1");
        props.put(IntegerChromosome.MAX_GENE, "1");
        GenerationalValidator validator = new GenerationalValidator();
        String[] issues = validator.validate(props);
        
        assertEquals(issues.length, 1);
    }
    
    public void testMissingFPProps()
    {
        Properties props = genTestProps();
        props.put(GenetikConstants.CHROMOSOME, DoubleChromosome.class.getCanonicalName());
        props.put(IntegerChromosome.MIN_GENE, "1");
        GenerationalValidator validator = new GenerationalValidator();
        String[] issues = validator.validate(props);
        
        assertEquals(issues.length, 1);
    }
    
    public void testValidFPProps()
    {
        Properties props = genTestProps();
        props.put(GenetikConstants.CHROMOSOME, DoubleChromosome.class.getCanonicalName());
        props.put(IntegerChromosome.MIN_GENE, "1");
        props.put(IntegerChromosome.MAX_GENE, "10");
        GenerationalValidator validator = new GenerationalValidator();
        String[] issues = validator.validate(props);
        
        assertEquals(issues.length, 0);
    }
    
    public void testInValidFPProps()
    {
        Properties props = genTestProps();
        props.put(GenetikConstants.CHROMOSOME, DoubleChromosome.class.getCanonicalName());
        props.put(IntegerChromosome.MIN_GENE, "1");
        props.put(IntegerChromosome.MAX_GENE, "1");
        GenerationalValidator validator = new GenerationalValidator();
        String[] issues = validator.validate(props);
        
        assertEquals(issues.length, 1);
    }
    
    public void testMissingCharChromoProps()
    {
        Properties props = genTestProps();
        props.put(GenetikConstants.CHROMOSOME, CharChromosome.class.getCanonicalName());
        GenerationalValidator validator = new GenerationalValidator();
        String[] issues = validator.validate(props);
        
        assertEquals(issues.length, 1);
    }
    
    public void testValidCharChromoProps()
    {
        Properties props = genTestProps();
        props.put(GenetikConstants.CHROMOSOME, CharChromosome.class.getCanonicalName());
        props.put(CharChromosome.AVAILABLE_GENES, "abcdefg");
        GenerationalValidator validator = new GenerationalValidator();
        String[] issues = validator.validate(props);
        
        assertEquals(issues.length, 0);
    }
    
    public void testInValidCharChromoProps()
    {
        Properties props = genTestProps();
        props.put(GenetikConstants.CHROMOSOME, CharChromosome.class.getCanonicalName());
        props.put(CharChromosome.AVAILABLE_GENES, "");
        GenerationalValidator validator = new GenerationalValidator();
        String[] issues = validator.validate(props);
        
        assertEquals(issues.length, 1);
    }
    
    public void testBadTerminator()
    {
        Properties props = genTestProps();
        props.put(GenetikConstants.TERMINATION, FitnessValueTermination.class.getCanonicalName());
        GenerationalValidator validator = new GenerationalValidator();
        String[] issues = validator.validate(props);
        
        assertEquals(issues.length, 1);
    }
    
    public void testValidTerminator()
    {
        Properties props = genTestProps();
        props.put(GenetikConstants.TERMINATION, FitnessValueTermination.class.getCanonicalName());
        props.put(FitnessValueTermination.MAX_TERMINAL_FITNESS, "1.0");
        props.put(FitnessValueTermination.MIN_TERMINAL_FITNESS, "-1.0");
        GenerationalValidator validator = new GenerationalValidator();
        String[] issues = validator.validate(props);
        
        assertEquals(issues.length, 0);
    }
    
    public void testInvalidSelctionForGA()
    {
        Properties props = genTestProps();
        props.put(GenetikConstants.SELECTION,"badvalue");
        GenerationalValidator validator = new GenerationalValidator();
        String[] issues = validator.validate(props);
        
        assertEquals(issues.length, 1);
    }
    
    public void testInvalidOperationsForGA()
    {
        Properties props = genTestProps();
        props.put(GenerationalGA.OPERATIONS,"badvalue");
        GenerationalValidator validator = new GenerationalValidator();
        String[] issues = validator.validate(props);
        
        assertEquals(issues.length, 2);//1 for op, 1 for probability of op
    }
    
    public void testInvalidProbablilitesForGA()
    {
        Properties props = genTestProps();
        props.setProperty("copy."+GenerationalGA.PROBABILITY, "bad");
        GenerationalValidator validator = new GenerationalValidator();
        String[] issues = validator.validate(props);
        
        assertEquals(issues.length, 1);
    }
    
    public void testGoodProcessor()
    {
        Properties props = genTestProps();
        props.setProperty(GenetikConstants.PREPROCESSOR, GoodProcessor.class.getCanonicalName());
        props.setProperty(GenetikConstants.POSTPROCESSOR, GoodProcessor.class.getCanonicalName());
        GenerationalValidator validator = new GenerationalValidator();
        String[] issues = validator.validate(props);
        
        assertEquals(issues.length, 0);
    }
    
    public void testInvalidProcessor()
    {
        Properties props = genTestProps();
        props.setProperty(GenetikConstants.PREPROCESSOR, "badvalue");
        GenerationalValidator validator = new GenerationalValidator();
        String[] issues = validator.validate(props);
        
        assertEquals(issues.length, 1);
    }
    
    public void testBadProcessor()
    {
        Properties props = genTestProps();
        props.setProperty(GenetikConstants.PREPROCESSOR, BadProcessor.class.getCanonicalName());
        props.setProperty(GenetikConstants.POSTPROCESSOR, BadProcessor.class.getCanonicalName());
        GenerationalValidator validator = new GenerationalValidator();
        String[] issues = validator.validate(props);
        
        assertEquals(issues.length, 2);
    }
    
    public void testPropBasedProcessor()
    {
        Properties props = genTestProps();
        props.setProperty(GenetikConstants.PREPROCESSOR, PropBasedProcessor.class.getCanonicalName());
        props.setProperty(GenetikConstants.POSTPROCESSOR, PropBasedProcessor.class.getCanonicalName());
        GenerationalValidator validator = new GenerationalValidator();
        String[] issues = validator.validate(props);
        
        assertEquals(issues.length, 2);
        
        props.setProperty(PropBasedProcessor.PROP,"value");
        
        issues = validator.validate(props);
        
        assertEquals(issues.length, 0);
    }
}
