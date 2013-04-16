package com.sasbury.genetik.driver;

import java.util.*;
import java.util.logging.*;

import com.sasbury.genetik.*;
import com.sasbury.genetik.population.*;
import com.sasbury.genetik.reporting.*;
import com.sasbury.util.json.*;

public abstract class SimpleGenerationalDriver extends AbstractGenerationalDriver implements RunContext
{
    protected PreProcessor preprocessor;
    protected PostProcessor postprocessor;
    protected GenerationalStats stats;
    protected ArrayList<HashMap<String,Object>> generationStats;
    
    public abstract Population createEmptyPopulation();
    public abstract Population getPopulationFor(Run run,int generation);
    public abstract void savePopulationFor(Population pop,Run run,int generation);
    public abstract void disposeOfPopulation(Population pop);

    public Run createRun(String runName)
    {
        return new Run(getProperties(),runName,this);
    }
    
    /**
     * Separated from build to allow for phased generation.
     * @param run
     * @param previousRun
     */
    public void initialBuild(Run run,Run previousRun)
    {
        Population pop = createEmptyPopulation();
        IndividualGenerator generator = (IndividualGenerator) run.createObject(GenetikConstants.GENERATOR);
        int popSize = Integer.parseInt(run.getProperty(GenetikConstants.POPULATION));
        Population prevPop = null;
        
        if(previousRun !=null)
        {
            int prevGen = Integer.parseInt(previousRun.getProperty(GenetikConstants.GENERATIONS));
            prevPop = getPopulationFor(previousRun, prevGen-1);//index from 0
            generator.initialize(run, previousRun, prevPop);
        }
        
        for(int i=0;i<popSize;i++)
        {
            Individual ind = generator.generateIndividual(run);
            pop.set(i, ind);
        }
        
        savePopulationFor(pop, run, 0);
        disposeOfPopulation(pop);
        if(prevPop != null) disposeOfPopulation(prevPop);
        
        generator.cleanup();
    }
    
    public void build(Run run, int generation)
    {
        Logger logger = Logger.getLogger(GenetikConstants.LOGGER);
        Population pop = createEmptyPopulation();
        GenerationalOptimizer optimizer = (GenerationalOptimizer) run.createObject(GenetikConstants.OPTIMIZER);
        Population prevPop = getPopulationFor(run,generation-1);
        
        logger.info("  Requestion generation from optimizer.");
        optimizer.generatePopulation(prevPop, pop, run, generation);

        long cacheHits = 0;
        logger.info("  Building cache and pre-scoring.");
        try
        {
            HashMap<Chromosome,Individual> cache = buildCache(run,generation);
            for(int i=0;i<pop.size();i++)
            {
                Individual p = pop.get(i);
                Chromosome code = p.getChromosome();
                Individual hit = cache.get(code);
                
                if(hit!=null && hit.hasFitness())
                {
                    p.setRawScores(hit.getRawScores());
                    p.setUserData(hit.getUserData());//copy working data for reporting
                    getStats().increment(GenerationalStats.SCORING_CACHE_HITS, 1);
                    cacheHits++;
                }
            }
        }
        catch(Exception exp)
        {
            logger.log(Level.SEVERE,"Error pre-scoring from cache.",exp);
        }
        logger.info("  Got "+cacheHits+" cache hits.");
        
        logger.info("  Saving population.");
        savePopulationFor(pop, run, generation);
        
        logger.info("  Disposing of population.");
        disposeOfPopulation(pop);
        if(prevPop != null) disposeOfPopulation(prevPop);
    }

    public boolean score(Run run, int generation)
    {
        Logger logger = Logger.getLogger(GenetikConstants.LOGGER);
        long start = System.currentTimeMillis();
        GenerationalOptimizer optimizer = (GenerationalOptimizer) run.createObject(GenetikConstants.OPTIMIZER);
        Population prevPop = getPopulationFor(run,generation-1);
        Population pop = getPopulationFor(run,generation);
        String testStr = run.getProperty(GenetikConstants.TESTS);
        String testNames[] = testStr.split(",");
        FitnessTest[] tests = new FitnessTest[testNames.length];
        int testCount = testNames.length;
        boolean testFailed = false;
        
        //load the tests
        for(int i=0;i<testCount;i++)
        {
            String testName = testNames[i];
            FitnessTest test = (FitnessTest) run.createObject(GenetikConstants.CLASS,testName);
            tests[i] = test;
        }
        
        logger.info("Scoring generation "+generation);
        
        int prescored = 0;
        //score the individuals
        for(int i=0,max=pop.size();i<max;i++)
        {
            Individual ind = pop.get(i);
            double scores[] = new double[testCount];
            
            if(i%10 == 0 && i!=0)
            {
                logger.info("  Scoring "+i+"th individual");
            }
            
            if(ind.hasScores())
            {
                prescored++;
                continue;
            }
            
            for(int j=0;j<testCount;j++)
            {
                FitnessTest test = tests[j];
                double score = test.calculateRawScore(ind, run, testNames[j]);
                
                if(Double.isNaN(score)||Double.isInfinite(score))
                {
                    testFailed = true;
                    break;
                }
                else
                {
                    scores[j] = score;
                }
            }
            
            if(!testFailed) ind.setRawScores(scores);
            else break;
        }
        
        logger.info("  Found "+prescored+" with scores.");
        
        if(!testFailed)
        {
            optimizer.finalizePopulation(prevPop, pop, run, generation);
            savePopulationFor(pop, run, generation);
        }
        
        disposeOfPopulation(pop);
        if(prevPop != null) disposeOfPopulation(prevPop);
        
        long end = System.currentTimeMillis();
        stats.increment(GenerationalStats.SCORE_TIME,(end-start));
        return !testFailed;
    }
    
    public void collectAndScale(Run run, int generation)
    {
        Logger logger = Logger.getLogger(GenetikConstants.LOGGER);
        FitnessScaling scale = (FitnessScaling) run.createObject(GenetikConstants.SCALING);
        Population pop = getPopulationFor(run,generation);
        double maxScore = -100000.0;
        double minScore = 100000.0;
        
        logger.info("Collecting/Scaling generation "+generation);
        
        scale.initialize(pop, run);
        
        for(int i=0,max=pop.size();i<max;i++)
        {
            Individual ind = pop.get(i);
            double fit = scale.calculateFitness(ind, pop, run);
            ind.setFitness(fit);
            
            if(fit > maxScore) maxScore = fit;
            if(fit<minScore) minScore = fit;
        }

        logger.info("Minimum score = "+minScore);
        logger.info("Maximum score = "+maxScore);
        
        scale.cleanup();
        savePopulationFor(pop, run, generation);
        disposeOfPopulation(pop);
    }

    public void startingGeneration(Run run, int generation)
    {
        
    }
    
    public void finishedGeneration(Run run, int generation)
    {
        Logger logger = Logger.getLogger(GenetikConstants.LOGGER);
        Population pop = getPopulationFor(run,generation);
        HashMap<String,Object> genStats = stats.captureStatisticsForCurrentGeneration(pop,generation);
        
        if(generationStats.size()>generation)//if this was a restart, replace the old data
        {
            generationStats.set(generation, genStats);
        }
        else
        {
            generationStats.add(genStats);
        }
        disposeOfPopulation(pop);
        checkPointRunStats(run);
        
        logger.info("Finished generation "+generation);
    }
    
    public void preprocess()
    {
        if(props.getProperty(GenetikConstants.PREPROCESSOR)!=null)
        {
            preprocessor = (PreProcessor) Run.createObjectForClassName(props.getProperty(GenetikConstants.PREPROCESSOR));
        }
    }

    @SuppressWarnings("unchecked")
    public void preprocess(Run run)
    {
        stats = new GenerationalStats();
        
        String runData = null;
        
        try
        {
            runData = getRunData(run,"statistics.json");
        }
        catch(Exception exp)
        {
            //ignore
        }
        
        if(runData != null)//restarting
        {
            try
            {
                JSONDecoder decoder = new JSONDecoder();
                HashMap<String,Object> jsonObj = decoder.decode(runData);
                generationStats = (ArrayList<HashMap<String,Object>>)jsonObj.get(GenerationalStats.GENERATION_STATISTICS);
            }
            catch(Exception exp)
            {
                throw new RuntimeException("Error converting stats from json.",exp);
            }
        }
        
        if(generationStats==null)
        {
            generationStats = new ArrayList<HashMap<String,Object>>();
        }
        
        if(preprocessor != null)
        {
            Logger logger = Logger.getLogger(GenetikConstants.LOGGER);
            logger.info("Running preprocessor");
            preprocessor.preprocess(run);
        }
    }

    public void postprocess(Run run)
    {
        initPostProcessor();
        
        //let the post processor do stats if it wants
        if(postprocessor != null)
        {
            Logger logger = Logger.getLogger(GenetikConstants.LOGGER);
            logger.info("Running postprocessor");
            postprocessor.postprocess(run);
        }
        
        checkPointRunStats(run);
        
        stats = null;
        generationStats = null;
    }
    
    private void checkPointRunStats(Run run)
    {
        HashMap<String,Object> runStats = stats.generateRunStatistics(generationStats);

        runStats.put(GenerationalStats.RUN_NAME, run.getName());
        runStats.put(GenetikConstants.OPTIMIZER, run.getProperty(GenetikConstants.OPTIMIZER));
        
        HashMap<String,Object> jsonObj = new HashMap<String,Object>();
        jsonObj.put(GenerationalStats.RUN_STATISTICS, runStats);
        jsonObj.put(GenerationalStats.GENERATION_STATISTICS, generationStats);
        
        try
        {
            JSONEncoder encoder = new JSONEncoder();
            String json = encoder.encode(jsonObj);
            saveRunData(run, "statistics.json", json);
        }
        catch(Exception exp)
        {
            throw new RuntimeException("Error converting stats to json.",exp);
        }
    }

    public void postprocess()
    {
    }
    
    public void haltingRun(Run run)
    {
        checkPointRunStats(run);
    }
    
    protected void initPostProcessor()
    {
        if(props.getProperty(GenetikConstants.POSTPROCESSOR)!=null)
        {
            postprocessor = (PostProcessor) Run.createObjectForClassName(props.getProperty(GenetikConstants.POSTPROCESSOR));
        }
    }
    
    public Statistics getStats()
    {
        return stats;
    }

    public HashMap<Chromosome,Individual> buildCache(Run run,int currentGeneration) throws Exception
    {
        HashMap<Chromosome,Individual> cache = new HashMap<Chromosome,Individual>();
        
        for(int i=0;i<currentGeneration;i++)
        {
            Population pop = getPopulationFor(run, i);
            
            for(int j=0,max=pop.size();j<max;j++)
            {
                Individual p = pop.get(j);
                Chromosome code = p.getChromosome();
                
                if(!cache.containsKey(code) && p.hasFitness())
                {
                    cache.put(code, p);
                }
            }
        }
        
        return cache;
    }
}
