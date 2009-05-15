package com.u2d.app;

import java.util.logging.*;
import java.util.Date;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.text.MessageFormat;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Mar 25, 2006
 * Time: 8:37:02 PM
 * 
 * So as not to confuse code "logging" (tracing) with the higher-level
 * Logging of application events to database, i will use the term "tracing"
 * instead
 */
public class Tracing
{
   public static final String JMATTER_LOGGER_NAME = "org.jmatter";
   private static int LOGFILE_SIZELIMIT = 5 * 1024 * 1024;  // 5 MB

   static
   {
      configureLogging();
   }
   private static synchronized void configureLogging()
   {
//      Formatter formatter = new SimpleFormatter();
      Formatter formatter = new ShortFormatter();
      Level desiredLogLevel = Level.INFO;

      Logger rootLogger = Logger.getLogger("");

      // start out clean..
      for (Handler h : rootLogger.getHandlers())
      {
         rootLogger.removeHandler(h);
      }

      Handler handler = new ConsoleHandler();
      handler.setFormatter(formatter);
      handler.setLevel(desiredLogLevel);
      rootLogger.addHandler(handler);

      try
      {
         String filePattern = "application%g.log";
         Handler fileHandler = new FileHandler(filePattern, LOGFILE_SIZELIMIT, 3, false);
         fileHandler.setFormatter(formatter);
         fileHandler.setLevel(desiredLogLevel);
         rootLogger.addHandler(fileHandler);
      }
      catch (IOException ex)
      {
         ex.printStackTrace();
      }

      rootLogger.setLevel(desiredLogLevel);  // let child logger levels default to value inherited from root logger
      rootLogger.config("Tracing has been configured..level is "+rootLogger.getLevel());
   }
   public static Logger tracer() { return Logger.getLogger(JMATTER_LOGGER_NAME); }


   static class ShortFormatter extends Formatter
   {
      private String lineSeparator = System.getProperty("line.separator");
      Date dat = new Date();
      private final static String format = "{0,date} {0,time}";
      private MessageFormat formatter = new MessageFormat(format);
      private Object args[] = new Object[1];

      public synchronized String format(LogRecord record)
      {
         StringBuffer sb = new StringBuffer();

         dat.setTime(record.getMillis());
         args[0] = dat;
         formatter.format(args, sb, null);
         sb.append(" / ");

         sb.append(record.getLevel().getLocalizedName());
         sb.append(": ");

         String message = formatMessage(record);
         sb.append(message);

         sb.append(lineSeparator);
         if (record.getThrown() != null)
         {
            try
            {
               StringWriter sw = new StringWriter();
               PrintWriter pw = new PrintWriter(sw);
               record.getThrown().printStackTrace(pw);
               pw.close();
               sb.append(sw.toString());
            }
            catch (Exception ex) {}
         }
         return sb.toString();
      }
   }

}
