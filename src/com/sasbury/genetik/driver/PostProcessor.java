package com.sasbury.genetik.driver;

import java.util.*;

public interface PostProcessor
{
    public void postprocess(Run run);
    
    public String[] validate(Properties props);
}
