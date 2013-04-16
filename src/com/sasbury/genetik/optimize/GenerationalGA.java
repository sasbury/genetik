package com.sasbury.genetik.optimize;

import java.util.*;
import java.util.logging.*;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;
import com.sasbury.genetik.skip.*;

public class GenerationalGA implements GenerationalOptimizer
{
    /**
     * A comma seperated list of operation names. These names are used as pre-fixes to find operation information.
     * 
     * operations = crossover, mutation, copy
     * 
     * or
     * 
     * run1.operations = crossover, mutation, copy
     */
    public static final String OPERATIONS = "operations";
    
    /**
     * Set the probability of an operation. The total probability will be the sum of all probabilities, so this can be 
     * of the form 0.7, or 70, etc...
     * 
     * crossover.probability = 0.7
     * or
     * run1.crossover.probability = 0.7
     */
    public static final String PROBABILITY = "probability";
    
    public String[] validate(Run run)
    {
        ArrayList<String> issues = new ArrayList<String>();
        
        SelectionScheme selection = (SelectionScheme) run.createObject(GenetikConstants.SELECTION);
        if(selection == null)
        {
            issues.add("No Selection Scheme defined for "+run.getName()+"." );
        }
        else
        {
            issues.addAll(Arrays.asList(selection.validate(run)));
        }
        
        String operationStr = run.getProperty(OPERATIONS);
        
        if(operationStr == null || operationStr.length()==0)
        {
            issues.add("No operations defined for GA optimizer in run "+run.getName()+".");
        }
        else
        {
            String operationNames[] = operationStr.split(",");
            
            //load the operations
            for(int i=0,max=operationNames.length;i<max;i++)
            {
                String opName = operationNames[i];
                
                try
                {
                    Operation op = (Operation) run.createObject(GenetikConstants.CLASS,opName);
                    
                    if(op == null)
                    {
                        issues.add("Operation named "+opName+" could not be created for "+run.getName()+".");
                    }
                    else
                    {
                        issues.addAll(Arrays.asList(op.validate(run)));
                    }
                }
                catch(Exception exp)
                {
                    issues.add("Operation named "+opName+" could not be created for "+run.getName()+".");
                }
                
                try
                {
                    double prob = Double.parseDouble(run.getPropertyWithPrefix(PROBABILITY,opName));
                    
                    if(prob < 0)
                    {

                        issues.add("Probablilty for operation named "+opName+" is < 0 for "+run.getName()+".");
                    }
                }
                catch(Exception exp)
                {
                    issues.add("Probablilty for operation named "+opName+" is not a number or is missing for "+run.getName()+".");
                }
            }
        }
        
        return issues.toArray(new String[0]);
    }

    public void generatePopulation(Population oldPop, Population newPop, Run run, int generation)
    {
        Logger logger = Logger.getLogger(GenetikConstants.LOGGER);
        SelectionScheme selection = (SelectionScheme) run.createObject(GenetikConstants.SELECTION);
        String operationStr = run.getProperty(OPERATIONS);
        String operationNames[] = operationStr.split(",");
        Operation[] operations = new Operation[operationNames.length];
        double[] probabilities = new double[operationNames.length];
        double totalProbability = 0;
        
        selection.initialize(oldPop, run);
        
        //load the operations
        for(int i=0,max=operationNames.length;i<max;i++)
        {
            String opName = operationNames[i];
            Operation op = (Operation) run.createObject(GenetikConstants.CLASS,opName);
            double prob = Double.parseDouble(run.getPropertyWithPrefix(PROBABILITY,opName));
            op.setName(opName);
            operations[i] = op;
            probabilities[i] = prob;
            totalProbability += prob;
        }
        
        //Scale the probabilities
        for(int i=0,max=probabilities.length;i<max;i++)
        {
            probabilities[i] = probabilities[i]/totalProbability;
        }
        
        int tries = 0;
        logger.info("  Generating population of "+oldPop.size()+" with GA.");
        
        while(newPop.size() < oldPop.size())
        {
            if(tries>2*oldPop.size())
            {
                logger.severe("  Tried "+tries+" times, error-ing out.");
                throw new RuntimeException("too many tries");
            }
            
            double prob = run.pickFloat();
            Operation op = null;
            String opName = null;
            double total = 0;
            
            for(int j=0,maxj=probabilities.length;j<maxj;j++)
            {
                total += probabilities[j];
                if(prob <= total)
                {
                    op = operations[j];
                    opName = operationNames[j];
                    break;
                }
            }
            
            int requiredParents = op.getRequiredParents();
            Individual parents[] = new Individual[requiredParents];
            
            for(int j=0;j<requiredParents;j++)
            {
                parents[j] = selection.select(oldPop, new MultiSkip(parents), run);
            }
            
            Individual[] children = op.generateChildren(parents, run);
            
            for(int i=0,max=children.length;i<max;i++)
            {
                newPop.set(-1,children[i]);
                if(newPop.size() == oldPop.size()) break;
            }

            run.getContext().getStats().increment(opName, 1);
            run.getContext().getStats().increment("GA Operations", 1);
            tries++;
        }
        
        selection.cleanup();
    }

    /**
     * No Op
     */
    public void finalizePopulation(Population oldPop, Population newPop, Run run, int generation)
    {
    }
}
