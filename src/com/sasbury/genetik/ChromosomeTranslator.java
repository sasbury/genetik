package com.sasbury.genetik;

import com.sasbury.genetik.driver.*;

public interface ChromosomeTranslator
{
    public String[] validate(Run fromRun,Run toRun);
    public Chromosome translate(Chromosome old,Run fromRun,Run toRun);
}
