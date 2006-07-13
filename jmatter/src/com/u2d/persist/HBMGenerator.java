/*
 * Created on Mar 31, 2004
 */
package com.u2d.persist;

import com.u2d.app.Application;
import com.u2d.app.Tracing;

import java.util.List;
import java.io.File;

/**
 * Use HBMMaker to generate hbm.xml files for each item in app.properties' persistClasses
 * 
 * @author Eitan Suez
 */
public class HBMGenerator
{

   public void processClassList(List persistClasses)
   {
      Class clazz = null;
      for (int i=0; i<persistClasses.size(); i++)
      {
         clazz = (Class) persistClasses.get(i);
         
         if (isUptodate(clazz)) continue;  // skip processing if file is uptodate

         Tracing.tracer().info("HBMGenerator is Processing "+clazz.getName());
         try
         {
            HBMMaker hbmmaker = new HBMMaker(clazz);
            hbmmaker.writeDocToFile();
         }
         catch (Exception ex)
         {
            ex.printStackTrace();
            System.exit(0);
         }
      }
   }
   
   private boolean isUptodate(Class clazz)
   {
      String classfilename = clazz.getName().replace('.', File.separatorChar) + ".class";
      String hbmxmlfilename = clazz.getName().replace('.', File.separatorChar) + ".hbm.xml";
      
      File hbmxmlfile = new File(hbmxmlfilename);
      if (!hbmxmlfile.exists())
         return false;
      
      File classfile = new File(classfilename);
      return (hbmxmlfile.lastModified() > classfile.lastModified());
   }
      

   public static void main(String[] args)
   {
      new Application(new HBMGenerator());
   }

}
