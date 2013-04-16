package com.sasbury.genetik;

import com.sasbury.genetik.driver.*;
import com.sasbury.genetik.population.*;

public interface Operation
{
    public String[] validate(Run run);
    public void setName(String name);
    public String getName();
    public int getRequiredParents();
    public Individual[] generateChildren(Individual parents[],Run run);
}
