package com.u2d.tools;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.Template;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 30, 2005
 * Time: 5:20:11 PM
 */
public class JnlpMaker extends Task
{
   private File _template, _propsFile, _jarbasepath, _tofile;
   private String _excludejar;
   public void setTemplate(File template) { _template = template; }
   public void setProps(File propsFile) { _propsFile = propsFile; }
   public void setJarbasepath(File jarbasepath) { _jarbasepath = jarbasepath; }
   public void setTofile(File tofile) { _tofile = tofile; }
   public void setExcludejar(String excludejar) { _excludejar = excludejar; }

   public void execute()
         throws BuildException
   {
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
         addPropertiesToContext(context);

         List jarpathlist = jarpaths(_jarbasepath);
         context.put("jarpathlist", jarpathlist);

         Template template = Velocity.getTemplate(_template.getName());
         FileWriter writer = new FileWriter(_tofile);
         template.merge(context, writer);
         writer.flush();
         writer.close();
      }
      catch (Exception ex)
      {
         throw new BuildException("Failed to generate jnlp file", ex);
      }
   }


   private void addPropertiesToContext(VelocityContext context)
         throws Exception
   {
      InputStream is = new FileInputStream(_propsFile);
      Properties props = new Properties();
      props.load(is);
      Enumeration en = props.propertyNames();
      String key = "";
      while (en.hasMoreElements())
      {
         key = (String) en.nextElement();
         context.put(key, props.getProperty(key));
      }
   }

   private List jarpaths(File basePath) throws Exception
   {
      List list = new ArrayList();
      File[] paths = basePath.listFiles();
      for (int i=0; i<paths.length; i++)
      {
         if ( paths[i].isFile() && paths[i].getName().endsWith(".jar")
               && (!_excludejar.equals(paths[i].getName())) )
         {
            String relativePath = paths[i].getCanonicalPath();
            String basepath = _jarbasepath.getAbsolutePath() + File.separator;
            int index = relativePath.indexOf(basepath);
            relativePath = relativePath.substring(index + basepath.length());
            list.add(relativePath);
         }
         else if (paths[i].isDirectory())
         {
            list.addAll(jarpaths(paths[i]));
         }
      }
      return list;
   }

}
