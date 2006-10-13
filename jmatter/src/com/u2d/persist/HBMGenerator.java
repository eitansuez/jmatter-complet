/*
 * Created on Mar 31, 2004
 */
package com.u2d.persist;

import com.u2d.app.Tracing;
import com.u2d.app.HBMPersistenceMechanism;
import java.util.Set;
import java.io.File;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Use HBMMaker to generate hbm.xml files for each item in app.properties' persistClasses
 * 
 * @author Eitan Suez
 */
public class HBMGenerator
{
   private Set<Class> _persistClasses;
   
   public HBMGenerator() {}
   
   public void setPersistClasses(Set<Class> classes)
   {
      _persistClasses = classes;
   }
   

   public void processClassList()
   {
      for (Class clazz : _persistClasses)
      {
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
      ApplicationContext context =
            new ClassPathXmlApplicationContext("hbmGeneratorContext.xml");
      HBMGenerator generator = (HBMGenerator) context.getBean("hbm-generator");
      generator.processClassList();
   }

}
