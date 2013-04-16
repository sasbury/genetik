package test.com.sasbury.genetik;

import com.sasbury.genetik.*;
import com.sasbury.genetik.population.*;
import com.sasbury.genetik.skip.*;

import junit.framework.*;

public class SkipTestTests extends TestCase
{
    public void testNoSkip()
    {
        Individual ind = new Individual();
        SkipTest skip = new NoSkip();
        
        assertEquals(false,skip.skip(ind));
    }
    
    public void testSingleSkip()
    {
        Individual ind = new Individual();
        Individual ind2 = new Individual();
        SkipTest skip = new SingleSkip(ind);

        assertEquals(true,skip.skip(ind));
        assertEquals(false,skip.skip(ind2));
    }
    
    public void testMultiSkip()
    {
        Individual ind = new Individual();
        Individual ind2 = new Individual();
        Individual ind3 = new Individual();
        Individual toSkip[] = {ind,ind3};
        
        SkipTest skip = new MultiSkip(toSkip);
        
        assertEquals(true,skip.skip(ind));
        assertEquals(false,skip.skip(ind2));
        assertEquals(true,skip.skip(ind3));
    }
}
