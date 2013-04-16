package test.com.sasbury.genetik;

import java.util.*;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.insertion.*;
import com.sasbury.genetik.population.*;

import junit.framework.*;

public class InsertionTests extends TestCase
{
    public Population buildPopulation()
    {
        InMemoryPopulation pop = new InMemoryPopulation();
        
        for(int i=0,max=101;i<max;i++)
        {
            Individual ind = new Individual();
            ind.setFitness(((double)i)/100.0);
            pop.set(-1,ind);
        }
        
        return pop;
    }
    
    public void testParentInsertion()
    {
        Properties props = new Properties(Run.createDefaultProperties());
        props.put(GenetikConstants.INSERTION, ParentInsertion.class.getCanonicalName());
        
        Run run = new Run(props);
        Population pop = buildPopulation();
        InsertionScheme scheme = (InsertionScheme) run.createObject(GenetikConstants.INSERTION);
        
        scheme.initialize(pop, run);
        
        Individual one = pop.get(0);
        Individual child = new Individual();
        child.setParentOne(one.getId());
        
        scheme.insertProgramIntoPopulation(child, pop);
        
        Individual newOne = pop.get(0);
        assert(newOne == child);
    }
    
    public void testReplaceBestInsertion()
    {
        Properties props = new Properties(Run.createDefaultProperties());
        props.put(GenetikConstants.INSERTION, ReplaceBest.class.getCanonicalName());
        
        Run run = new Run(props);
        Population pop = buildPopulation();
        InsertionScheme scheme = (InsertionScheme) run.createObject(GenetikConstants.INSERTION);
        
        scheme.initialize(pop, run);
        
        Individual child = new Individual();
        
        scheme.insertProgramIntoPopulation(child, pop);
        
        Individual newOne = pop.get(pop.size()-1);
        assert(newOne == child);
    }
    
    public void testReplaceWorstInsertion()
    {
        Properties props = new Properties(Run.createDefaultProperties());
        props.put(GenetikConstants.INSERTION, ReplaceBest.class.getCanonicalName());
        
        Run run = new Run(props);
        Population pop = buildPopulation();
        InsertionScheme scheme = (InsertionScheme) run.createObject(GenetikConstants.INSERTION);
        
        scheme.initialize(pop, run);
        
        Individual child = new Individual();
        
        scheme.insertProgramIntoPopulation(child, pop);
        
        Individual newOne = pop.get(0);
        assert(newOne == child);
    }
    
    public void testRandomInsertion()
    {
        Properties props = new Properties(Run.createDefaultProperties());
        props.put(GenetikConstants.INSERTION, RandomInsertion.class.getCanonicalName());
        
        Run run = new Run(props);
        Population pop = buildPopulation();
        InsertionScheme scheme = (InsertionScheme) run.createObject(GenetikConstants.INSERTION);
        
        scheme.initialize(pop, run);
        
        Individual child = new Individual();
        int sizeBefore = pop.size();
        
        scheme.insertProgramIntoPopulation(child, pop);
        
        assertEquals(sizeBefore, pop.size());
       
        int count = 0;
        
        for(int i=0,max=pop.size();i<max;i++)
        {
            Individual ind = pop.get(i);
            
            if(ind == child) count++;
        }
        
        assertEquals(1,count);
    }
}
