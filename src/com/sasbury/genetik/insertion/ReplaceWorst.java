package com.sasbury.genetik.insertion;

import java.util.*;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;

public class ReplaceWorst extends ReplaceBest
{
    /**
     * Reverses the ranks array so that the ReplaceBest algorithm becomes replace worst.
     * @param population
     * @param desc
     */
    public void initialize(Population population, Run run)
    {
        super.initialize(population, run);
        Collections.reverse(ranks);
    }
}
