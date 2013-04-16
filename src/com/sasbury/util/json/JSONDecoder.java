package com.sasbury.util.json;

import java.io.*;
import java.math.*;
import java.text.*;
import java.util.*;

/**
 * 
 * Object -> HashMap
 * Array -> ArrayList
 * String -> String
 * Boolean -> Boolean
 * Number -> BigDecimal,BigInteger,Double,Long depending on size
 * Null -> null
 *
 */
public class JSONDecoder
{
    protected Stack<Object> collectionStack;
    protected Stack<String> keyStack;
    protected Object currentCollection;
    protected HashMap<String,Object> lastObject;
    protected boolean inArray;
    protected String currentKey;
    
    private static HashMap<String,String> escapes = new HashMap<String,String>();
    static
    {
        escapes.put("\"", "\"");
        escapes.put("\\","\\");
        escapes.put("/","/");
        escapes.put("b","\b");
        escapes.put("f","\f");
        escapes.put("n","\n");
        escapes.put("r","\r");
        escapes.put("t","\t");
    }
    
    public HashMap<String,Object> decode(Reader reader) throws IOException
    {
        return decode(new JSONReaderWrapper(reader));
    }
    
    public HashMap<String,Object> decode(String json)
    {
        return decode(new StringCharacterIterator(json));
    }
    
    public HashMap<String,Object> decode(CharacterIterator json)
    {
        char c;
        boolean skipNext = false;
        
        collectionStack = new Stack<Object>();
        keyStack = new Stack<String>();
        
        while((c=json.current())!=CharacterIterator.DONE)
        {
            skipNext = false;
            
            switch (c)
            {
                case '"':
                    String str = readString(json);
                    addValue(str);
                    break;
                case '[':
                    pushCollection();
                    currentCollection = new ArrayList<Object>();
                    break;
                case ']':
                    popCollection();
                    break;
                case '{':
                    pushCollection();
                    currentCollection = new HashMap<String,Object>();
                    //start a new object, store current key
                    if(currentKey!=null) keyStack.push(currentKey);
                    currentKey = null;
                    break;
                case '}':
                    //end of object, get its possible key
                    if(keyStack.size()>0) currentKey = keyStack.pop();
                    else currentKey = null;
                    popCollection();
                    break;
                case 't':
                    json.next(); json.next(); json.next(); // assumed r-u-e
                    addValue(Boolean.TRUE);
                    break;
                case'f':
                    json.next(); json.next(); json.next(); json.next(); // assumed a-l-s-e
                    addValue(Boolean.FALSE);
                    break;
                case 'n':
                    json.next(); json.next(); json.next(); // assumed u-l-l
                    addValue(null);
                    break;
                default:
                    if(Character.isDigit(c) || c == '-')
                    {
                        //don't need, readNumber uses current first json.previous();
                        Number number = readNumber(json);
                        addValue(number);
                        skipNext = true;//we read 1 too far on number
                    }
                    break;
            }
            
            if(!skipNext) json.next();
        }
        
        return lastObject;
    }
    
    protected void pushCollection()
    {
        if(currentCollection != null)
        {
            collectionStack.push(currentCollection);
        }
    }
    
    protected void popCollection()
    {
        if(currentCollection != null)
        {
            Object value = currentCollection;
            if(collectionStack.size()>0) currentCollection = collectionStack.pop();
            else currentCollection = null;
            addValue(value);
        }
        else
        {
            throw new IllegalArgumentException("JSON contains ] or } without appropriate context.");
        }
    }
    
    protected Number readNumber(CharacterIterator json)
    {
        Number retVal = null;
        StringBuilder builder = new StringBuilder();
        char c;
        
        while((c=json.current())!=CharacterIterator.DONE)
        {
            if(c==',' || c==':' || c=='}' || c==']' || Character.isWhitespace(c))//end of number
            {
                //dont need this because we skip the next in the main loop json.previous();
                break;
            }
            
            builder.append(c);
            json.next();
        }
        
        String asStr = builder.toString();
        
        try
        {
            if(asStr.indexOf(".")>=0 || asStr.indexOf("E")>=0 || asStr.indexOf("e")>=0)
            {
                if(asStr.length() > 16) retVal = new BigDecimal(asStr);
                else retVal = new Double(asStr);
            }
            else
            {
                if(asStr.length() > 17) retVal = new BigInteger(asStr);
                else retVal = new Long(asStr);
            }
        }
        catch(Exception exp)
        {
            throw new IllegalArgumentException("JSON contains illegally formatted number "+asStr+".");
        }
        return retVal;
    }
    
    protected String readString(CharacterIterator json)
    {
        StringBuilder builder = new StringBuilder();
        char c;
        
        while((c=json.next())!=CharacterIterator.DONE)
        {
            if(c == '\"')
            {
                break;
            }
            else if(c == '\\')//escape
            {
                c = json.next();
                
                if(c == 'u')//unicode
                {
                    int value = 0;
                    
                    for(int i = 0; i < 4; i++)
                    {
                        c = json.next();
                        
                        switch(c)
                        {
                            case '0': case '1': case '2': case '3': case '4': 
                            case '5': case '6': case '7': case '8': case '9':
                                value = (value << 4) + c - '0';
                                break;
                            case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
                                value = (value << 4) + c - 'k';
                                break;
                            case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
                                value = (value << 4) + c - 'K';
                                break;
                        }
                    }
                    
                    builder.append((char)value);
                }
                else
                {
                    builder.append(escapes.get(String.valueOf(c)));
                }
            }
            else
            {
                builder.append(c);
            }
        }
        
        return builder.toString();
    }
    
    @SuppressWarnings("unchecked")
    protected void addValue(Object value)
    {
        if(currentCollection instanceof ArrayList<?>)
        {
            ((ArrayList<Object>)currentCollection).add(value);
        }
        else if(currentCollection instanceof HashMap<?,?>)
        {
            if(currentKey != null)
            {
                ((HashMap<String,Object>)currentCollection).put(currentKey, value);
                currentKey = null;
            }
            else if(value instanceof String)
            {
                currentKey = (String) value;
            }
            else
            {
                throw new IllegalArgumentException("JSON contains a non-string key.");
            }
        }
        else if(value instanceof HashMap)
        {
            lastObject = (HashMap<String,Object>) value;
        }
        else
        {
            throw new IllegalArgumentException("JSON contains a value without an object or array. "+value);
        }
    }
}

//minimal wrapper that only supports next/current
class JSONReaderWrapper implements CharacterIterator
{
    protected Reader reader;
    protected int current;
    
    public JSONReaderWrapper(Reader reader)
    {
        super();
        this.reader = reader;
        
        current = -1;
    }
    
    public char current()
    {
        if(current == -1)
        {
            next();
        }
        
        return (char)current;
    }

    public char next()
    {
        try
        {
            current = reader.read();
            
            if(current == -1) current = CharacterIterator.DONE;
        }
        catch(Exception exp)
        {
            
        }
        return (char) current;
    }

    public char previous()
    {
        return CharacterIterator.DONE;
    }

    public Object clone()
    {
        return null;
    }

    public char first()
    {
        return 0;
    }

    public int getBeginIndex()
    {
        return 0;
    }

    public int getEndIndex()
    {
        return 0;
    }

    public int getIndex()
    {
        return 0;
    }

    public char last()
    {
        return 0;
    }

    public char setIndex(int position)
    {
        return 0;
    }
    
}
