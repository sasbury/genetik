package test.com.sasbury.genetik.fittests;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;

public class FitTestWithProp implements FitnessTest
{
    public String[] validate(Run run, String testName)
    {
        String err[] = {"Missing testprop for "+testName};
        
        if(run.getPropertyWithPrefix("testprop", testName)!=null)
        {
            return new String[0];
        }
        else
        {
            return err;
        }
    }

    public double calculateRawScore(Individual ind, Run run,
            String testName)
    {
        return 0;
    } 
}
