package com.sasbury.genetik;

public interface GenetikConstants
{
    /**
     * Name for the logger used for any errors,etc.. in the library.
     */
    public static final String LOGGER="com.sasbury.genetik";
    
    /**
     * Specify a seed for the random generator. This should allow runs to be duplicated since all random numbers come from that initial seed.
     * If not provided, this value defaults to the current time in milliseconds.
     */
    public static final String SEED = "seed";
    
    /**
     * Specifies the Generator class to use for a genetik run, the default value is random generation.
     */
    public static final String GENERATOR = "generator";
    
    /**
     * Specifies the optimizer class to use for a genetik run, the default value is generational GA.
     */
    public static final String OPTIMIZER = "optimizer";
    
    /**
     * Specifies the FitnessScaling class to use for a genetik run, the default value is no scaling.
     */
    public static final String SCALING = "scaling";
    
    /**
     * Specifies the SelectionScheme class to use for a genetik run, the default value is random selection.
     */
    public static final String SELECTION = "selection";
    
    /**
     * Specifies the InsertionScheme class to use for a genetik run, the default value is null, since generational optimization is used by default.
     */
    public static final String INSERTION = "insertion";
    
    /**
     * Specifies the TerminationCondition class to use for a genetik run, the default value is NoTermination.
     */
    public static final String TERMINATION = "termination";
    
    /**
     * Specifies the class of the chromosome to use.
     */
    public static final String CHROMOSOME = "chromosome";
    
    /**
     * Specifies the number of generations to run generational GA, or another generational optimizer for.
     */
    public static final String GENERATIONS = "generations";

    /**
     * Optimizers that do not change population size will use this value as the population size, possibly for each generation.
     */
    public static final String POPULATION = "population";
    
    /**
     * Operations that do not change gene count will maintain this number of genes per chromosome. Others will start with this many genes.
     */
    public static final String GENES ="genes";

    /**
     * Specifies the class of the Preprocessor to use. Shared by runs, should not be prefixed.
     */
    public static final String PREPROCESSOR = "preprocessor";

    
    /**
     * Specifies the class of the Postprocessor to use. Shared by runs, should not be prefixed.
     */
    public static final String POSTPROCESSOR = "postprocessor";
    
    /**
     * Specify the names of one or more fitness test(s).
     * Use the class value to set the class for each test, for example.
     * 
     * tests = test1, test2
     * test1.class = com.sasbury.foo
     * test2.class = com.sasbury.bar
     * 
     * Unless altered, the final score will be an average of each test's score.
     * 
     * Tests can use their name to pass data through the properties file:
     * 
     * test1.rings = 5
     * 
     * When used with multiple runs, the run prefix can be used:
     * 
     * run1.test1.rings = 5
     * run2.test1.rings = 10
     * 
     */
    public static final String TESTS = "tests";
    
    /**
     * An optional entry in the genetik properties file. If this entry is present, it contains
     * a comma separated list if prefixes. These prefixes are used for the settings for each run in the list.
     * For example:
     * 
     * runs = run1, run2, run3, ...
     * run1.generations = 10
     * run2.generations = 20
     * 
     * If a setting is not provided with the prefix, genetik will try to find a shared value, for example:
     * 
     * runs = run1,run2,run3
     * generations = 10
     * run3.generations = 20
     * 
     * will have 10 generations for run1 and run2 and 20 for run3
     */
    public static final String RUNS = "runs";
    
    /**
     * Specify the class of a fitness test, operation or other named entity.
     * 
     * crossover.class = com.sasbury.operations.SinglePointCrossover
     * or
     * run1.crossover.class = com.sasbury.operations.SinglePointCrossover
     */
    public static final String CLASS = "class";

    /**
     * The class that will indicate the appropriate loader for the custom reporting resources.
     */
    public static final String CUSTOM_REPORTING_RELATIVE_CLASS="customRelativeClass";
    
    /**
     * A location, package name, in resource form to prepend to the custom reporting
     * resources to find them in the class path. This allows the resources to use paths
     * in the web folder.
     */
    public static final String CUSTOM_REPORTING_RESOURCE_BASE="customReportingBase";

    /**
     * Resource locations, in the class path, for files to include in the web folder.
     * If the location ends with / it is treated as a path in web and a directory is created.
     * This is a comma separated list, the folders should be included first.
     */
    public static final String CUSTOM_REPORTING_RESOURCES="customReporting";
    
    /*
     * The file name, or path, relative to the web folder for a fragement to include when
     * viewing an individual.
     */
    public static final String CUSTOM_INDIVIDUAL_REPORT_FRAGMENT="customIndFragment";
    public static final String CUSTOM_RUN_REPORT_FRAGMENT="customRunFragment";
    public static final String CUSTOM_SUMMARY_REPORT_FRAGMENT="customSummaryFragment";
}
