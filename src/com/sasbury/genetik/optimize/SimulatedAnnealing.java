package com.sasbury.genetik.optimize;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;

/**
 * Extends hillclimb but changes the keep algorithm. The best programs are always kept,
 * Sometimes the worse programs are kept. Chosing to keep the worse value is based on an annealing probability.
 * This value starts at one and is updated by ANNEALING_SCALE for each loop.
 * The probability of keeping the new value is:
 * <br><br>
 *           probability = Math.exp(-(fitnessOld-fitnessNew)/anneal);
 * <br><br>
 * The default annealing scale is 0.95.
 */
public class SimulatedAnnealing extends HillClimb
{
    public static final String ANNEALING_SCALE = "annealing";
    
    public void finalizePopulation(Population oldPop, Population newPop, Run run, int generation)
    {
        if(oldPop == null || oldPop.size()==0) return;
        
        String scaleStr = run.getProperty(ANNEALING_SCALE);
        
        double scale = 0.95;
        
        if(scaleStr != null) scale = Double.parseDouble(scaleStr);
        
        double anneal = Math.pow(scale,generation);
        
        for(int i=0,max=newPop.size();i<max;i++)
        {
            Individual newInd = newPop.get(i);
            Individual oldInd = oldPop.get(i);

            double newF = newInd.getFitness();
            double oldF  = oldInd.getFitness();
            
            if(newF < oldF)
            {
                double delta = oldF-newF;
                double probability = Math.exp(-delta/anneal);
                
                if(run.pickFloat() > probability)
                {
                    newInd = oldInd.duplicate();
                    newPop.set(i, newInd);
                }
            }
        }
    }

}
