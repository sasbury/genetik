package com.sasbury.genetik.selection;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;

/**
 * Selects individuals using a set of pre-calculated points. The number of points is based on
 * the total number of programs in generation. The distance between points is the total
 * fitness of all programs divded by the number of points, called the interval. The first point is a
 * random value between 0 and interval.
 */
public class StochasticUniversalSelection extends RouletteSelection
{
    protected double interval;
    protected double curPoint;

    public Individual select(Population population,SkipTest skip,Run run)
    {
        Individual retVal = select(population,skip,run,curPoint/total);
        curPoint += interval;
        return retVal;
    }

    /**
     * Caches the total probability, interval and curpoint for use in selection.
     */
    public void initialize(Population population, Run run)
    {
        super.initialize(population, run);
        interval = total/((double)population.size());
        curPoint = run.pickFloat()*interval;
    }
}
