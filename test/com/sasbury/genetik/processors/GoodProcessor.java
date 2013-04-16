package test.com.sasbury.genetik.processors;

import java.util.*;

import com.sasbury.genetik.driver.*;

public class GoodProcessor implements PreProcessor,PostProcessor
{
    public void preprocess(Run run)
    {
    }

    public String[] validate(Properties props)
    {
        String err[] = {};
        return err;
    }

    public void postprocess(Run run)
    {
    }
}
