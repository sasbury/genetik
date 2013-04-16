package test.com.sasbury.genetik;

import java.util.*;

import com.sasbury.genetik.*;
import com.sasbury.genetik.chromosomes.*;
import com.sasbury.genetik.population.*;

import junit.framework.*;

public class IndividualTests extends TestCase
{
    public void testCloneAndEquals() throws Exception
    {
        Individual ind = new Individual();

        String archive = "0101010";
        Chromosome chromo = new BinaryChromosome();
        chromo.initalizeFromArchive(archive);
        
        double raw[] = {0.0,2.3,51.98};
        
        ind.setChromosome(chromo);
        ind.setFitness(1.0);
        ind.setParentOne("one");
        ind.setParentTwo("two");
        ind.setRawScores(raw);
        ind.setId("id");
        ind.setUserData("test test test");
        ind.setWorkingData("test 2 3");
        
        Individual clone = ind.duplicate();

        assertNotSame(clone.getId(),"id");
        
        clone.setId(ind.getId());
        
        assertEquals(ind,clone);
        assertEquals(clone.getFitness(),1.0);
        assertEquals(clone.getParentOne(),"one");
        assertEquals(clone.getParentTwo(),"two");
        assertEquals(clone.getUserData(),"test test test");
        assertEquals(clone.getWorkingData(),null);
        assertTrue(Arrays.equals(raw, clone.getRawScores()));
        assertEquals(clone.getChromosome().toArchiveFormat(),archive);
    }
}
