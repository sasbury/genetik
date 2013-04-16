package com.sasbury.genetik.insertion;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;

public abstract class AbstractInsertionScheme implements InsertionScheme
{
    protected Run run;

    /**
     * Saves reference to description for subclasses.
     * @param population
     * @param desc
     */
    public void initialize(Population population, Run run)
    {
        this.run = run;
    }

    /**
     * No op.
     */
    public void cleanup()
    {
    }

    public Run getRun()
    {
        return run;
    }
}
