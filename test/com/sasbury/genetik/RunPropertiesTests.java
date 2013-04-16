package test.com.sasbury.genetik;

import java.util.*;

import com.sasbury.genetik.driver.*;

import junit.framework.*;

public class RunPropertiesTests extends TestCase
{
    public void testSimpleProperty()
    {
        Properties props = new Properties();
        props.setProperty("color","blue");
        Run run = new Run(props,null);
        
        assertEquals(run.getProperty("color"),"blue");
    }
    
    public void testPropertyWithPrefix()
    {
        Properties props = new Properties();
        props.setProperty("color","blue");
        props.setProperty("run.color","red");
        Run run = new Run(props,"run",null);
        
        assertEquals(run.getProperty("color"),"red");
    }
    
    public void testPropertyWithAdditionalPrefix()
    {
        Properties props = new Properties();
        props.setProperty("color","blue");
        props.setProperty("run.color","red");
        props.setProperty("run.op.color","green");
        Run run = new Run(props,"run",null);

        assertEquals(run.getPropertyWithPrefix("color","op"),"green");
        assertEquals(run.getPropertyWithPrefix("color",null),"red");
    }
    
    public void testDefaultedPropertyWithAdditionalPrefix()
    {
        Properties props = new Properties();
        props.setProperty("twist","top");
        props.setProperty("location","us");
        props.setProperty("run.location","japan");
        props.setProperty("color","blue");
        props.setProperty("run.color","red");
        props.setProperty("run.op.color","green");
        Run run = new Run(props,"run",null);

        assertEquals(run.getPropertyWithPrefix("twist","op"),"top");
        assertEquals(run.getPropertyWithPrefix("location","op"),"japan");
    }
    
    public void testBadProperty()
    {
        Properties props = new Properties();
        props.setProperty("color","blue");
        props.setProperty("run.color","red");
        props.setProperty("run.op.color","green");
        Run run = new Run(props,"run",null);
        
        assertEquals(run.getPropertyWithPrefix("bad","op"),null);
    }
    
    public void testGenerationProperty()
    {
        Properties props = new Properties();
        props.setProperty("runs","runs=4_bands,8_bands,16_bands");
        props.setProperty("4_bands.generations","10");
        props.setProperty("8_bands.generations","15");
        props.setProperty("16_bands.generations","25");
        
        Run run = new Run(props,"4_bands",null);
        assertEquals(run.getProperty("generations"),"10");
        run = new Run(props,"8_bands",null);
        assertEquals(run.getProperty("generations"),"15");
        run = new Run(props,"16_bands",null);
        assertEquals(run.getProperty("generations"),"25");
    }
}
