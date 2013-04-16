package com.sasbury.genetik;

import com.sasbury.genetik.driver.*;

/**
 * 
 * Generational optimizers take the individuals from a population and use them to build a new population. The simpliest optimizer 
 * would just copy each individual forward. But no individual should be placed in two populations at the same time.
 *
 */
public interface GenerationalOptimizer
{
    /**
     * Provided to the optimizer so that it can validate the run properties.
     * @param run
     * @return
     */
    public String[] validate(Run run);
    
    /**
     * The optimizer should do what it needs to to fill in newPop which will start out empty. The new individuals should only have a fitness score if they
     * are unchanged from the previous population.
     * @param oldPop
     * @param newPop
     * @param run the configuration information
     * @param generation The generation count, if appropriate, provided for some optimizers that change as they progress (annealing for example)
     */
    public void generatePopulation(Population oldPop,Population newPop,Run run,int generation);
    
    /**
     * After the new population is scored, this method is called on the optimizer to allow it to alter the new population if necessary.
     * This mechanism is provided for "keep the best" style optimizers. It is ok if this method does nothing.
     * @param oldPop
     * @param newPop
     * @param run the configuration information
     * @param generation The generation count, if appropriate, provided for some optimizers that change as they progress (annealing for example)
     */
    public void finalizePopulation(Population oldPop,Population newPop,Run run,int generation);
}
