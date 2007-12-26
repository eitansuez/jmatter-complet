package com.u2d.ui;

import java.awt.event.InputEvent;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 29, 2005
 * Time: 1:08:34 PM
 */
public class Platform
{
   public static boolean APPLE =
         System.getProperty("os.name").toLowerCase().startsWith("mac os x");

   public static int mask()
   {
      return (APPLE) ? InputEvent.META_MASK : InputEvent.CTRL_MASK;
   }
   
   private static final String JAVA_VERSION = getSystemProperty("java.version");
   public static boolean ISJAVA6 =  startsWith(JAVA_VERSION, "1.6");

   public static String getSystemProperty(String key)
   {
       try
       {
           return System.getProperty(key);
       }
       catch (SecurityException e)
       {
           System.err.println("Can't read the System property " + key + ".");
           return null;
       }
   }
   
   private static boolean startsWith(String str, String prefix)
   {
       return str != null && str.startsWith(prefix);
   }
}
