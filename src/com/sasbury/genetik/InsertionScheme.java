package com.sasbury.genetik;

import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;

public interface InsertionScheme
{
    /**
     * Called before the insertion scheme is used to allow initialization. Called once per generation.
     * @param population the Population to be inserted into
     * @param run the configuration information
     */
    public void initialize(Population population,Run run);

    /**
     * Add the individual ind into the population using this scheme. Scheme instances should update the id for the indivudual to its new location.
     * @param ind
     * @param population
     */
    public void insertProgramIntoPopulation(Individual ind,Population population);

    /**
     * Called after the insertion scheme is used, and provided for any cleanup. Called once per generation.
     */
    public void cleanup();
}
