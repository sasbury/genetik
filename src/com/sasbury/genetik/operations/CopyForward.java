package com.sasbury.genetik.operations;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;

public class CopyForward implements Operation
{
    protected String name;
    
    public String[] validate(Run run)
    {
        return new String[0];
    }
    
    public int getRequiredParents()
    {
        return 1;
    }

    public Individual[] generateChildren(Individual[] parents, Run run)
    {
        Individual parent = parents[0];
        Individual retVal = parent.duplicate();
        
        retVal.setId(parent.getId());
        retVal.setParentOne(parent.getId());
        retVal.setParentTwo(null);
        
        Individual[] children = {retVal};
        
        return children;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

}
