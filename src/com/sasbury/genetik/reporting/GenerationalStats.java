package com.sasbury.genetik.reporting;

import java.util.*;
import java.util.concurrent.atomic.*;

import com.sasbury.genetik.*;
import com.sasbury.genetik.population.*;

public class GenerationalStats extends Statistics
{
    public static final String RUN_STATISTICS="runStatistics";
    public static final String GENERATION_STATISTICS="generationStatistics";
    
    public static final String INDIVIDUAL_GENES="Individual Genes";
    public static final String AVG_INDIVIDUAL_GENES="Average Individual Genes";
    public static final String MAX_INDIVIDUAL_GENES="Maximum Individual Genes";
    public static final String MIN_INDIVIDUAL_GENES="Minimum Individual Genes";

    public static final String INDIVIDUAL_FITNESS="Individual Fitness";
    public static final String AVG_INDIVIDUAL_FITNESS="Average Individual Fitness";
    public static final String MAX_INDIVIDUAL_FITNESS="Maximum Individual Fitness";
    public static final String MIN_INDIVIDUAL_FITNESS="Minimum Individual Fitness";

    public static final String POPULATION="Population";
    public static final String GENERATIONS="Generations";
    public static final String GENERATION="Generation";
    public static final String BEST_INDIVIDUAL="Best Individual ID";
    public static final String BEST_INDIVIDUAL_GEN="Best Individual Generation";
    
    public static final String SCORING_CACHE_HITS="Scoring Cache Hits";
    public static final String REPAIR_COPIES = "Repairs";
    
    public static final String BUILD_TIME="Build Time";
    public static final String SCALING_TIME="Scaling Time";
    public static final String SCORE_TIME="Scoring Time";
    
    public static final String RUN_NAME = "Name";
    
    public GenerationalStats()
    {
    }
    
    public HashMap<String,Object> captureStatisticsForCurrentGeneration(Population pop,int generation)
    {
        HashMap<String,Object> retVal = new HashMap<String,Object>();
        
        int count = pop.size();
        int totalGenes = 0;
        int maxGenes = 0;
        int minGenes = Integer.MAX_VALUE;
        double minFitness = Double.MAX_VALUE;
        double maxFitness = -Double.MAX_VALUE;
        double totalFitness = 0;
        String best = null;
        
        for(int i=0;i<count;i++)
        {
            Individual ind = pop.get(i);
            int genes = ind.getChromosome().geneCount();
            double fitness = ind.getFitness();
            
            totalGenes += genes;
            if(genes>=maxGenes) maxGenes = genes;
            if(genes<=minGenes) minGenes = genes;
            
            totalFitness += fitness;
            if(fitness < minFitness) minFitness = fitness;
            if(maxFitness < fitness)
            {
                maxFitness = fitness;
                best = ind.getId();
            }
        }

        retVal.put(AVG_INDIVIDUAL_FITNESS,new Double(totalFitness/(double)count));
        retVal.put(MIN_INDIVIDUAL_FITNESS,new Double(minFitness));
        retVal.put(MAX_INDIVIDUAL_FITNESS,new Double(maxFitness));

        retVal.put(AVG_INDIVIDUAL_GENES,new Double(totalGenes/(double)count));
        retVal.put(MIN_INDIVIDUAL_GENES,new Double(minGenes));
        retVal.put(MAX_INDIVIDUAL_GENES,new Double(maxGenes));
        
        retVal.put(BEST_INDIVIDUAL, best);
        retVal.put(POPULATION, new Integer(count));
        retVal.put(GENERATION, new Integer(generation));
        
        for(String key : stats.keySet())
        {
            AtomicLong value = stats.get(key);
            retVal.put(key, value);
        }
        
        stats.clear();
        
        return retVal;
    }

    public HashMap<String,Object> generateRunStatistics(ArrayList<HashMap<String,Object>> generationalStatistics)
    {
        HashMap<String,Object> retVal = new HashMap<String,Object>();
        HashMap<String,StatData> statData = new HashMap<String,StatData>();
        String bestId = null;
        int genForBest = -1;
        double generations = generationalStatistics.size();
        int genCount = 0;
        int individualCount = 0;
        
        for(HashMap<String,Object> genStats : generationalStatistics)
        {
            for(String key : genStats.keySet())
            {
                if(BEST_INDIVIDUAL.equals(key)||GENERATION.equals(key))
                {
                    continue;
                }
                else if(AVG_INDIVIDUAL_FITNESS.equals(key))
                {
                    double pop = ((Number)genStats.get(POPULATION)).doubleValue();
                    double avg = ((Number)genStats.get(key)).doubleValue();
                    double total = avg * pop;
                    StatData data = getStatData(statData,INDIVIDUAL_FITNESS);
                    data.total += total;
                    individualCount += pop;
                }
                else if(AVG_INDIVIDUAL_GENES.equals(key))
                {
                    double pop = ((Number)genStats.get(POPULATION)).doubleValue();
                    double avg = ((Number)genStats.get(key)).doubleValue();
                    double total = avg * pop;
                    StatData data = getStatData(statData,INDIVIDUAL_GENES);
                    data.total += total;
                }
                else if(MAX_INDIVIDUAL_FITNESS.equals(key))
                {
                    double max = ((Number)genStats.get(key)).doubleValue();
                    StatData data = getStatData(statData,INDIVIDUAL_FITNESS);
                    if(data.max < max)
                    {
                        data.max = max;
                        bestId = genStats.get(BEST_INDIVIDUAL).toString();
                        genForBest = genCount;
                    }
                }
                else if(MAX_INDIVIDUAL_GENES.equals(key))
                {
                    double max = ((Number)genStats.get(key)).doubleValue();
                    StatData data = getStatData(statData,INDIVIDUAL_GENES);
                    if(data.max < max) data.max = max;
                }
                else if(MIN_INDIVIDUAL_FITNESS.equals(key))
                {
                    double min = ((Number)genStats.get(key)).doubleValue();
                    StatData data = getStatData(statData,INDIVIDUAL_FITNESS);
                    if(data.min > min) data.min = min;
                }
                else if(MIN_INDIVIDUAL_GENES.equals(key))
                {
                    double min = ((Number)genStats.get(key)).doubleValue();
                    StatData data = getStatData(statData,INDIVIDUAL_GENES);
                    if(data.min > min) data.min = min;
                }
                else
                {
                    double value = ((Number)genStats.get(key)).doubleValue();
                    StatData data = getStatData(statData,key);
                    if(data.min > value) data.min = value;
                    if(data.max < value) data.max = value;
                    data.total += value;
                }
            }
            
            genCount++;
        }
        
        for(String key : statData.keySet())
        {
            StatData data = statData.get(key);

            if(INDIVIDUAL_FITNESS.equals(key) || INDIVIDUAL_GENES.equals(key))
            {
                retVal.put("Average "+key, new Double(data.total/(double)individualCount));
            }
            else
            {
                retVal.put("Average "+key, new Double(data.total/generations));
            }
            
            retVal.put("Total "+key, new Double(data.total));
            retVal.put("Maximum "+key, new Double(data.max));
            retVal.put("Minimum "+key, new Double(data.min));
        }

        retVal.put(BEST_INDIVIDUAL, bestId);
        retVal.put(BEST_INDIVIDUAL_GEN, new Integer(genForBest));
        retVal.put(GENERATIONS, new Integer((int)generations));

        return retVal;
    }
    
    protected StatData getStatData(HashMap<String,StatData> stats,String stat)
    {
        StatData retVal = stats.get(stat);
        if(retVal == null)
        {
            retVal = new StatData();
            stats.put(stat,retVal);
        }
        return retVal;
    }
}

class StatData
{
    public double total = 0;
    public double max = -Double.MAX_VALUE;
    public double min = Double.MAX_VALUE;
}
