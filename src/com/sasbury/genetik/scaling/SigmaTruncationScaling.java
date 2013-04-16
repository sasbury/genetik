package com.sasbury.genetik.scaling;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;

/**
 * Calculates the averate and standard deviation for a population. Gets the value of c=SIGMA_FITNESS_SCALE_C from
 * the run configuration and performs scaling using the formula:
 * <br><br>
 * fitness = rawScore - (averageScore - c * standardDeviation)
 * <br><br>
 * Each test gets its own average and stdDev. So if there are 3 tests, there are
 * 3 averages and 3 stdDeviations. The resulting values are averaged.
 * <br><br>
 * by default c = 1.
 * <br><br>
 * Values less than 0 are truncated to 0.
 */
public class SigmaTruncationScaling implements FitnessScaling
{
    public static final String SIGMA_FITNESS_SCALE_C = "sigma_scaling_c";
    
    protected double c;
    protected double[] avg;
    protected double[] stdDev;
    protected double[] scale;

    public double calculateFitness(Individual individual,Population population,Run run)
    {
        double rawScores[] = individual.getRawScores();
        double total = 0;

        for(int i=0,max=rawScores.length;i<max;i++)
        {
            total += rawScores[i] - scale[i];
        }

        total /= (double)rawScores.length;

        if(total>0) total = 0;

        return total;
    }

    /**
     * No op.
     */
    public void cleanup()
    {

    }

    public String[] validate(Run run)
    {
        //has default fitness scale
        return new String[0];
    }

    /**
     * Caches constants, average and standard deviation.
     */
    public void initialize(Population population, Run run)
    {
        String cStr = run.getProperty(SIGMA_FITNESS_SCALE_C);

        if(cStr == null) c = 1;
        else c = Double.parseDouble(cStr);

        Individual p = population.get(0);
        double rawScores[] = p.getRawScores();
        int popSize = population.size();
        int numScores = rawScores.length;
        double x;

        scale = new double[numScores];
        avg = new double[numScores];
        stdDev = new double[numScores];

        for(int i=0;i<popSize;i++)
        {
            p = population.get(i);
            rawScores = p.getRawScores();

            for(int j=0;j<numScores;j++)
            {
                avg[j] += rawScores[j];
            }
        }

        for(int j=0;j<numScores;j++)
        {
            avg[j] = avg[j]/popSize;
        }

        for(int i=0;i<popSize;i++)
        {
            p = population.get(i);
            rawScores = p.getRawScores();

            for(int j=0;j<numScores;j++)
            {
                x = rawScores[j]-avg[j];
                stdDev[j] += x*x;
            }
        }

        for(int j=0;j<numScores;j++)
        {
            stdDev[j] = Math.sqrt(stdDev[j]/(popSize-1));
        }

        for(int j=0;j<numScores;j++)
        {
            scale[j] = (avg[j]-c*stdDev[j]);
        }
    }
}
