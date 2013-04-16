package test.com.sasbury.genetik;

import java.io.*;
import java.util.*;

import com.sasbury.util.json.*;

import junit.framework.*;

public class JSONDecoderTests extends TestCase
{
    public void testSample0() throws Exception
    {
        JSONDecoder decoder = new JSONDecoder();
        InputStream input = getClass().getClassLoader().getResourceAsStream("test/com/sasbury/genetik/json/sample0.json");
        Reader reader = new BufferedReader(new InputStreamReader(input));
        HashMap<String,Object> obj = decoder.decode(reader);
        
        assertNotNull(obj);
        assertNotNull(obj.get("name"));
        assertEquals(obj.get("name"),"hello");
    }
    
    @SuppressWarnings("unchecked")
    public void testSample1() throws Exception
    {
        JSONDecoder decoder = new JSONDecoder();
        InputStream input = getClass().getClassLoader().getResourceAsStream("test/com/sasbury/genetik/json/sample1.json");
        Reader reader = new BufferedReader(new InputStreamReader(input));
        HashMap<String,Object> obj = decoder.decode(reader);
        
        assertNotNull(obj);
        assertNotNull(obj.get("glossary"));
        assertEquals(((HashMap<String,Object>)obj.get("glossary")).get("title"),"example glossary");
    }
    
    @SuppressWarnings("unchecked")
    public void testSample2() throws Exception
    {
        JSONDecoder decoder = new JSONDecoder();
        InputStream input = getClass().getClassLoader().getResourceAsStream("test/com/sasbury/genetik/json/sample2.json");
        Reader reader = new BufferedReader(new InputStreamReader(input));
        HashMap<String,Object> obj = decoder.decode(reader);
        
        assertNotNull(obj);
        assertNotNull(obj.get("menu"));
        HashMap<String,Object> menu = (HashMap<String,Object>)obj.get("menu");
        HashMap<String,Object> popup = (HashMap<String,Object>)menu.get("popup");
        ArrayList<Object> menuitem = (ArrayList<Object>)popup.get("menuitem");
        HashMap<String,Object> item = (HashMap<String,Object>)menuitem.get(1);
        String value = (String) item.get("onclick");
        assertEquals(value,"OpenDoc()");
    }
    
    @SuppressWarnings("unchecked")
    public void testSample3() throws Exception
    {
        JSONDecoder decoder = new JSONDecoder();
        InputStream input = getClass().getClassLoader().getResourceAsStream("test/com/sasbury/genetik/json/sample3.json");
        Reader reader = new BufferedReader(new InputStreamReader(input));
        HashMap<String,Object> obj = decoder.decode(reader);
        
        assertNotNull(obj);
        assertNotNull(obj.get("widget"));
        HashMap<String,Object> widget = (HashMap<String,Object>)obj.get("widget");
        HashMap<String,Object> window = (HashMap<String,Object>)widget.get("window");
        Long value = (Long) window.get("width");
        assertEquals(value.longValue(),500l);
    }
    
    @SuppressWarnings("unchecked")
    public void testSample4() throws Exception
    {
        JSONDecoder decoder = new JSONDecoder();
        InputStream input = getClass().getClassLoader().getResourceAsStream("test/com/sasbury/genetik/json/sample4.json");
        Reader reader = new BufferedReader(new InputStreamReader(input));
        HashMap<String,Object> obj = decoder.decode(reader);
        
        assertNotNull(obj);
        assertNotNull(obj.get("web-app"));
        HashMap<String,Object> webapp = (HashMap<String,Object>)obj.get("web-app");
        ArrayList<Object> servlet = (ArrayList<Object>)webapp.get("servlet");
        HashMap<String,Object> init = (HashMap<String,Object>)((HashMap<String,Object>)servlet.get(0)).get("init-param");
        Long value = (Long) init.get("cacheTemplatesTrack");
        Boolean bool = (Boolean) init.get("useJSP");
        assertEquals(value.longValue(),100);
        assertEquals(bool.booleanValue(),false);
    }
    
    @SuppressWarnings("unchecked")
    public void testSample5() throws Exception
    {
        JSONDecoder decoder = new JSONDecoder();
        InputStream input = getClass().getClassLoader().getResourceAsStream("test/com/sasbury/genetik/json/sample5.json");
        Reader reader = new BufferedReader(new InputStreamReader(input));
        HashMap<String,Object> obj = decoder.decode(reader);
        
        assertNotNull(obj);
        assertNotNull(obj.get("menu"));
        HashMap<String,Object> menu = (HashMap<String,Object>)obj.get("menu");
        ArrayList<Object> items = (ArrayList<Object>)menu.get("items");
        assertNull(items.get(2));
        assertNull(items.get(6));
        assertNotNull(items.get(3));
    }
}
