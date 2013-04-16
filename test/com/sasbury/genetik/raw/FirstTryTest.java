package test.com.sasbury.genetik.raw;

import java.io.*;
import java.util.*;

import com.sasbury.genetik.*;
import com.sasbury.genetik.chromosomes.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.validation.*;

public class FirstTryTest
{
    public static void main(String args[])
    {
        try
        {
            String theWord = "helloworld";
            File root = new File("/tmp/firsttest");
            root.mkdir();
            
            Properties props = Run.createDefaultProperties();

            props.setProperty(GenetikConstants.RUNS, "run_0,run_1,run_2,run_3");
            //run_0 will be ga
            props.setProperty("run_1."+GenetikConstants.OPTIMIZER,com.sasbury.genetik.optimize.HillClimb.class.getCanonicalName());
            props.setProperty("run_2."+GenetikConstants.OPTIMIZER,com.sasbury.genetik.optimize.MonteCarlo.class.getCanonicalName());
            props.setProperty("run_3."+GenetikConstants.OPTIMIZER,com.sasbury.genetik.optimize.SimulatedAnnealing.class.getCanonicalName());
            props.setProperty(GenetikConstants.GENERATIONS, "25");
            props.setProperty(GenetikConstants.POPULATION, "1500");
            props.setProperty(GenetikConstants.GENES, String.valueOf(theWord.length()));
            props.setProperty(GenetikConstants.CHROMOSOME, CharChromosome.class.getCanonicalName());
            props.setProperty(CharChromosome.AVAILABLE_GENES,"abcdefghijklmnopqrstuvwxyz");
            
            props.setProperty(GenetikConstants.CUSTOM_REPORTING_RELATIVE_CLASS, WordGuessTest.class.getCanonicalName());
            props.setProperty(GenetikConstants.CUSTOM_REPORTING_RESOURCE_BASE, "/test/com/sasbury/genetik/raw/reporting/");
            props.setProperty(GenetikConstants.CUSTOM_REPORTING_RESOURCES,"wordguess/,wordguess/ind.html,wordguess/run.html,wordguess/summary.html");
            props.setProperty(GenetikConstants.CUSTOM_INDIVIDUAL_REPORT_FRAGMENT, "wordguess/ind.html");
            props.setProperty(GenetikConstants.CUSTOM_RUN_REPORT_FRAGMENT, "wordguess/run.html");
            props.setProperty(GenetikConstants.CUSTOM_SUMMARY_REPORT_FRAGMENT, "wordguess/summary.html");

            props.setProperty(GenetikConstants.TESTS,"wordguess");
            props.setProperty("wordguess.class",WordGuessTest.class.getCanonicalName());
            props.setProperty(WordGuessTest.THE_WORD, theWord);
            
            GenerationalValidator validator = new GenerationalValidator();
            String[] issues = validator.validate(props);
            
            if(issues.length>0)
            {
                for(String s : issues)
                {
                    System.out.println(s);
                }
                
                System.exit(1);
            }
            
            FileBasedGenerationalDriver driver = new FileBasedGenerationalDriver(root);
            driver.execute(props);
        }
        catch(Exception exp)
        {
            exp.printStackTrace();
        }
    }
}
