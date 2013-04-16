package com.sasbury.genetik;

import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;

public interface IndividualGenerator
{
    public void initialize(Run run,Run previousRun,Population previousPopulation);
    
    public void cleanup();
    
    /**
     * Provided so generators can validate their configuration.
     * @param run
     * @return
     */
    public String[] validate(Run run);
    
    /**
     * Generate a individual using the properties in the run.
     * @param run
     * @return
     */
    public Individual generateIndividual(Run run);
}
