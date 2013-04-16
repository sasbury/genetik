package test.com.sasbury.genetik.fittests;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;

public class ValidFitTest implements FitnessTest
{
    public String[] validate(Run run, String testName)
    {
        return new String[0];
    }

    public double calculateRawScore(Individual ind, Run run,
            String testName)
    {
        return 0;
    } 
}
