package com.sasbury.genetik.scaling;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;

/**
 * Truncates the raw scores between TRUNCATION_FITNESS_SCALE_MAX and TRUNCATION_FITNESS_SCALE_MIN, then averages the
 * raw scores.
 * <br><br>
 * Min defaults to 0, max defaults to 1.
 */
public class TruncationScaling implements FitnessScaling
{
    public static final String TRUNCATION_FITNESS_SCALE_MAX = "truncation_scale_max";
    public static final String TRUNCATION_FITNESS_SCALE_MIN = "truncation_scale_min";
    
    protected double max;
    protected double min;

    public double calculateFitness(Individual individual,Population population,Run run)
    {
        double rawScores[] = individual.getRawScores();
        double raw;
        double total = 0;

        for(int i=0,scores=rawScores.length;i<scores;i++)
        {
            raw = rawScores[i];
            raw = Math.min(raw,max);
            raw = Math.max(raw,min);

            total += raw;
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
        String aStr = run.getProperty(TRUNCATION_FITNESS_SCALE_MAX);
        String bStr = run.getProperty(TRUNCATION_FITNESS_SCALE_MIN);

        if(aStr == null) max = 1;
        else max = Double.parseDouble(aStr);
        
        if(bStr == null) min = 0;
        else min = Double.parseDouble(bStr);
    }

    public String[] validate(Run run)
    {
        //has default scales
        return new String[0];
    }
}
