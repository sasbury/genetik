package com.sasbury.genetik.generator;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;

public class PhaseBasedGenerator extends RandomGenerator
{
    public static final String MUTATIONS_ON_GENERATION = "mutations_on_phase_generation";
    
    /**
     * Class name for the phase chromosome translator.
     */
    public static final String PHASE_TRANSLATOR_CLASS_NAME = "phase_translator";
    
    /**
     * Class name for the selection scheme used to select items from the previous phase.
     */
    public static final String PHASE_SELECTION_CLASS_NAME = "phase_selection";
    
    protected Run previousRun;
    protected Population previousPopulation;
    protected SelectionScheme selectionScheme;
    protected ChromosomeTranslator translator;

    public void initialize(Run run,Run previousRun,Population previousPopulation)
    {
        this.previousRun = previousRun;
        this.previousPopulation = previousPopulation;
        this.selectionScheme = (SelectionScheme)run.createObject(PHASE_SELECTION_CLASS_NAME);
        this.translator = (ChromosomeTranslator)run.createObject(PHASE_TRANSLATOR_CLASS_NAME);
        
        this.selectionScheme.initialize(previousPopulation, run);
    }
    
    public void cleanup()
    {
        this.selectionScheme.cleanup();
    }

    public String[] validate(Run fromRun,Run toRun)
    { 
        SelectionScheme scheme = (SelectionScheme) toRun.createObject(PHASE_SELECTION_CLASS_NAME);
        
        if(scheme == null)
        {
            String[] err = {"Phased based generator is missing "+PHASE_SELECTION_CLASS_NAME+" property."};
            return err;
        }
        else
        {
            String[] err = scheme.validate(toRun);
            if(err!=null && err.length>0)
            {
                return err;
            }
        }

        ChromosomeTranslator translator = (ChromosomeTranslator) toRun.createObject(PHASE_TRANSLATOR_CLASS_NAME);
        if(translator == null)
        {
            String[] err = {"Phased based generator is missing "+PHASE_TRANSLATOR_CLASS_NAME+" property."};
            return err;
        }
        else
        {
            return translator.validate(fromRun, toRun);
        }
    }

    @Override
    public Individual generateIndividual(Run run)
    {
        int mutations = run.getIntProperty(MUTATIONS_ON_GENERATION, 0, true);
        Individual result;
        
        if(previousPopulation == null)
        {
            result = super.generateIndividual(run);
        }
        else
        {
            Individual parent = selectionScheme.select(previousPopulation, null, run);
            Chromosome parentC = parent.getChromosome();
            Chromosome newC = null;
            
            if(translator != null)
            {
                newC = translator.translate(parentC,previousRun,run);
            }
            
            if(newC == null)
            {
                newC = (Chromosome) run.createObject(GenetikConstants.CHROMOSOME);
                newC.initalizeFromGenes(parentC.extractGenes());
            }
            
            result = new Individual();
            result.setChromosome(newC);
            result.setParentOne(parent.getId());
            result.setParentTwo(parent.getId());
        }
        
        if(mutations > 0)
        {
            Chromosome chromo = result.getChromosome();
            String[] genes = chromo.extractGenes();
            
            for(int i=0;i<mutations;i++)
            {
                int index = run.pickRandom(genes.length-1);
                genes[index] = chromo.randomGene(run);
            }
            
            chromo.initalizeFromGenes(genes);
            result.clearFitness();
        }
        
        return result;
    }
}
