package com.sasbury.genetik;

import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;

/**
 * The basic optimizers all rely on a termination condition to decide if it is time to stop optimizing.
 * Depending on the problem and fitness tests it is possible to simply run the optimizer through the maximum number
 * of generations, if applicable, or use a termination condition to stop when a solution is found. The former
 * mechanism is useful when there may not be a concrete solution. For example, the DeJong F4 problem has
 * random noise, so it is unlikely that it is "solvable" but optimizers can find a "best" solution.
 * <br><br>
 * The argument for deciding termination is an Individual. This means that the termination condition can 
 * use the fitness or some other criteria to decide if the individual is a "solution." For example, a multi-objective
 * problem might use the raw scores from a individual to determine a solution rather than the fitness which
 * may be a simple pareto ranking that doesn't distinguish between individuals based on which areas they excel at.
 */
public interface TerminationCondition
{
    /**
     * Provided for TCs to validate the run properites.
     * @param run
     * @return
     */
    public String[] validate(Run run);
    
    public boolean matchesTerminationCondition(Individual ind,Run run);
}
