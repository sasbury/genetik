package com.sasbury.genetik.selection;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;

import java.util.*;

/**
 * Uses the TOURNAMENT_SIZE property in the run configuration to create a random tournament.
 * Then picks the program with the max or min fitness from the tournament based on the CHOOSE_MIN_PROBABILITY
 * property. The default chance of picking the min is 0.25 (25%). The
 * default tournament size is 5.
 */
public class TournamentSelection implements SelectionScheme
{
    public static final String TOURNAMENT_SIZE = "tournament_size";
    public static final String CHOOSE_MIN_PROBABILITY = "choose_worst_in_tournament";
    
    protected int tournamentSize;
    protected double pickMinProb;

    public String[] validate(Run run)
    {
        /* has defaults
        int size = Run.getIntProperty(run, TOURNAMENT_SIZE, -1, true);
        double prob = Run.getDoubleProperty(run, CHOOSE_MIN_PROBABILITY, -1, true);
        
        if(size == -1)
        {
            String err[] = {"Run with tournament selection has invalid "+TOURNAMENT_SIZE+" property."};
            return err;
        }
        
        if(prob == -1)
        {
            String err[] = {"Run with tournament selection has invalid "+CHOOSE_MIN_PROBABILITY+" property."};
            return err;
        }*/
        
        return new String[0];
    }
    
    public Individual select(Population population,SkipTest skip,Run run)
    {
        ArrayList<Individual> tournament = new ArrayList<Individual>();
        Individual retVal = null;
        int index;
        int popSize=population.size();
        Individual p;
        
        if(tournamentSize >= popSize) tournamentSize = 1;

        for(int i=0;i<tournamentSize;i++)
        {
            index = run.pickRandom(popSize-1);

            p = population.get(index);

            if((p!=null) && !tournament.contains(p) && (skip==null || !skip.skip(p)))
            {
                tournament.add(p);
            }
            else
            {
                i--;
            }
        }

        if(tournament.size()==1) retVal = (Individual) tournament.get(0);
        else if(run.pickFloat()>pickMinProb) retVal = pickFromTournamentMax(tournament);
        else retVal = pickFromTournamentMin(tournament);

        return retVal;
    }

    public Individual pickFromTournamentMax(ArrayList<Individual> tournament)
    {
        Individual p;
        Individual retVal = null;
        double max = -Double.MAX_VALUE;

        for(int i=0;i<tournament.size();i++)
        {
            p = (Individual) tournament.get(i);
            if((p!=null) && p.getFitness()>max)
            {
                max = p.getFitness();
                retVal = p;
            }
        }

        return retVal;
    }

    public Individual pickFromTournamentMin(ArrayList<Individual> tournament)
    {
        Individual p;
        Individual retVal = null;
        double min = Integer.MAX_VALUE;

        for(int i=0;i<tournament.size();i++)
        {
            p = (Individual) tournament.get(i);
            if((p!=null) && p.getFitness()<min)
            {
                min = p.getFitness();
                retVal = p;
            }
        }

        return retVal;
    }

    /**
     * No op.
     */
    public void cleanup()
    {

    }

    /**
     * Caches constants.
     */
    public void initialize(Population population, Run run)
    {
        tournamentSize = run.getIntProperty(TOURNAMENT_SIZE, 5, true);
        pickMinProb = run.getDoubleProperty(CHOOSE_MIN_PROBABILITY, 0.25, true);
    }
}
