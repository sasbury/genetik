package com.sasbury.genetik.scaling;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;

/**
 * Scales each raw score using rawScore^POWER_FITNESS_SCALE_K, where
 * POWER_FITNESS_SCALE_K is a property in the run configuration
 * file. The scaled scores are averaged.
 * <br><br>
 * By default, K=1;
 */
public class PowerScaling implements FitnessScaling
{
    public static final String POWER_FITNESS_SCALE_K = "power_scale_k";
    
    protected double k;

    public double calculateFitness(Individual individual,Population population,Run run)
    {
        double rawScores[] = individual.getRawScores();
        double total = 0;

        for(int i=0,max=rawScores.length;i<max;i++)
        {
            total += Math.pow(rawScores[i],k);
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
     * Caches power constants
     */
    public void initialize(Population population, Run run)
    {
        String kStr = run.getProperty(POWER_FITNESS_SCALE_K);

        if(kStr == null) k = 1;
        else k = Double.parseDouble(kStr);
    }

    public String[] validate(Run run)
    {
        //has default power scale
        return new String[0];
    }
}
