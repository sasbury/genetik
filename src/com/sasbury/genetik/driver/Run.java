package com.sasbury.genetik.driver;

import java.text.*;
import java.util.*;
import java.util.logging.*;

import com.sasbury.genetik.*;
import com.sasbury.genetik.generator.*;
import com.sasbury.genetik.operations.*;
import com.sasbury.genetik.optimize.*;
import com.sasbury.genetik.scaling.*;
import com.sasbury.genetik.selection.*;
import com.sasbury.genetik.terminate.*;

public class Run
{
    public static final String DEFAULT_NAME = "run_zero";
    
    protected Properties properties;
    protected String prefix;
    protected String name;
    protected Random random;
    protected RunContext context;
    
    /**
     * The default properties include: No termination, tournament selection, no scaling, random generation
     * GenerationalGA with 10% copy, 20% mutate and 70% uniform crossover.
     * @return
     */
    public static Properties createDefaultProperties()
    {
        Properties props = new Properties();

        props.setProperty(GenetikConstants.TERMINATION, NoTermination.class.getCanonicalName());
        props.setProperty(GenetikConstants.SELECTION, TournamentSelection.class.getCanonicalName());
        props.setProperty(GenetikConstants.SCALING, NoScaling.class.getCanonicalName());
        props.setProperty(GenetikConstants.GENERATOR, RandomGenerator.class.getCanonicalName());
        props.setProperty(GenetikConstants.OPTIMIZER, GenerationalGA.class.getCanonicalName());
        
        props.setProperty(GenerationalGA.OPERATIONS,"crossover,mutation,copy");
        props.setProperty("copy."+GenetikConstants.CLASS, CopyForward.class.getCanonicalName());
        props.setProperty("copy."+GenerationalGA.PROBABILITY, "0.1");
        props.setProperty("mutation."+GenetikConstants.CLASS, SimpleMutation.class.getCanonicalName());
        props.setProperty("mutation."+GenerationalGA.PROBABILITY, "0.2");
        props.setProperty("crossover."+GenetikConstants.CLASS, UniformCrossover.class.getCanonicalName());
        props.setProperty("crossover."+GenerationalGA.PROBABILITY, "0.7");
        
        DateFormat fmt = DateFormat.getDateTimeInstance();
        props.setProperty("Run Date",fmt.format(new Date()));
        
        return props;
    }
    
    /**
     * Create a run with no prefix, or context.
     * @param properties
     */
    public Run(Properties properties)
    {
        this(properties,null,null);
    }
    
    /**
     * Create a run with no prefix.
     * @param properties
     */
    public Run(Properties properties,RunContext runContext)
    {
        this(properties,null,runContext);
    }
    
    public Run(Properties properties, String prefix,RunContext runContext)
    {
        super();
        this.properties = properties;
        this.prefix = prefix;
        this.name = (prefix!=null)?prefix:DEFAULT_NAME;
        this.context = runContext;
    }
    
    public String getName()
    {
        return name;
    }

    protected void setName(String name)
    {
        this.name = name;
    }

    public Properties getProperties()
    {
        return properties;
    }

    public String getPrefix()
    {
        return prefix;
    }
    
    public String getProperty(String propertyName)
    {
        return getPropertyWithPrefix(propertyName,null);
    }
    
    public String getPropertyWithDefault(String propertyName,String defaultValue)
    {
        String retVal = getPropertyWithPrefix(propertyName,null);
        if(retVal == null)retVal = defaultValue;
        return retVal;
    }
    
    
    /**
     * Tries to get a property, first using the runPrefix.additionalPrefix.propertyName, then additionalPrefix.propertyName then propertyName. To
     * allow defaulting. If any prefix is null, it is skipped.
     * @param propertyName
     * @param additionalPrefix
     * @return
     */
    public String getPropertyWithPrefix(String propertyName,String additionalPrefix)
    {
        String retVal = null;
        
        if(additionalPrefix != null)
        {
            if(prefix!=null)
            {
                String firstTry = prefix+"."+additionalPrefix+"."+propertyName;
                retVal = properties.getProperty(firstTry);
            }
            
            if(retVal == null)
            {
                String secondTry = additionalPrefix+"."+propertyName;
                retVal = properties.getProperty(secondTry);
            }
        }
        
        if(retVal == null && prefix != null)
        {
            String secondTry = prefix+"."+propertyName; 
            retVal = properties.getProperty(secondTry);
        }
        
        if(retVal == null)
        {
            String lastTry = propertyName;
            retVal = properties.getProperty(lastTry);
        }
        
        return retVal;
    }
    
    public int getIntProperty(String propertyName,int defaultValue,boolean safe)
    {
        int retVal = defaultValue;
        
        if(safe)
        {
            try
            {
                String str = getProperty(propertyName);
                
                if(null != str)
                {
                    retVal = Integer.parseInt(str);
                }
            }
            catch(Exception exp)
            {
                //ignore
                retVal = defaultValue;
            }
        }
        else
        {
            retVal = Integer.parseInt(getProperty(propertyName));
        }
        
        return retVal;
    }
    
    public double getDoubleProperty(String propertyName,double defaultValue,boolean safe)
    {
        double retVal = defaultValue;
        
        if(safe)
        {
            try
            {
                String str = getProperty(propertyName);
                
                if(null != str)
                {
                    retVal = Double.parseDouble(str);
                }
            }
            catch(Exception exp)
            {
                //ignore
                retVal = defaultValue;
            }
        }
        else
        {
            retVal = Double.parseDouble(getProperty(propertyName));
        }
        
        return retVal;
    }
    
    public Object createObject(String propertyName)
    {
        return createObject(propertyName,null);
    }

    public Object createObject(String propertyName,String additionalPrefix)
    {
        String className = getPropertyWithPrefix(propertyName,additionalPrefix);
        return createObjectForClassName(className);
    }
    
    public static Object createObjectForClassName(String className)
    {
        Object retVal = null;
        
        if(className == null) return null;
        
        try
        {
            Class<?> cls = Class.forName(className);
            retVal = cls.newInstance();
        }
        catch(Exception ex)
        {
            retVal = null;
            Logger.getLogger(GenetikConstants.LOGGER).log(Level.SEVERE, "Unable to create object for class name "+className, ex);
        }
        
        return retVal;
    }
    
    /**
     * The seed is either the "SEED" property or the current time.
     * @return a random number generator associated with this description.
     */
    public Random getRandom()
    {
        if(random == null)
        {
            long seed = System.currentTimeMillis();
            String seedStr = getProperty(GenetikConstants.SEED);
            if(seedStr != null)
            {
                seed = Long.parseLong(seedStr);
            }
            random = new Random(seed);
        }
        
        return random;
    }
    
    /**
     * Pick a random number from 0 to max, inclusive.
     * @param max
     * @return
     */
    public int pickRandom(int max)
    {
        return getRandom().nextInt(max+1);
    }
    
    public float pickFloat()
    {
        return getRandom().nextFloat();
    }
    
    public double pickDouble()
    {
        return getRandom().nextDouble();
    }
    

    public RunContext getContext()
    {
        return context;
    }
    

    @Override
    public int hashCode()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((prefix == null) ? 0 : prefix.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        final Run other = (Run) obj;
        if(prefix == null)
        {
            if(other.prefix != null)
                return false;
        }
        else if(!prefix.equals(other.prefix))
            return false;
        return true;
    }
}
