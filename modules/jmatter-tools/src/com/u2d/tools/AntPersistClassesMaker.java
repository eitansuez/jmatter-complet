package com.u2d.tools;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Path;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.Template;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.util.Properties;
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
   
   Path _path = new Path(getProject());
   
   public void setClasspathRef(Reference r)
   {
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
            Class annotation = loader.loadClass("com.u2d.persist.Persist");
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
         Properties config = new Properties();
         config.put("resource.loader", "file");
         config.put("file.resource.loader.class",
                    "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
         config.put("file.resource.loader.path",
                    _template.getParent());

         Velocity.init(config);

         VelocityContext context = new VelocityContext();

         context.put("classnames", classSet);

         Template velotemplate = Velocity.getTemplate(_template.getName());
         PrintWriter writer = new PrintWriter(new FileWriter(_target));
         
         velotemplate.merge(context, writer);
         writer.flush();
         writer.close();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
   
}
