package com.sasbury.util;

import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.util.logging.Formatter;

public class CompactFormatter extends Formatter
{
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public String format(LogRecord record)
    {
        StringBuilder sb = new StringBuilder();

        sb.append(new Date(record.getMillis())).append(" ").append(
                record.getLevel().getLocalizedName()).append(": ").append(
                formatMessage(record)).append(LINE_SEPARATOR);

        if(record.getThrown() != null)
        {
            try
            {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                sb.append(sw.toString());
            }
            catch(Exception ex)
            {
                // ignore
            }
        }

        String string = sb.toString();
        return string;
    }
}