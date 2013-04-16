package com.sasbury.genetik.population;

import java.util.*;

public class IndividualComparator implements Comparator<Individual>
{
    protected int rawScoreIndex;

    public IndividualComparator()
    {
        rawScoreIndex =-1;
    }

    public IndividualComparator(int useRawScores)
    {
        this.rawScoreIndex = useRawScores;
    }

    public int compare(Individual p1, Individual p2)
    {
        int retVal = 0;
        double fitness1;
        double fitness2;

        if(rawScoreIndex>=0)
        {
            double[] rawScores = p1.getRawScores();

            if(rawScores==null)
            {
                fitness1 = 0;
            }
            else
            {
                fitness1 = rawScores[rawScoreIndex];
            }

            rawScores = p2.getRawScores();

            if(rawScores==null)
            {
                fitness2 = 0;
            }
            else
            {
                fitness2 = rawScores[rawScoreIndex];
            }
        }
        else
        {
            fitness1 = p1.getFitness();
            fitness2 = p2.getFitness();
        }

        if(fitness1>fitness2) retVal = 1;
        else if(fitness2>fitness1) retVal = -1;
        else retVal = 0;

        return retVal;
    }
}
