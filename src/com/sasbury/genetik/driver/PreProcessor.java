package com.sasbury.genetik.driver;

import java.util.*;

public interface PreProcessor
{
    public void preprocess(Run run);
    
    public String[] validate(Properties props);
}
