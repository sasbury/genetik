package com.sasbury.genetik.skip;

import com.sasbury.genetik.*;
import com.sasbury.genetik.population.*;

/**
 * Doesn't skip any individuals.
 */
public class NoSkip implements SkipTest
{
    public boolean skip(Individual individual)
    {
        return false;
    }

}
