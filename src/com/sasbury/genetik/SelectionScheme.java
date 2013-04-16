package com.sasbury.genetik;

import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;

/**
 * A selection scheme is used to pick items.
 */
public interface SelectionScheme
{
    /**
     * Called before the selection scheme is used to allow initialization. Called once per generation.
     * @param population the Generation to be scaled
     * @param run the configuration information
     */
    public void initialize(Population population,Run run);

    /**
     * Called to select a program from a population.
     * @param population a generation
     * @param skip a test for individuals to skip (used for crossover and similar operators)
     * @param run configuration information
     * @return the program to use
     */
    public Individual select(Population population,SkipTest skip,Run run);
    
    /**
     * Provided for selections schemes to validate the run properties.
     * @param run
     * @return
     */
    public String[] validate(Run run);

    /**
     * Called after the selection scheme is used, and provided for any cleanup. Called once per generation.
     */
    public void cleanup();
}
