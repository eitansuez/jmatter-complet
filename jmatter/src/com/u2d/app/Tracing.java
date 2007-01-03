package com.u2d.app;

import java.util.logging.*;
import java.io.StringWriter;
import java.io.PrintWriter;

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
   public static final String JMATTER_LOGGER_NAME = "com.u2d.jmatter";

   static
   {
      Logger tracer = Logger.getLogger(JMATTER_LOGGER_NAME);
      tracer.setUseParentHandlers(false);

      tracer.setLevel(Level.FINE);
      Handler handler = new ConsoleHandler();
      handler.setFormatter(new ShortFormatter());
      tracer.addHandler(handler);
      tracer.config("Tracing has been configured..level is "+tracer.getLevel());
   }

   public static Logger tracer() { return Logger.getLogger(JMATTER_LOGGER_NAME); }


   static class ShortFormatter extends Formatter
   {
      private String lineSeparator = (String) java.security.AccessController.doPrivileged(
            new sun.security.action.GetPropertyAction("line.separator"));

      public synchronized String format(LogRecord record)
      {
         StringBuffer sb = new StringBuffer();
         String message = formatMessage(record);
         sb.append(record.getLevel().getLocalizedName());
         sb.append(": ");
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
