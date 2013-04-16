package test.com.sasbury.genetik.raw;

import com.sasbury.genetik.*;
import com.sasbury.genetik.chromosomes.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;

public class SleepyWordGuessTest implements FitnessTest
{
    public static final String THE_WORD="the_word";
    
    public double calculateRawScore(Individual ind, Run run, String testName)
    {
        String theWord = run.getPropertyWithPrefix(THE_WORD, testName);
        CharChromosome charChromo = (CharChromosome) ind.getChromosome();
        String test = charChromo.asString();
        double retVal = 1;
        
        for(int i=0,max=theWord.length();i<max;i++)
        {
            if(test.length()>i)
            {
                int one = (int)theWord.charAt(i);
                int two = (int)test.charAt(i);
                int toAdd = Math.abs(one-two);
                retVal-= toAdd;
            }
        }

        if(test.length()>theWord.length())
        {
            retVal -= (test.length()-theWord.length());
        }
        else if(test.length()<theWord.length())
        {
            retVal -= (theWord.length()-test.length());
        }
        
        try
        {
            Thread.sleep(1000 * 30);//sleep 30 s
        }
        catch(Exception exp)
        {
            //ignore it
        }
        return retVal;
    }

    public String[] validate(Run run, String testName)
    {
        String err[] = {"Missing value of "+THE_WORD+" for "+testName};
        
        if(run.getPropertyWithPrefix(THE_WORD, testName)!=null)
        {
            return new String[0];
        }
        else
        {
            return err;
        }
    }

}
