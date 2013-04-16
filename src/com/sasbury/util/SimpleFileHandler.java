package com.sasbury.util;

import java.io.*;
import java.util.logging.*;

//file handler with no locking
public class SimpleFileHandler extends StreamHandler
{
    BufferedOutputStream log;
    
    public SimpleFileHandler(File file) throws Exception
    {
        log = new BufferedOutputStream(new FileOutputStream(file,true));
        setOutputStream(log);
        setEncoding("UTF-8");
    }

    public synchronized void publish(LogRecord record)
    {
        super.publish(record);
        flush();
    }
}
