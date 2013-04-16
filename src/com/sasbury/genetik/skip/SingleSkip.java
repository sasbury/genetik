package com.sasbury.genetik.skip;

import com.sasbury.genetik.*;
import com.sasbury.genetik.population.*;

public class SingleSkip implements SkipTest
{
    protected Individual toSkip;

    public SingleSkip(Individual toSkip)
    {
        super();
        this.toSkip = toSkip;
    }

    public Individual getToSkip()
    {
        return toSkip;
    }

    public void setToSkip(Individual toSkip)
    {
        this.toSkip = toSkip;
    }

    public boolean skip(Individual individual)
    {
        return (individual!=null)?individual.equals(toSkip):true;
    }
    
}
