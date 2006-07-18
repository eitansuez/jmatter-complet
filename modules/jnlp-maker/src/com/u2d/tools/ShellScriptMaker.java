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
         FileWriter writer = new FileWriter(_file);
         writer.write(cmd);
         writer.close();
      }
      catch (IOException e)
      {
         e.printStackTrace();
         throw new BuildException("SheellScriptMaker failed to write run script to: "+_file.toString(), e);
      }
   }
}
