package com.sasbury.genetik;

import com.sasbury.genetik.population.*;

/**
 * SkipTests are used when one ore more programs may need to be skipped, particularly in selection schemes.
 *
 */
public interface SkipTest
{
    public boolean skip(Individual individual);
}
