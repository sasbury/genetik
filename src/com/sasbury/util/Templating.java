package com.sasbury.util;

import java.util.*;
import java.util.regex.*;

public class Templating
{
    public static String templatize(String text,Map<String,String> replacements)
    {
        if(text == null) return null;
        
        Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
        Matcher matcher = pattern.matcher(text);
        StringBuilder builder = new StringBuilder();
        int i = 0;
        
        while(matcher.find())
        {
            String replacement = replacements.get(matcher.group(1));
            builder.append(text.substring(i, matcher.start()));
            if (replacement == null)
                builder.append("");
            else
                builder.append(replacement);
            i = matcher.end();
        }
        builder.append(text.substring(i, text.length()));
        return builder.toString();
    }
}
