package com.sasbury.genetik.terminate;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;

/**
 * Matches programs against the MIN_TERMINAL_FITNESS and MAX_TERMINAL_FITNESS properties. If the fitness is greater than the
 * minimum fitness, the termination condition is met. Likewise if the fitness is less than the maximum fitness the termination condition is met.
 * The default maximum is Double.NEGATIVE_INFINITY, and the default minimum is Double.POSITIVE_INFINITY. In general, only one value needs to be set depending
 * on the fitness functions increasing/decreasing characteristics.
 *
 */
public class FitnessValueTermination implements TerminationCondition
{
    public static final String MIN_TERMINAL_FITNESS = "fitness_termination_min";
    public static final String MAX_TERMINAL_FITNESS = "fitness_termination_max";

    public String[] validate(Run run)
    {
        double min = run.getDoubleProperty(MIN_TERMINAL_FITNESS, Double.POSITIVE_INFINITY, true);
        double max = run.getDoubleProperty(MAX_TERMINAL_FITNESS, Double.NEGATIVE_INFINITY, true);
        
        if(min == Double.POSITIVE_INFINITY)
        {
            String err[] = {"Run with fitness value termination has invalid "+MIN_TERMINAL_FITNESS+" property."};
            return err;
        }
        
        if(max == Double.NEGATIVE_INFINITY)
        {
            String err[] = {"Run with fitness value termination has invalid "+MAX_TERMINAL_FITNESS+" property."};
            return err;
        }
        
        return new String[0];
    }
    
    public boolean matchesTerminationCondition(Individual ind,Run run)
    {
        boolean retVal = false;
        String minStr = run.getProperty(MIN_TERMINAL_FITNESS);
        String maxStr = run.getProperty(MAX_TERMINAL_FITNESS);
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        
        if(minStr != null) min = Double.parseDouble(minStr);
        if(maxStr != null) max = Double.parseDouble(maxStr);
        
        double fit = ind.getFitness();
        
        if(fit>=min || fit<=max)
        {
            retVal = true;
        }

        return retVal;
    }
}
