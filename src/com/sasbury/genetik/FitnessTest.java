package com.sasbury.genetik;

import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;

public interface FitnessTest
{
    /**
     * Provides a way for a fitness test to validate that its properties are set up correctly.
     * @param run
     * @param testName
     * @return An empty array or an array of strings, should not return null
     */
    public String[] validate(Run run,String testName);
    
    /**
     * Calculate and return the score for a particular individual given an individual, run (for properties) and test name.
     * @param the individual to score, the function should return the score, and possibly set the user data, it shoudl not set the fitness
     * @param run the run containing the properties for this test
     * @param testName the name used for this test in the properties file
     * @return
     */
    public double calculateRawScore(Individual ind,Run run,String testName);
}
