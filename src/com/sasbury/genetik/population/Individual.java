package com.sasbury.genetik.population;

import java.util.*;
import java.util.logging.*;

import com.sasbury.genetik.*;

public class Individual
{
    protected String id;
    protected double fitness;
    protected double[] rawScores;
    protected Chromosome chromosome;
    protected String parentOne;
    protected String parentTwo;
    protected Object workingData;
    protected String userData;
    protected int index;
    
    protected static long MAX_COUNT = 1000000000;
    protected static String ALPHA="0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    protected static long count = 1;
    
    public static String generateId()
    {
        long seconds = System.currentTimeMillis()/(1000);
        StringBuffer buffer = new StringBuffer();
        long mod, div;
        long size = ALPHA.length();
        
        mod = seconds % size;
        div = seconds / size;
        
        if(mod == 0) div -= 1;

        while(seconds > 0)//works from smallest to largest, we can ignore biggest, up to a point
        {
            buffer.append(ALPHA.charAt((int)mod));
            seconds = div;
            div = (seconds / size);
            mod = seconds % size;
            if(mod == 0) div -= 1;
        }
        
        buffer.append(".");

        double r = Math.random();
        long rand = (long)(0.001*r*(double)MAX_COUNT);
        mod = rand % size;
        div = rand / size;
        
        if(mod == 0) div -= 1;

        while(rand > 0)//works from smallest to largest, we can ignore biggest, up to a point
        {
            buffer.append(ALPHA.charAt((int)mod));
            rand = div;
            div = (rand / size);
            mod = rand % size;
            if(mod == 0) div -= 1;
        }
        
        buffer.append(".");
        
        long c = count;
        mod = c % size;
        div = c / size;
        
        if(mod == 0) div -= 1;
        
        while(c > 0)//works from smallest to largest, we can ignore biggest, up to a point
        {
            buffer.append(ALPHA.charAt((int)mod));
            c = div;
            div = (c / size);
            mod = c % size;
            if(mod == 0) div -= 1;
        }
        
        count += 1;
        count %= MAX_COUNT;
        
        return buffer.toString();
    }
    
    public Individual()
    {
        fitness = Integer.MIN_VALUE;
        id = generateId();
    }
    
    public Chromosome getChromosome()
    {
        return chromosome;
    }
    
    public void setChromosome(Chromosome chromosome)
    {
        this.chromosome = chromosome;
    }
    
    public double getFitness()
    {
        return fitness;
    }
    
    public void setFitness(double fitness)
    {
        this.fitness = fitness;
    }
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public String getParentOne()
    {
        return parentOne;
    }
    
    public void setParentOne(String parentOne)
    {
        this.parentOne = parentOne;
    }
    
    public String getParentTwo()
    {
        return parentTwo;
    }
    
    public void setParentTwo(String parentTwo)
    {
        this.parentTwo = parentTwo;
    }
    
    public double[] getRawScores()
    {
        return rawScores;
    }
    
    public void setRawScores(double[] rawScores)
    {
        this.rawScores = rawScores;
    }
    
    public String getUserData()
    {
        return userData;
    }
    
    public void setUserData(String userData)
    {
        this.userData = userData;
    }
    
    public Object getWorkingData()
    {
        return workingData;
    }
    
    public void setWorkingData(Object workingData)
    {
        this.workingData = workingData;
    }
    
    /**
     * Index is provided if the individual is in a population, it is not saved to disk.
     * @return
     */
    public int getIndex()
    {
        return index;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    public boolean hasFitness()
    {
        return rawScores!=null || fitness!=Integer.MIN_VALUE;
    }
    
    public boolean hasScores()
    {
        return rawScores!=null && rawScores.length>0;
    }
    
    public void clearFitness()
    {
        fitness = Integer.MIN_VALUE;
        rawScores = null;
    }
    
    public Individual duplicate()
    {
        Individual retVal = new Individual();

        //keep a unique id retVal.id = this.id;
        retVal.rawScores=this.rawScores;
        retVal.fitness=this.fitness;
        retVal.parentOne=this.parentOne;
        retVal.parentTwo=this.parentTwo;
        retVal.userData = this.userData;
        
        try
        {
            Chromosome c = (Chromosome) chromosome.getClass().newInstance();
            c.initalizeFromArchive(chromosome.toArchiveFormat());
            retVal.chromosome = c;
        }
        catch(Exception ex)
        {
            Logger.getLogger(GenetikConstants.LOGGER).log(Level.SEVERE, "Unable to duplicate chromosome "+chromosome, ex);
        }
        
        return retVal;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((chromosome == null) ? 0 : chromosome.hashCode());
        long temp;
        temp = Double.doubleToLongBits(fitness);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result
                + ((parentOne == null) ? 0 : parentOne.hashCode());
        result = prime * result
                + ((parentTwo == null) ? 0 : parentTwo.hashCode());
        result = prime * result + Arrays.hashCode(rawScores);
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        Individual other = (Individual) obj;
        if(chromosome == null)
        {
            if(other.chromosome != null)
                return false;
        }
        else if(!chromosome.equals(other.chromosome))
            return false;
        if(Double.doubleToLongBits(fitness) != Double
                .doubleToLongBits(other.fitness))
            return false;
        if(id == null)
        {
            if(other.id != null)
                return false;
        }
        else if(!id.equals(other.id))
            return false;
        if(parentOne == null)
        {
            if(other.parentOne != null)
                return false;
        }
        else if(!parentOne.equals(other.parentOne))
            return false;
        if(parentTwo == null)
        {
            if(other.parentTwo != null)
                return false;
        }
        else if(!parentTwo.equals(other.parentTwo))
            return false;
        if(!Arrays.equals(rawScores, other.rawScores))
            return false;
        return true;
    }
}
