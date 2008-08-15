package com.u2d.tools;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.antlr.stringtemplate.StringTemplate;
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
   private String[] _excludejars;
   public void setTemplate(File template) { _template = template; }
   public void setProps(File propsFile) { _propsFile = propsFile; }
   public void setJarbasepath(File jarbasepath) { _jarbasepath = jarbasepath; }
   public void setTofile(File tofile) { _tofile = tofile; }
   public void setExcludejars(String excludejars)
   {
      _excludejars = excludejars.split(",");
   }

   public void execute()
         throws BuildException
   {
      try
      {
         StringTemplate template = STUtils.templateForFile(_template);
         bindProperties(template);

         List jarpathlist = jarpaths(_jarbasepath);
         template.setAttribute("jars", jarpathlist);

         STUtils.toFile(template, _tofile);
      }
      catch (IOException ex)
      {
         throw new BuildException("Failed to generate jnlp file", ex);
      }
   }


   private void bindProperties(StringTemplate template) throws IOException
   {
      InputStream is = new FileInputStream(_propsFile);
      Properties props = new Properties();
      props.load(is);
      Enumeration en = props.propertyNames();
      String key;
      while (en.hasMoreElements())
      {
         key = (String) en.nextElement();
         template.setAttribute(key, props.getProperty(key));
      }
   }

   private List<String> jarpaths(File basePath) throws IOException
   {
      List<String> list = new ArrayList<String>();
      File[] paths = basePath.listFiles();
      for (int i=0; i<paths.length; i++)
      {
         if ( paths[i].isFile() && paths[i].getName().endsWith(".jar")
               && (!excluded(paths[i].getName())) )
         {
            String relativePath = paths[i].getCanonicalPath();
            String basepath = _jarbasepath.getAbsolutePath() + File.separator;
            int index = relativePath.indexOf(basepath);
            relativePath = relativePath.substring(index + basepath.length());
            // reviewed spec and entries are url's:  that is the path separators must be foreslashes
            // (thanks to lex for catching this) 
            list.add(relativePath.replace('\\', '/'));
         }
         else if (paths[i].isDirectory())
         {
            list.addAll(jarpaths(paths[i]));
         }
      }
      return list;
   }

   private boolean excluded(String path)
   {
      for (String exclpath : _excludejars)
      {
         if (exclpath.equals(path))
         {
            return true;
         }
      }
      return false;
   }

}

