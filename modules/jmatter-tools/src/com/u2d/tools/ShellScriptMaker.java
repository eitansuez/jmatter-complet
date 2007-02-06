package com.u2d.tools;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Java;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Apr 11, 2006
 * Time: 11:55:24 AM
 */
public class ShellScriptMaker extends Java
{
   File _file;
   public void setOutputFile(File file)
   {
      _file = file;
   }
   
   public void execute()
         throws BuildException
   {
      if (_file == null)
      {
         throw new BuildException("Must specify outputFile");
      }
      
      try
      {
         String cmd = getCommandLine().toString();
         FileWriter writer = new FileWriter(platformQualifySuffix(_file));
         writer.write(cmd);
         writer.close();
      }
      catch (IOException e)
      {
         e.printStackTrace();
         throw new BuildException("ShellScriptMaker failed to write run script to: "+_file.toString(), e);
      }
   }
   
   private File platformQualifySuffix(File file)
   {
      String path = file.getAbsolutePath();
      String name = file.getName();
      if (name.indexOf(".") != -1)
      {
         return file; // no change
      }
      
      String os = System.getProperty("os.name").toLowerCase();
      if (os.indexOf("windows") != -1 || os.indexOf("nt") != -1)
      {
         return new File(path+".bat");
      }
      else
      {
         return new File(path+".sh");
      }
   }
   
}
