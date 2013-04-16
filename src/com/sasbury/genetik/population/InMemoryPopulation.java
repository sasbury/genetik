package com.sasbury.genetik.population;

import java.util.*;

import com.sasbury.genetik.*;

public class InMemoryPopulation implements Population
{
    protected ArrayList<Individual> individuals;
    
    /**
     * InMemory populations have a dynamic size
     * @param size
     */
    public InMemoryPopulation()
    {
        individuals = new ArrayList<Individual>();
    }
    
    /**
     * Set the storage to null so things can be GC'd.
     */
    public void dispose()
    {
        individuals.clear();
        individuals = null;
    }

    /**
     * Throws an exception if the index is out of bounds
     */
    public Individual get(int i)
    {
        return individuals.get(i);
    }

    public Individual get(String id)
    {
        Individual retVal = null;
        
        if(null == id) return null;
        
        for(Individual i : individuals)
        {
            if(id.equals(i.getId()))
            {
                retVal = i;
                break;
            }
        }
        
        return retVal;
    }
    

    public int indexFor(String id)
    {
        int retVal = -1;
        int count = 0;
        
        if(null == id) return retVal;
        
        for(Individual i : individuals)
        {
            if(id.equals(i.getId()))
            {
                retVal = count;
                break;
            }
            
            count++;
        }
        
        return retVal;
    }

    /**
     * No op for an in-memory population, always returns true.
     */
    public boolean save() throws Exception
    {
        return true;
    }

    /**
     * If the index is greater than or equal to the size or negative, the the individual is added to the end. InMemory populations cannot be sparse.
     */
    public void set(int index, Individual individual)
    {
        if(index>=0 && index < individuals.size())
        {
            individual.setIndex(index);
            individuals.set(index, individual);
        }
        else
        {
            individual.setIndex(individuals.size());
            individuals.add(individual);
        }
    }

    public int size()
    {
        return individuals.size();
    }

    @Override
    public int hashCode()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((individuals == null) ? 0 : individuals.hashCode());
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
        final InMemoryPopulation other = (InMemoryPopulation) obj;
        if(individuals == null)
        {
            if(other.individuals != null)
                return false;
        }
        else if(!Arrays.equals(individuals.toArray(), other.individuals.toArray()))
            return false;
        return true;
    }

}
