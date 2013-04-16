package com.sasbury.genetik.scaling;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;

/**
 * Scales each raw score using LINEAR_FITNESS_SCALE_A*rawScore + LINEAR_FITNESS_SCALE_B, where
 * LINEAR_FITNESS_SCALE_A is a property in the run configuration
 * file. The scaled scores are averaged.
 * <br><br>
 * By default, A=1 and B=0;
 */
public class LinearScaling implements FitnessScaling
{
    public static final String LINEAR_FITNESS_SCALE_A = "linear_scale_a";
    public static final String LINEAR_FITNESS_SCALE_B = "linear_scale_b";
    
    protected double a;
    protected double b;

    public double calculateFitness(Individual individual,Population population,Run run)
    {
        double rawScores[] = individual.getRawScores();
        double total = 0;

        for(int i=0,max=rawScores.length;i<max;i++)
        {
            total += a*rawScores[i]+b;
        }

        total /= (double)rawScores.length;

        return total;
    }

    /**
     * No op.
     */
    public void cleanup()
    {

    }

    /**
     * Caches linear constants
     */
    public void initialize(Population population, Run run)
    {
        String aStr = run.getProperty(LINEAR_FITNESS_SCALE_A);
        String bStr = run.getProperty(LINEAR_FITNESS_SCALE_B);

        if(aStr == null) a = 1;
        else a = Double.parseDouble(aStr);
        
        if(bStr == null) b = 0;
        else b = Double.parseDouble(bStr);
    }

    public String[] validate(Run run)
    {
        //has default values for A and B
        return new String[0];
    }
}
