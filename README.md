genetik
=======

Java Genetic Algorithm and optimization library used for my PhD research. 

Genetik is a flexible library for running genetik algorithms and other heuristic-based optimization schemes. The primary goal of Genetik was to provide an extensible library for exploring optimization routines, especially GA, for nuclear engineering problems.

The Genetik library contains a number of sub-packages that define major features. Some of these include:

 * **crossover** Classes that define GA crossover operators, renamed to operations in 2.0
 * **generator** Classes that define different ways to generate a population, including phase-based generation used for MGGA
 * **insertion** Classes that define different ways to insert individuals into a population
 * **driver** Utility classes for running Genetik, including on a cluster
 * **optimize** Core optimization algorithm implementations
 * **population** Classes that define population holders
 * **reporting** Classes to help build reports from Genetik Runs
 * **scaling** Classes that can be used to scale raw scores before the fitness is calculated
 * **selection** Classes that define the various GA selection schemes
 * **terminate** Classes that define termination conditions for runs

Combined these packages represent numerous options for running GA or other optimization algorithms. Genetik library includes optimizers that use GA, HillClimbing, Random Change, Particle Swarm and Simulated Annealing to find the best solution in a generational context. The crossover package includes single point and uniform crossover. Finally, Genetik supports random, rank, roulette, tournament, truncation and stochastic selection.

Genetik uses a properties file to define a run. The following defines 3 runs of a super simple fitness test described below.

	population=1000
	generations=20
	runs=run_0,run_1,run_2
	chromosome=com.sasbury.genetik.chromosomes.CharChromosome
	available_genes=abcdefghijklmnopqrstuvwxyz
	genes=10
	tests=wordguess
	wordguess.class=test.com.sasbury.genetik.raw.WordGuessTest
	the_word=helloworld

Some items worth noting in this run description are the way a set of MGGA runs are defined using the key array value array of keys pattern is used. Also, properties can be passed to the fitness functions, like the command line for MCNP.

	package test.com.sasbury.genetik.raw;

	import com.sasbury.genetik.*;
	import com.sasbury.genetik.chromosomes.*;
	import com.sasbury.genetik.driver.*;
	import com.sasbury.genetik.population.*;

	public class WordGuessTest implements FitnessTest
	{
	    public static final String THE_WORD="the_word";
	    
	    public double calculateRawScore(Individual ind, Run run, String testName)
	    {
	        String theWord = run.getPropertyWithPrefix(THE_WORD, testName);
	        CharChromosome charChromo = (CharChromosome) ind.getChromosome();
	        String test = charChromo.asString();
	        double retVal = 1;
	        
	        for(int i=0,max=theWord.length();i<max;i++)
	        {
	            if(test.length()>i)
	            {
	                int one = (int)theWord.charAt(i);
	                int two = (int)test.charAt(i);
	                int toAdd = Math.abs(one-two);
	                retVal-= toAdd;
	            }
	        }

	        if(test.length()>theWord.length())
	        {
	            retVal -= (test.length()-theWord.length());
	        }
	        else if(test.length()<theWord.length())
	        {
	            retVal -= (theWord.length()-test.length());
	        }
	        
	        return retVal;
	    }

	    public String[] validate(Run run, String testName)
	    {
	        String err[] = {"Missing value of "+THE_WORD+" for "+testName};
	        
	        if(run.getPropertyWithPrefix(THE_WORD, testName)!=null)
	        {
	            return new String[0];
	        }
	        else
	        {
	            return err;
	        }
	    }
	}
