package com.sasbury.genetik.validation;

import java.util.*;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;

public class GenerationalValidator
{
    protected void validateRun(Run run,ArrayList<String> issues)
    {
        String runName = (run.getName()!=null)?run.getName():"the default run";
        
        try
        {
            Integer.parseInt(run.getProperty(GenetikConstants.GENERATIONS));
        }
        catch(NumberFormatException ex)
        {
            issues.add("Either "+runName+" is missing a "+GenetikConstants.GENERATIONS+" property or it is not a valid number.");
        }
        
        try
        {
            Integer.parseInt(run.getProperty(GenetikConstants.GENES));
        }
        catch(NumberFormatException ex)
        {
            issues.add("Either "+runName+" is missing a "+GenetikConstants.GENES+" property or it is not a valid number.");
        }
        
        try
        {
            Integer.parseInt(run.getProperty(GenetikConstants.POPULATION));
        }
        catch(NumberFormatException ex)
        {
            issues.add("Either "+runName+" is missing a "+GenetikConstants.POPULATION+" property or it is not a valid number.");
        }
        
        if(run.getProperty(GenetikConstants.TESTS) == null)
        {
            issues.add("No tests are defined for "+runName+"." );
        }
        else
        {
            String testStr = run.getProperty(GenetikConstants.TESTS);
            String testNames[] = testStr.split(",");
            
            for(String testName : testNames)
            {
                try
                {
                    FitnessTest test = (FitnessTest) run.createObject(GenetikConstants.CLASS,testName);
                    
                    issues.addAll(Arrays.asList(test.validate(run, testName)));
                }
                catch(Exception exp)
                {
                    issues.add("The fitness test named "+testName+" in "+runName+" is not a valid class.");
                }
            }
        }
        
        IndividualGenerator generator = (IndividualGenerator) run.createObject(GenetikConstants.GENERATOR);
        
        if(generator == null)
        {
            issues.add("No generator defined for "+runName+"." );
        }
        else
        {
            issues.addAll(Arrays.asList(generator.validate(run)));
        }

        Chromosome chromo = (Chromosome) run.createObject(GenetikConstants.CHROMOSOME);
        
        if(chromo == null)
        {
            issues.add("No chromosome type defined for "+runName+"." );
        }
        else
        {
            issues.addAll(Arrays.asList(chromo.validate(run)));
        }

        TerminationCondition term = (TerminationCondition) run.createObject(GenetikConstants.TERMINATION);
        
        if(term == null)
        {
            issues.add("No termination type defined for "+runName+"." );
        }
        else
        {
            issues.addAll(Arrays.asList(term.validate(run)));
        }
        
        FitnessScaling scale = (FitnessScaling) run.createObject(GenetikConstants.SCALING);
        if(scale == null)
        {
            issues.add("No Fitness Scaling defined for "+runName+"." );
        }
        else
        {
            issues.addAll(Arrays.asList(scale.validate(run)));
        }
        
        GenerationalOptimizer optimizer = (GenerationalOptimizer) run.createObject(GenetikConstants.OPTIMIZER);
        if(optimizer == null)
        {
            issues.add("No Optimizer defined for "+runName+"." );
        }
        else
        {
            issues.addAll(Arrays.asList(optimizer.validate(run)));
        }
        
        String repClass = run.getProperty(GenetikConstants.CUSTOM_REPORTING_RELATIVE_CLASS);;
        if(repClass != null)
        {
            try
            {
                Class.forName(repClass);
            }
            catch(ClassNotFoundException ex)
            {
                issues.add("Invalid custom reporting relative class.");
            }
        }
    }
    
    /**
     * Provided for sub-classes to add extra raw validation.
     * @param props
     * @param issues
     */
    protected void customValidate(Properties props,ArrayList<String> issues)
    {
        
    }
    
    /**
     * Validate the properties and return any issues. The caller should exit if the array is non-empty.
     * @param props
     * @return
     */
    public String[] validate(Properties props)
    {
        ArrayList<String> issues = new ArrayList<String>();
        String runStr = props.getProperty(GenetikConstants.RUNS);
        
        if(runStr != null)
        {
            String runNames[] = runStr.split(",");
            Set<String> uniqueNames = new HashSet<String>();
            uniqueNames.addAll(Arrays.asList(runNames));
            
            if(uniqueNames.size() != runNames.length)
            {
                issues.add("Run names are not unique.");
            }
            
            for(String runName : runNames)
            {
                Run run = new Run(props,runName,null);
                validateRun(run,issues);
            }
        }
        else
        {
            Run run = new Run(props);
            validateRun(run,issues);
        }

        if(props.getProperty(GenetikConstants.PREPROCESSOR)!=null)
        {
            PreProcessor preprocessor = (PreProcessor) Run.createObjectForClassName(props.getProperty(GenetikConstants.PREPROCESSOR));
            if(preprocessor == null)
            {
                issues.add("Class name for preprocessor is invalid." );
            }
            else
            {
                issues.addAll(Arrays.asList(preprocessor.validate(props)));
            }
        }

        if(props.getProperty(GenetikConstants.POSTPROCESSOR)!=null)
        {
            PostProcessor postprocessor = (PostProcessor) Run.createObjectForClassName(props.getProperty(GenetikConstants.POSTPROCESSOR));
            if( postprocessor == null)
            {
                issues.add("Class name for  postprocessor is invalid." );
            }
            else
            {
                issues.addAll(Arrays.asList( postprocessor.validate(props)));
            }
        }
        
        customValidate(props, issues);
        
        return issues.toArray(new String[0]);
    }
}
