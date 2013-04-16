package test.com.sasbury.genetik;

import java.util.*;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;
import com.sasbury.genetik.terminate.*;

import junit.framework.*;

public class TerminationTests extends TestCase implements GenetikConstants
{
    public void testNoTerminate()
    {
        Properties props = new Properties();
        Run run = new Run(props);
        Individual individual = new Individual();
        NoTermination test = new NoTermination();
        
        assertEquals(false, test.matchesTerminationCondition(individual, run));
    }
    
    public void testMaxFitnessTerminate()
    {
        Properties props = new Properties();
        props.setProperty(FitnessValueTermination.MAX_TERMINAL_FITNESS, "1.0");
        Run run = new Run(props);
        Individual individual = new Individual();
        FitnessValueTermination test = new FitnessValueTermination();

        individual.setFitness(1.0);
        assertEquals(true, test.matchesTerminationCondition(individual, run));
        
        individual.setFitness(0.0);
        assertEquals(true, test.matchesTerminationCondition(individual, run));
        
        individual.setFitness(2.0);
        assertEquals(false, test.matchesTerminationCondition(individual, run));
    }
    
    public void testMinFitnessTerminate()
    {
        Properties props = new Properties();
        props.setProperty(FitnessValueTermination.MIN_TERMINAL_FITNESS, "0.0");
        Run run = new Run(props);
        Individual individual = new Individual();
        FitnessValueTermination test = new FitnessValueTermination();

        individual.setFitness(1.0);
        assertEquals(true, test.matchesTerminationCondition(individual, run));
        
        individual.setFitness(0.0);
        assertEquals(true, test.matchesTerminationCondition(individual, run));
        
        individual.setFitness(-2.0);
        assertEquals(false, test.matchesTerminationCondition(individual, run));
    }
    
    public void testMinMaxFitnessTerminate()
    {
        Properties props = new Properties();
        props.setProperty(FitnessValueTermination.MIN_TERMINAL_FITNESS, "1.0");
        props.setProperty(FitnessValueTermination.MAX_TERMINAL_FITNESS, "0.0");
        Run run = new Run(props);
        Individual individual = new Individual();
        FitnessValueTermination test = new FitnessValueTermination();

        individual.setFitness(1.0);
        assertEquals(true, test.matchesTerminationCondition(individual, run));
        
        individual.setFitness(0.0);
        assertEquals(true, test.matchesTerminationCondition(individual, run));
        
        individual.setFitness(0.5);
        assertEquals(false, test.matchesTerminationCondition(individual, run));
    }
}
