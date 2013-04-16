package test.com.sasbury.genetik;

import java.util.*;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;
import com.sasbury.genetik.selection.*;
import com.sasbury.genetik.skip.*;

import junit.framework.*;

public class SelectionTests extends TestCase
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
    
    public void testDefaultSelectionIsTournament() throws Exception
    {
        Properties props = new Properties(Run.createDefaultProperties());
        Run run = new Run(props);
        SelectionScheme scheme = (SelectionScheme) run.createObject(GenetikConstants.SELECTION);
        
        assertEquals(scheme.getClass().getName(),TournamentSelection.class.getCanonicalName());
    }
    
    public void testRandomWithSkip() throws Exception
    {
        Properties props = new Properties(Run.createDefaultProperties());
        props.put(GenetikConstants.SELECTION, RandomSelection.class.getCanonicalName());
        
        Run run = new Run(props);
        Population pop = buildPopulation();
        SelectionScheme scheme = (SelectionScheme) run.createObject(GenetikConstants.SELECTION);
        
        scheme.initialize(pop, run);
        
        SkipTest skip = new SingleSkip(pop.get(0));
        Individual last = null;
        int diffs = 0;
        
        for(int i=0,max=1000;i<max;i++)
        {
            Individual ind = scheme.select(pop, skip, run);
            
            assertTrue(!skip.skip(ind));
            
            if(ind != last) diffs++;
            
            last = ind;
        }
        
        assertTrue(diffs > 10);//arbitrary number to indicate randomness
        
        scheme.cleanup();
    }
    
    public void testRouletteWithSkip() throws Exception
    {
        Properties props = new Properties(Run.createDefaultProperties());
        props.put(GenetikConstants.SELECTION, RouletteSelection.class.getCanonicalName());
        
        Run run = new Run(props);
        Population pop = buildPopulation();
        SelectionScheme scheme = (SelectionScheme) run.createObject(GenetikConstants.SELECTION);
        
        scheme.initialize(pop, run);
        
        SkipTest skip = new SingleSkip(pop.get(0));
        Individual last = null;
        int diffs = 0;
        
        for(int i=0,max=1000;i<max;i++)
        {
            Individual ind = scheme.select(pop, skip, run);
            
            assertTrue(!skip.skip(ind));
            
            if(ind != last) diffs++;
            
            last = ind;
        }
        
        assertTrue(diffs > 10);//arbitrary number to indicate randomness
        
        scheme.cleanup();
    }
    
    public void testStochasticWithSkip() throws Exception
    {
        Properties props = new Properties(Run.createDefaultProperties());
        props.put(GenetikConstants.SELECTION, StochasticUniversalSelection.class.getCanonicalName());
        
        Run run = new Run(props);
        Population pop = buildPopulation();
        SelectionScheme scheme = (SelectionScheme) run.createObject(GenetikConstants.SELECTION);
        
        scheme.initialize(pop, run);
        
        SkipTest skip = new SingleSkip(pop.get(0));
        Individual last = null;
        int diffs = 0;
        
        for(int i=0,max=1000;i<max;i++)
        {
            Individual ind = scheme.select(pop, skip, run);
            
            assertTrue(!skip.skip(ind));
            
            if(ind != last) diffs++;
            
            last = ind;
        }
        
        assertTrue(diffs > 10);//arbitrary number to indicate randomness
        
        scheme.cleanup();
    }
    
    public void testTruncationWithSkip() throws Exception
    {
        Properties props = new Properties(Run.createDefaultProperties());
        props.put(GenetikConstants.SELECTION, TruncationSelection.class.getCanonicalName());
        
        Run run = new Run(props);
        Population pop = buildPopulation();
        SelectionScheme scheme = (SelectionScheme) run.createObject(GenetikConstants.SELECTION);
        
        scheme.initialize(pop, run);
        
        SkipTest skip = new SingleSkip(pop.get(50));
        Individual last = null;
        int diffs = 0;
        
        for(int i=0,max=1000;i<max;i++)
        {
            Individual ind = scheme.select(pop, skip, run);

            assertTrue(!skip.skip(ind));
            assertTrue(ind.getFitness()>=0.25);
            
            if(ind != last) diffs++;
            
            last = ind;
        }
        
        assertTrue(diffs > 10);//arbitrary number to indicate randomness
        
        scheme.cleanup();
    }
    
    public void testRankWithSkip() throws Exception
    {
        Properties props = new Properties(Run.createDefaultProperties());
        props.put(GenetikConstants.SELECTION, RankSelection.class.getCanonicalName());
        
        Run run = new Run(props);
        Population pop = buildPopulation();
        SelectionScheme scheme = (SelectionScheme) run.createObject(GenetikConstants.SELECTION);
        
        scheme.initialize(pop, run);
        
        SkipTest skip = new SingleSkip(pop.get(pop.size()-1));
        
        for(int i=0,max=1000;i<max;i++)
        {
            Individual ind = scheme.select(pop, skip, run);
            
            assertTrue(!skip.skip(ind));
            
            assertTrue(ind == pop.get(pop.size()-2));
        }
        
        scheme.cleanup();
    }
    
    public void testRankWithoutSkip() throws Exception
    {
        Properties props = new Properties(Run.createDefaultProperties());
        props.put(GenetikConstants.SELECTION, RankSelection.class.getCanonicalName());
        
        Run run = new Run(props);
        Population pop = buildPopulation();
        SelectionScheme scheme = (SelectionScheme) run.createObject(GenetikConstants.SELECTION);
        
        scheme.initialize(pop, run);
        for(int i=0,max=1000;i<max;i++)
        {
            Individual ind = scheme.select(pop, null, run);
            
            assertTrue(ind.getFitness() == 1.0);
        }
        
        scheme.cleanup();
    }
    
    public void testTournPickMinMax()
    {
        TournamentSelection sel = new TournamentSelection();
        ArrayList<Individual> tournament = new ArrayList<Individual>();

        for(int i=0,max=11;i<max;i++)
        {
            Individual ind = new Individual();
            ind.setFitness(((double)i)/10.0);
            tournament.add(ind);
        }
        
        Individual ind = sel.pickFromTournamentMax(tournament);
        
        assertEquals(ind.getFitness(),1.0);

        ind = sel.pickFromTournamentMin(tournament);
        assertEquals(ind.getFitness(),0.0);   
    }

    
    public void testTournamentWithSkip() throws Exception
    {
        Properties props = new Properties(Run.createDefaultProperties());
        props.put(GenetikConstants.SELECTION, TournamentSelection.class.getCanonicalName());
        
        Run run = new Run(props);
        Population pop = buildPopulation();
        SelectionScheme scheme = (SelectionScheme) run.createObject(GenetikConstants.SELECTION);
        
        scheme.initialize(pop, run);
        
        SkipTest skip = new SingleSkip(pop.get(45));
        Individual last = null;
        int diffs = 0;
        
        for(int i=0,max=1000;i<max;i++)
        {
            Individual ind = scheme.select(pop, skip, run);
            
            assertTrue(!skip.skip(ind));
            
            if(ind != last) diffs++;
            
            last = ind;
        }
        
        assertTrue(diffs > 50);//arbitrary number to indicate randomness
        
        scheme.cleanup();
    }
}
