package com.sasbury.genetik;

import com.sasbury.genetik.population.*;

public interface Population
{
    /**
     * @param i an index
     * @return the i'th individual in the population
     */
    public Individual get(int i);
    
    /**
     * @param id an id
     * @return the individual in the population with the provided id
     */
    public Individual get(String id);
    
    public int indexFor(String id);

    /**
     * Set the i'th individual in a population, if index <0 or > size() the individual is added to the end.
     * @param index
     * @param individual
     */
    public void set(int index, Individual individual);
    
    /**
    *
    * @return the size of the population
    */
   public int size();
}
