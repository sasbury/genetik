package com.sasbury.genetik.scaling;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;

/**
 * Calculates a simple average of the raw scores with no scaling.
 */
public class NoScaling implements FitnessScaling
{
    public double calculateFitness(Individual individual,Population population,Run run)
    {
        double rawScores[] = individual.getRawScores();
        double total = 0;

        for(int i=0,max=rawScores.length;i<max;i++)
        {
            total += rawScores[i];
        }

        total = total / ((double)rawScores.length);

        return total;
    }

    /**
     * No op.
     */
    public void cleanup()
    {

    }

    /**
     * No Op
     * @param population
     * @param desc
     */
    public void initialize(Population population, Run run)
    {

    }

    public String[] validate(Run run)
    {
        return new String[0];
    }
}
