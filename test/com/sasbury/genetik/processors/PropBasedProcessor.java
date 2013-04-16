package test.com.sasbury.genetik.processors;

import java.util.*;

import com.sasbury.genetik.driver.*;

public class PropBasedProcessor implements PreProcessor,PostProcessor
{
    public static final String PROP = "propbased.proc";

    public void preprocess(Run run)
    {
    }

    public String[] validate(Properties props)
    {
        String err[] = {"Missing processor prop."};
        return (props.get(PROP)!=null)?new String[0]:err;
    }
    
    public void postprocess(Run run)
    {  
    }
}
