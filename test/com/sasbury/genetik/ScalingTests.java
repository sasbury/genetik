package test.com.sasbury.genetik;

import java.util.*;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;
import com.sasbury.genetik.scaling.*;

import junit.framework.*;

public class ScalingTests extends TestCase
{
    public void test(Properties props,double rawScores[][],double expected[]) throws Exception
    {
        Population pop = new InMemoryPopulation();
        
        for(int i=0,max=rawScores.length;i<max;i++)
        {
            Individual ind = new Individual();
            ind.setRawScores(rawScores[i]);
            pop.set(-1, ind);
        }
        
        Run run = new Run(props);
        FitnessScaling scaler = (FitnessScaling) run.createObject(GenetikConstants.SCALING);
        
        scaler.initialize(pop, run);
        
        for(int i=0,max=pop.size();i<max;i++)
        {
            Individual ind = pop.get(i);
            double fit =  scaler.calculateFitness(ind, pop, run);
            
            assertEquals(fit, expected[i]);
        }
        
        scaler.cleanup();
        
    }
    
    public void testDefaultScaling() throws Exception
    {
        double rawScores[][] = {{0,1.0},{0.5,1.0},{0,0},{0,0.5}};
        double fitnesses[] = {0.5,0.75,0.0,0.25};
        Properties props = new Properties(Run.createDefaultProperties());
        test(props,rawScores,fitnesses);
    }
    
    public void testNoScaling() throws Exception
    {
        double rawScores[][] = {{0,1.0},{0.5,1.0},{0,0},{0,0.5}};
        double fitnesses[] = {0.5,0.75,0.0,0.25};
        Properties props = new Properties(Run.createDefaultProperties());
        props.put(GenetikConstants.SCALING, NoScaling.class.getCanonicalName());
        test(props,rawScores,fitnesses);
    }
    
    public void testTruncationScaling() throws Exception
    {
        double rawScores[][] = {{-1.0,2.0},{0.5,1.0},{0,0},{0,2.5}};
        double fitnesses[] = {0.5,0.75,0.0,0.5};
        Properties props = new Properties(Run.createDefaultProperties());
        props.put(GenetikConstants.SCALING, TruncationScaling.class.getCanonicalName());
        props.put(TruncationScaling.TRUNCATION_FITNESS_SCALE_MIN, "0.0");
        props.put(TruncationScaling.TRUNCATION_FITNESS_SCALE_MAX, "1.0");
        test(props,rawScores,fitnesses);
    }
    
    public void testLinearScaling() throws Exception
    {
        double rawScores[][] = {{-1.0,2.0},{0.5,1.0},{0,0},{0,2.5}};
        double fitnesses[] = {4.0,4.5,3.0,5.5};
        Properties props = new Properties(Run.createDefaultProperties());
        props.put(GenetikConstants.SCALING, LinearScaling.class.getCanonicalName());
        props.put(LinearScaling.LINEAR_FITNESS_SCALE_A, "2.0");
        props.put(LinearScaling.LINEAR_FITNESS_SCALE_B, "3.0");
        test(props,rawScores,fitnesses);
    }
}
