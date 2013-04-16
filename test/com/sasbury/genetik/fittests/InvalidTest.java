package test.com.sasbury.genetik.fittests;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;

public class InvalidTest implements FitnessTest
{
    public String[] validate(Run run, String testName)
    {
        String err[] = {testName+" is always invalid."};
        return err;
    }

    public double calculateRawScore(Individual ind, Run run,
            String testName)
    {
        return 0;
    } 
}
