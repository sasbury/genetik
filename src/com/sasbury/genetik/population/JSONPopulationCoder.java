package com.sasbury.genetik.population;

import java.io.*;
import java.util.*;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.util.json.*;

public class JSONPopulationCoder
{
    public static final String POPULATION="pop";
    public static final String INDIVIDUAL="ind";
    public static final String ID="id";
    public static final String GENERATION="gen";
    public static final String PARENT_ONE="p1";
    public static final String PARENT_TWO="p2";
    public static final String FITNESS="fit";
    public static final String SCORES="raw";
    public static final String USER_DATA="user";
    public static final String CHROMOSOME="genes";
    
    public static Population decode(File file,Run run)
    {
        Population pop = new InMemoryPopulation();
        
        if(file.exists())
        {
            try
            {
                FileInputStream in = new FileInputStream(file);
                BufferedInputStream buffIn = new BufferedInputStream(in);
                InputStreamReader writer = new InputStreamReader(buffIn);
                BufferedReader buff = new BufferedReader(writer);

                JSONDecoder decoder = new JSONDecoder();
                HashMap<String,Object> json = decoder.decode(buff);
                convert(json,pop,run);
                
                buff.close();
                buffIn.close();
            }
            catch(Exception exp)
            {
                throw new RuntimeException(exp);
            }
        }
        
        return pop;
    }
    
    public static boolean encode(Population pop,File file)
    {
        boolean retVal = true;
        
        try
        {
            FileOutputStream out = new FileOutputStream(file);
            BufferedOutputStream buffOut = new BufferedOutputStream(out);
            OutputStreamWriter writer = new OutputStreamWriter(buffOut);
            BufferedWriter buff = new BufferedWriter(writer);
            
            HashMap<String,Object> json = convert(pop);
            JSONEncoder encoder = new JSONEncoder();
            encoder.setIndent("");
            encoder.setIgnoreUnicode(true);
            encoder.encode(json,buff);
            
            buff.close();
            buffOut.close();
        }
        catch(Exception exp)
        {
            throw new RuntimeException(exp);
        }
        
        return retVal;
    }
    
    @SuppressWarnings("unchecked")
    protected static void convert(HashMap<String,Object> json,Population pop,Run run)
    {
        ArrayList<Object> popList = (ArrayList<Object>) json.get(POPULATION);
        
        if(popList != null)
        {
            for(Object o : popList)
            {
                HashMap<String,Object> map = (HashMap<String,Object>)o;
                
                Individual ind = new Individual();

                ind.setParentOne((String) map.get(PARENT_ONE));
                ind.setParentTwo((String) map.get(PARENT_TWO));
                ind.setUserData((String) map.get(USER_DATA));
                ind.setId((String) map.get(ID));
                if(map.get(FITNESS)!=null) ind.setFitness(((Number) map.get(FITNESS)).doubleValue());
                String archive = (String)map.get(CHROMOSOME);

                try
                {
                    Chromosome chromo = (Chromosome) run.createObject(GenetikConstants.CHROMOSOME);
                    chromo.initalizeFromArchive(archive);
                    ind.setChromosome(chromo);
                }
                catch(Exception exp)
                {
                    throw new IllegalArgumentException("Error loading chromosome from file",exp);
                }
                
                ArrayList<Object> scores = (ArrayList<Object>) map.get(SCORES);
                
                if(scores != null)
                {
                    double[] raw = new double[scores.size()];
                    
                    for(int i=0,max=raw.length;i<max;i++)
                    {
                        raw[i] = ((Number)scores.get(i)).doubleValue();
                    }
                    
                    ind.setRawScores(raw);
                }
                
                pop.set(-1,ind);
            }
        }
    }
    
    protected static HashMap<String,Object> convert(Population pop)
    {
        HashMap<String,Object> root = new HashMap<String,Object>();
        ArrayList<Object> popList = new ArrayList<Object>();
        
        for(int i=0,max=pop.size();i<max;i++)
        {
            HashMap<String,Object> map = new HashMap<String,Object>();
            Individual ind = pop.get(i);

            map.put(CHROMOSOME, ind.getChromosome().toArchiveFormat());
            if(ind.getParentOne()!=null) map.put(PARENT_ONE, ind.getParentOne());
            if(ind.getParentTwo()!=null) map.put(PARENT_TWO, ind.getParentTwo());
            map.put(ID, ind.getId());
            if(ind.getFitness()!=Integer.MIN_VALUE) map.put(FITNESS, ind.getFitness());
            if(ind.getUserData()!=null) map.put(USER_DATA, ind.getUserData());
            
            double[] rawScores = ind.getRawScores();
            
            if(rawScores != null)
            {
                ArrayList<Object> scores = new ArrayList<Object>();
                
                for(int j=0,maxj=rawScores.length;j<maxj;j++)
                {
                    scores.add(new Double(rawScores[j]));
                }
                
                map.put(SCORES, scores);
            }
            
            popList.add(map);
        }
        
        root.put(POPULATION, popList);
        
        return root;
    }
}
