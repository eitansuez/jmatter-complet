package com.u2d.tools;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.antlr.stringtemplate.StringTemplate;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Basically processes template to output (target) file.
 * Useful in cases where template is self-contained / does not need context.
 */
public class BasicTemplateTask extends Task
{
   File _template, _target;
   
   public void setTemplate(File template) { _template = template; }
   public void setTarget(File target) { _target = target; }

   public void execute() throws BuildException
   {
      if (_template == null || _target == null)
      {
         throw new BuildException("Must specify both 'template' and " +
               "'target' parameters");
      }
      
      StringTemplate template = STUtils.templateForFile(_template);
      
      template.setAttribute("java_version", System.getProperty("java.version"));
      template.setAttribute("isjava6", System.getProperty("java.version").startsWith("1.6"));
      
      try
      {
         STUtils.toFile(template, _target);
      }
      catch (IOException e)
      {
         throw new BuildException(e);
      }
   }
   
}
