/*
 * Created on Mar 10, 2004
 */
package com.u2d.persist;

import com.u2d.app.*;
import org.hibernate.cfg.*;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.File;


/**
 * @author Eitan Suez
 */
public abstract class HibernatePersistor implements HBMPersistenceMechanism
{
   protected Configuration _cfg;

   public HibernatePersistor()
   {
      // reduce verboseness of hibernate logger..
      Logger.getLogger("org.hibernate").setLevel(Level.WARNING);
      _cfg = new Configuration();
   }
   
   private String outputFilePath(String path)
   {
      if (!path.endsWith(File.separator))
         path += File.separator;
      return path + "schema.sql";
   }

   public void exportSchema(String path)
   {
      String outputFilePath = outputFilePath(path);
      
      SchemaExport tool = new SchemaExport(_cfg);
      tool.setDelimiter(";");
      tool.setOutputFile(outputFilePath);
      tool.create(true, true);
   }

   public void updateSchema()
   {
      SchemaUpdate tool = new SchemaUpdate(_cfg);
      tool.execute(true, true);
   }

   public static void main(String[] args)
   {
      Application app = new Application(true);
      HibernatePersistor p =
            (HibernatePersistor) app.getPersistenceMechanism();

      if (args.length != 2)
      {
         System.out.println("Usage: java HibernatePersistor {export|update} {outputfilepath}");
         return;
      }
      if ("export".equals(args[0]))
      {
         p.exportSchema(args[1]);
      }
      else if ("update".equals(args[0]))
      {
         p.updateSchema();
      }
   }


}