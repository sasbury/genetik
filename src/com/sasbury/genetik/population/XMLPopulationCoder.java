package com.sasbury.genetik.population;

import java.io.*;

import javax.xml.parsers.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;

public class XMLPopulationCoder
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
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser saxParser = factory.newSAXParser();
                XMLPopSaxHandler handler = new XMLPopSaxHandler(pop,run);
                saxParser.parse(file,handler);
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
            
            buff.append("<pop>\n");
            
            for(int i=0,max= pop.size();i<max;i++)
            {
                Individual ind  = pop.get(i);
                
                buff.append(" <ind>\n");
                    buff.append("  <id>");
                    buff.append(String.valueOf(ind.getId()));
                    buff.append("</id>\n");
    
                    if(ind.getParentOne()!=null)
                    {
                        buff.append("  <p1>");
                        buff.append(String.valueOf(ind.getParentOne()));
                        buff.append("</p1>\n");
                    }
    
                    if(ind.getParentTwo()!=null)
                    {
                        buff.append("  <p2>");
                        buff.append(String.valueOf(ind.getParentTwo()));
                        buff.append("</p2>\n");
                    }
    
                    buff.append("  <fit>");
                    buff.append(String.valueOf(ind.getFitness()));
                    buff.append("</fit>\n");
    
                    if(ind.getRawScores() != null)
                    {
                        buff.append("  <raw>");
                        boolean first = true;
                        for(double score : ind.getRawScores())
                        {
                            if(!first) buff.append(",");
                            buff.append(String.valueOf(score));
                            first = false;
                        }
                        buff.append("</raw>\n");
                    }
    
                    buff.append("  <genes>");
                    buff.append(ind.getChromosome().toArchiveFormat());
                    buff.append("</genes>\n");
    
                    if(ind.getUserData()!=null)
                    {
                        buff.append("  <user>");
                        buff.append(ind.getUserData());
                        buff.append("</user>\n");
                    }
                buff.append(" </ind>\n");
            }
            
            buff.append("</pop>");
            
            buff.close();
            buffOut.close();
        }
        catch(Exception exp)
        {
            throw new RuntimeException(exp);
        }
        
        return retVal;
    }
}

class XMLPopSaxHandler extends DefaultHandler
{
    protected Individual curIndividual;
    protected String curEntity;
    protected Population pop;
    protected Run run;
    protected StringBuilder builder;
    
    public XMLPopSaxHandler(Population pop,Run run)
    {
        this.pop = pop;
        this.run = run;
    }
    
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        if(XMLPopulationCoder.INDIVIDUAL.equals(qName))
        {
            curIndividual = new Individual();
            curEntity = null;
            builder = null;
        }
        else
        {
            curEntity = qName;
            builder = new StringBuilder();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException
    {
        if(builder != null) builder.append(ch,start,length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        if(curIndividual!=null)
        {
            if(XMLPopulationCoder.INDIVIDUAL.equals(qName))
            {
                pop.set(-1, curIndividual);
            }
            else if(XMLPopulationCoder.CHROMOSOME.equals(curEntity))
            {
                String archive = builder.toString().trim();//for some reason we are getting carraige returns

                try
                {
                    Chromosome chromo = (Chromosome) run.createObject(GenetikConstants.CHROMOSOME);
                    chromo.initalizeFromArchive(archive);
                    curIndividual.setChromosome(chromo);
                }
                catch(Exception exp)
                {
                    throw new SAXException("Error loading chromosome from file",exp);
                }
            }
            else if(XMLPopulationCoder.FITNESS.equals(curEntity))
            {
                curIndividual.setFitness(Double.parseDouble(builder.toString()));
            }
            else if(XMLPopulationCoder.PARENT_ONE.equals(curEntity))
            {
                curIndividual.setParentOne(builder.toString());
            }
            else if(XMLPopulationCoder.PARENT_TWO.equals(curEntity))
            {
                curIndividual.setParentTwo(builder.toString());
            }
            else if(XMLPopulationCoder.ID.equals(curEntity))
            {
                curIndividual.setId(builder.toString());
            }
            else if(XMLPopulationCoder.SCORES.equals(curEntity))
            {
                String str = builder.toString();
                String vals[] = str.split(",");
                double scores[] = new double[vals.length];
                
                for(int i=0,max=scores.length;i<max;i++)
                {
                    String s = vals[i];
                    double d = Double.parseDouble(s.trim());
                    scores[i] = d;
                }
                
                curIndividual.setRawScores(scores);
            }
            else if(XMLPopulationCoder.USER_DATA.equals(curEntity))
            {
                curIndividual.setUserData(builder.toString());
            }
        }
    }
}