package test.com.sasbury.genetik;

import junit.framework.*;

import com.sasbury.genetik.population.*;

public class ComparatorTests extends TestCase
{
    public void testFitnessComparison()
    {
        Individual zero = new Individual();
        zero.setFitness(0.0);

        Individual half = new Individual();
        half.setFitness(0.5);
        
        Individual one = new Individual();
        one.setFitness(1.0);
        
        Individual anotherOne = new Individual();
        anotherOne.setFitness(1.0);
        
        IndividualComparator compare = new IndividualComparator();

        assertEquals(compare.compare(zero,one) , -1);
        assertEquals(compare.compare(half,one) , -1);
        assertEquals(compare.compare(half,zero) , 1);
        assertEquals(compare.compare(one,zero) , 1);
        assertEquals(compare.compare(anotherOne,one) , 0);
    }
    
    public void testScoreComparison()
    {
        Individual zero = new Individual();
        double[] raw = {0.0,1.0};
        zero.setRawScores(raw);

        Individual half = new Individual();
        double[] raw2 = {0.5,0.5};
        half.setRawScores(raw2);
        
        Individual one = new Individual();
        double[] raw3 = {1.0,0.0};
        one.setRawScores(raw3);
        
        Individual anotherOne = new Individual();
        double[] raw4 = {1.0,0.0};
        anotherOne.setRawScores(raw4);
        
        IndividualComparator compare = new IndividualComparator(0);

        assertEquals(compare.compare(zero,one) , -1);
        assertEquals(compare.compare(half,one) , -1);
        assertEquals(compare.compare(half,zero) , 1);
        assertEquals(compare.compare(one,zero) , 1);
        assertEquals(compare.compare(anotherOne,one) , 0);
        
        compare = new IndividualComparator(1);

        assertEquals(compare.compare(zero,one) , 1);
        assertEquals(compare.compare(half,one) , 1);
        assertEquals(compare.compare(half,zero) , -1);
        assertEquals(compare.compare(one,zero) , -1);
        assertEquals(compare.compare(anotherOne,one) , 0);
    }
}
