package com.sasbury.util.json;

import java.io.*;
import java.text.*;
import java.util.*;

public class JSONEncoder
{
    protected static char[] hexValues = "0123456789ABCDEF".toCharArray();
    
    protected String indent;
    protected boolean ignoreUnicode;
    protected String newLine;
    
    public JSONEncoder()
    {
        super();
        indent = "  ";
        ignoreUnicode = false;
        newLine = "\n";
    }

    public String getIndent()
    {
        return indent;
    }

    public void setIndent(String indent)
    {
        this.indent = indent;
    }

    public boolean isIgnoreUnicode()
    {
        return ignoreUnicode;
    }

    public void setIgnoreUnicode(boolean ignoreUnicode)
    {
        this.ignoreUnicode = ignoreUnicode;
    }

    public boolean isIncludeNewLines()
    {
        return newLine!=null && newLine.length()>0;
    }

    public void setIncludeNewLines(boolean includeNewLines)
    {
        newLine = (includeNewLines)?"\n":"";
    }

    public void encode(HashMap<String,Object> object,Writer writer) throws IOException
    {
        writeObject(object,writer,0);
        writer.write(newLine);
    }
    
    public String encode(HashMap<String,Object> object) throws IOException
    {
        StringWriter writer = new StringWriter();
        encode(object,writer);
        writer.close();
        return writer.toString();
    }
    
    protected void indent(Writer writer,int depth) throws IOException
    {
        if(indent!=null)
        {
            for(int i=0;i<depth;i++) writer.write(indent);
        }
    }
    
    protected void writeObject(HashMap<String,Object> object,Writer writer,int depth) throws IOException
    {
        writer.write("{");
        writer.write(newLine);
        
        Set<String> keySet = object.keySet();
        ArrayList<String> keys = new ArrayList<String>();
        keys.addAll(keySet);
        
        Collections.sort(keys);
        
        boolean first = true;
        
        for(String key : keys)
        {
            Object value = object.get(key);

            if(!first)
            {
                writer.write(",");
                writer.write(newLine);
            }
            indent(writer,depth+1);
            
            writeString(key,writer);
            writer.write(":");
            writeValue(value,writer,depth+1);
            
            first = false;
        }

        writer.write(newLine);
        indent(writer,depth);
        writer.write("}");
    }
    
    protected void writeArray(ArrayList<Object> object,Writer writer,int depth) throws IOException
    {
        writer.write("[");
        if(object.size()>1) writer.write(newLine);
        boolean first = true;
        
        for(Object value : object)
        {
            if(!first)
            {
                writer.write(",");
                writer.write(newLine);
            }
            indent(writer,depth);
            
            writeValue(value,writer,depth);
            first = false;
        }

        writer.write("]");
    }
    
    @SuppressWarnings("unchecked")
    protected void writeValue(Object value,Writer writer,int depth) throws IOException
    {
        if(value instanceof HashMap<?,?>)
        {
            writeObject((HashMap<String,Object>) value,writer,depth);
        }
        else if(value instanceof ArrayList<?>)
        {
            writeArray((ArrayList<Object>) value,writer,depth);
        }
        else if(value instanceof String)
        {
            writeString((String)value,writer);
        }
        else if(value instanceof Number)
        {
            writeNumber((Number)value,writer);
        }
        else if(value instanceof Boolean)
        {
            writeBoolean((Boolean)value,writer);
        }
        else
        {
            writeNull(writer);
        }
    }
    
    protected void writeString(String string,Writer writer) throws IOException
    {
        writer.write('\"');
        CharacterIterator it = new StringCharacterIterator(string);
        
        for(char c = it.first(); c != CharacterIterator.DONE; c = it.next())
        {
            if(c == '\"') writer.write("\\\"");
            else if(c == '\\') writer.write("\\\\");
            else if(c == '/') writer.write("\\/");
            else if(c == '\b') writer.write("\\b");
            else if(c == '\f') writer.write("\\f");
            else if(c == '\n') writer.write("\\n");
            else if(c == '\r') writer.write("\\r");
            else if(c == '\t') writer.write("\\t");
            else if(!ignoreUnicode && Character.isISOControl(c))
            {
                int val = c;
                writer.write("\\u");
                for(int i = 0; i < 4; i++)
                {
                    int index = (val & 0xf000) >> 12;
                    writer.write(hexValues[index]);
                    val <<= 4;
                }
            }
            else
            {
                writer.write(c);
            }
        }
        writer.write('\"');
    }
    
    protected void writeNumber(Number number,Writer writer) throws IOException
    {
        writer.write(number.toString());
    }
    
    protected void writeBoolean(Boolean bool,Writer writer) throws IOException
    {
        if(bool.booleanValue()) writer.write("true");
        else writer.write("false");
    }
    
    protected void writeNull(Writer writer) throws IOException
    {
        writer.append("null");
    }
}
