package com.sasbury.genetik;

import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;

/**
 * FitnessScaling objects are used to generate the fitness for a program based on the output of the
 * fitness tests.
 *
 * Since mutiple tests are allowed, most scalings will scale each raw score and take an average. But that
 * behavior is defined by the scaling object and could be changed.
 */
public interface FitnessScaling
{
    /**
     * Called before the scaling is used to allow initialization. Called once per population.
     * @param population the population to be scaled
     * @param run the configuration information
     */
    public void initialize(Population population,Run run);
    
    public String[] validate(Run run);

    /**
     * Calculates the fitness for a single program in a generation.
     * @param individual an individual
     * @param population the individuals population
     * @param run the configuration information
     * @return the fitness score
     */
    public double calculateFitness(Individual individual,Population population,Run run);

    /**
     * Called after the scaling is used, and provided for any cleanup. Called once per generation.
     */
    public void cleanup();
}
