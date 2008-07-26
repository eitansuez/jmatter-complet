package com.u2d.tools;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Path;
import org.antlr.stringtemplate.StringTemplate;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jan 9, 2007
 * Time: 4:55:40 PM
 */
public class AntPersistClassesMaker extends Task
{
   File _template, _target;
   FileSet _fileset;
   
   public void setTemplate(File template)
   {
      _template = template;
   }
   public void setTarget(File target)
   {
      _target = target;
   }
   /**
    * supports only a single fileset at the moment..
    */
   public void addFileset(FileSet fileset)
   {
      _fileset = fileset;
   }
   
   Path _path;
   
   public void setClasspathRef(Reference r)
   {
      if (_path == null)
      {
         _path = new Path(getProject());
      }
      _path.createPath().setRefid(r);
   }
   
   public void execute()
         throws BuildException
   {
      Set<String> classSet = new HashSet<String>();
      
      DirectoryScanner ds = _fileset.getDirectoryScanner(getProject());
      for (int i=0; i < ds.getIncludedFiles().length; i++)
      {
         try
         {
            String fileName = ds.getIncludedFiles()[i];
            String className = fileName.replace(File.separator,  ".");
            int idx = className.indexOf(".java");
            className = className.substring(0, idx);
            
            ClassLoader loader = new AntClassLoader(getProject(), _path);
            Class cls = loader.loadClass(className);
            Class annotation = loader.loadClass("javax.persistence.Entity");
            if (cls.isAnnotationPresent(annotation))
            {
               classSet.add(cls.getName());
            }
         }
         catch (ClassNotFoundException e)
         {
            e.printStackTrace();
         }
      }

      produceTarget(classSet);
   }
   
   private void produceTarget(Set classSet)
   {
      if (_template == null || _target == null)
      {
         throw new BuildException("Must specify both 'template' and " +
               "'target' parameters");
      }
      
      try
      {
         StringTemplate template = STUtils.templateForFile(_template);
         template.setAttribute("classnames", classSet);
         STUtils.toFile(template, _target);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }
   
}
