package com.u2d.tools;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Dec 27, 2006
 * Time: 5:24:20 PM
 */
public class DriverPropertySetter extends Task
{
   private File jdbcDriverBasePath;
   public void setBasepath(File path)
   {
      jdbcDriverBasePath = path;
   }
   
   public void execute()
         throws BuildException
   {
      try
      {
         File base = getProject().getBaseDir();
         File hbmfile = new File(base, "resources/hibernate.properties");

         Properties p = new Properties();
         p.load(new FileInputStream(hbmfile));
         
         String drvclsname = p.getProperty("hibernate.connection.driver_class");
         
         String filename = filenameFromDriverClassName(drvclsname);
         getProject().log("driver filename is: "+filename);
         if (filename != null)
         {
            getProject().setNewProperty("jdbcdrivername", filename);
         }
         // otherwise don't set property name
      }
      catch (IOException ex)
      {
         throw new BuildException("File not found (hibernate.properties) ?", ex);
      }
   }
   
   private String filenameFromDriverClassName(String dcn)
   {
      dcn = dcn.toLowerCase();
      if (dcn.contains("oracle"))
      {
         return findFile("ojdbc");
      }
      else if (dcn.contains("postgres"))
      {
         return findFile("postgres");
      }
      else if (dcn.contains("mysql"))
      {
         return findFile("mysql");
      }
      else if (dcn.contains("h2"))
      {
         return findFile("h2");
      }
      else if (dcn.contains("hsqldb"))
      {
         return findFile("hsql");
      }
      return null;
   }
   
   private String findFile(String keyword)
   {
      getProject().log("Looking for file matching keyword "+keyword);
      
      File[] files = jdbcDriverBasePath.listFiles();
      for (File file : files)
      {
         if (file.getName().toLowerCase().contains(keyword))
         {
            return file.getName();
         }
      }
      return null;
   }
}
