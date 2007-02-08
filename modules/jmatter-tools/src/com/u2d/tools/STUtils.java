package com.u2d.tools;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Feb 6, 2007
 * Time: 2:54:14 PM
 */
public class STUtils
{
   public static StringTemplate templateForFile(File file)
   {
      StringTemplateGroup group = new StringTemplateGroup("mygroup", file.getParent());
      String name = file.getName();
      if (name.endsWith(".st"))
      {
         name = name.substring(0, name.length() - 3);
      }
      return group.getInstanceOf(name);
   }
   
   public static void toFile(StringTemplate template, File target) throws IOException
   {
      String result = template.toString();
      FileWriter writer = new FileWriter(target);
      writer.write(result);
      writer.close();
   }

}
