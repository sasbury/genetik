package test.com.sasbury.genetik;

import java.util.*;

import com.sasbury.util.*;

import junit.framework.*;

public class TemplateTests extends TestCase
{
    public void testNoWildcards()
    {
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("one", "hello");
        map.put("two", "world");
        
        String source = "hello world";
        String result = Templating.templatize(source, map);
        assertEquals(source,result);
    }

    public void testNoValues()
    {
        HashMap<String,String> map = new HashMap<String,String>();

        String source = "${one} ${two}";
        String result = Templating.templatize(source, map);
        assertEquals(" ",result);
    }
    
    public void testOneValue()
    {
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("one", "hello");
        map.put("two", "world");
        
        String source = "${one}";
        String result = Templating.templatize(source, map);
        assertEquals("hello",result);
    }
    
    public void testTwoValues()
    {
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("one", "hello");
        map.put("two", "world");
        
        String source = "${one} ${two}";
        String result = Templating.templatize(source, map);
        assertEquals("hello world",result);
    }
    
    public void testMultipleValues()
    {
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("one", "hello");
        map.put("two", "world");
        
        String source = "${one} ${two} ${one}";
        String result = Templating.templatize(source, map);
        assertEquals("hello world hello",result);
    }
    
    public void testValuesInLongString()
    {
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("one", "hello");
        map.put("two", "world");
        
        String source = "this is a test ${one} ${two} ${one} with a fair bit of text";
        String result = Templating.templatize(source, map);
        assertEquals("this is a test hello world hello with a fair bit of text",result);
    }
    
    public void testNewLinesString()
    {
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("one", "hello");
        map.put("two", "world");
        
        String source = "this is a test\n${one} ${two} ${one}\nwith a fair bit of text";
        String result = Templating.templatize(source, map);
        assertEquals("this is a test\nhello world hello\nwith a fair bit of text",result);
    }
}
