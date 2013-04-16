package test.com.sasbury.genetik;

import java.io.*;
import java.util.*;

import junit.framework.*;

import com.sasbury.util.json.*;

public class JSONEncoderTests extends TestCase
{
    public void testSample0() throws Exception
    {
        JSONDecoder decoder = new JSONDecoder();
        JSONEncoder encoder = new JSONEncoder();
        InputStream input = getClass().getClassLoader().getResourceAsStream("test/com/sasbury/genetik/json/sample0.json");
        Reader reader = new BufferedReader(new InputStreamReader(input));
        HashMap<String,Object> obj = decoder.decode(reader);
        String json = encoder.encode(obj);
        HashMap<String,Object> obj2 = decoder.decode(json);
        
        assertEquals(obj, obj2);
    }
    
    public void testSample1() throws Exception
    {
        JSONDecoder decoder = new JSONDecoder();
        JSONEncoder encoder = new JSONEncoder();
        InputStream input = getClass().getClassLoader().getResourceAsStream("test/com/sasbury/genetik/json/sample1.json");
        Reader reader = new BufferedReader(new InputStreamReader(input));
        HashMap<String,Object> obj = decoder.decode(reader);
        String json = encoder.encode(obj);
        HashMap<String,Object> obj2 = decoder.decode(json);
        
        assertEquals(obj, obj2);
    }
    
    public void testSample2() throws Exception
    {
        JSONDecoder decoder = new JSONDecoder();
        JSONEncoder encoder = new JSONEncoder();
        InputStream input = getClass().getClassLoader().getResourceAsStream("test/com/sasbury/genetik/json/sample2.json");
        Reader reader = new BufferedReader(new InputStreamReader(input));
        HashMap<String,Object> obj = decoder.decode(reader);
        String json = encoder.encode(obj);
        HashMap<String,Object> obj2 = decoder.decode(json);
        
        assertEquals(obj, obj2);
    }
    
    public void testSample3() throws Exception
    {
        JSONDecoder decoder = new JSONDecoder();
        JSONEncoder encoder = new JSONEncoder();
        InputStream input = getClass().getClassLoader().getResourceAsStream("test/com/sasbury/genetik/json/sample3.json");
        Reader reader = new BufferedReader(new InputStreamReader(input));
        HashMap<String,Object> obj = decoder.decode(reader);
        String json = encoder.encode(obj);
        HashMap<String,Object> obj2 = decoder.decode(json);
        
        assertEquals(obj, obj2);
    }
    
    public void testSample4() throws Exception
    {
        JSONDecoder decoder = new JSONDecoder();
        JSONEncoder encoder = new JSONEncoder();
        InputStream input = getClass().getClassLoader().getResourceAsStream("test/com/sasbury/genetik/json/sample4.json");
        Reader reader = new BufferedReader(new InputStreamReader(input));
        HashMap<String,Object> obj = decoder.decode(reader);
        String json = encoder.encode(obj);
        HashMap<String,Object> obj2 = decoder.decode(json);
        
        assertEquals(obj, obj2);
    }
    
    public void testSample5() throws Exception
    {
        JSONDecoder decoder = new JSONDecoder();
        JSONEncoder encoder = new JSONEncoder();
        InputStream input = getClass().getClassLoader().getResourceAsStream("test/com/sasbury/genetik/json/sample5.json");
        Reader reader = new BufferedReader(new InputStreamReader(input));
        HashMap<String,Object> obj = decoder.decode(reader);
        String json = encoder.encode(obj);
        HashMap<String,Object> obj2 = decoder.decode(json);
        
        assertEquals(obj, obj2);
    }
}
