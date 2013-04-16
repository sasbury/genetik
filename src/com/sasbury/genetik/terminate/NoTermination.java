package com.sasbury.genetik.terminate;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;

/**
 * This termination condition will never return true.
 *
 */
public class NoTermination implements TerminationCondition
{    
    public String[] validate(Run run)
    {
        return new String[0];
    }
    
    public boolean matchesTerminationCondition(Individual ind, Run run)
    {
        return false;
    }

}
