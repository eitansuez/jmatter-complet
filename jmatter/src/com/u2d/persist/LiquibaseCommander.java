package com.u2d.persist;

import liquibase.database.Database;
import liquibase.database.HibernateDatabase;
import liquibase.exception.JDBCException;
import liquibase.exception.LiquibaseException;
import liquibase.commandline.CommandLineUtils;
import liquibase.FileSystemFileOpener;
import liquibase.Liquibase;
import org.hibernate.cfg.Configuration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.File;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Properties;
import java.util.Set;
import com.u2d.model.ComplexType;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Aug 7, 2008
 * Time: 10:17:03 PM
 */
public class LiquibaseCommander
{
   public static String CHANGELOG_FILENAME = "db_changelog.xml";
   private String basedir;
   private String changelogFullpath;
   private void setTargetDir(String path)
   {
      basedir = path;
      if (!path.endsWith(File.separator))
         path += File.separator;
      changelogFullpath = path + CHANGELOG_FILENAME;
   }

   private ClassLoader loader = Thread.currentThread().getContextClassLoader();

   private Properties properties;
   private Configuration hibernateConfig;

   private Database database;
   private HibernateDatabase hibernateDatabase;

   protected Set<Class> _classes;
   public void setClasses(Set<Class> classes) { _classes = classes; }

   public void doDiffToChangeLog() throws JDBCException, IOException, ParserConfigurationException
   {
      buildHibernateConfig();
      CommandLineUtils.doDiffToChangeLog(changelogFullpath, hibernateDatabase, database);
   }

   public void doGenerateChangelog() throws IOException, JDBCException, ParserConfigurationException
   {
      loadPropertiesAndCreateDbObject();
      CommandLineUtils.doGenerateChangeLog(changelogFullpath, database, null, null, null, null, null);
   }
   
   public void doUpdate(boolean todb) throws LiquibaseException
   {
      loadPropertiesAndCreateDbObject();

      FileSystemFileOpener fileOpener = new FileSystemFileOpener(basedir);
      Liquibase liquibase = new Liquibase(changelogFullpath, fileOpener, database);
      if (todb)
      {
         liquibase.update(null);
      }
      else
      {
         liquibase.update(null, new OutputStreamWriter(System.out));
      }
   }

   
   private void buildHibernateConfig()
   {
      hibernateConfig = new Configuration();
      loadPropertiesAndCreateDbObject();
      hibernateConfig.setProperties(properties);
      for (Class cls : _classes)
      {
         String resource = cls.getName().replace( '.', '/' ) + ".hbm.xml";
         hibernateConfig.addResource(resource, loader);
      }
      hibernateConfig.addClass(ComplexType.class);

      hibernateDatabase = new HibernateDatabase() {
          public Configuration getConfiguration() { return hibernateConfig; }
      };
   }
   
   private void loadPropertiesAndCreateDbObject()
   {
      try
      {
         properties = new Properties();
         properties.load(loader.getResourceAsStream("hibernate.properties"));
         String url = properties.getProperty("hibernate.connection.url");
         String username = properties.getProperty("hibernate.connection.username");
         String password = properties.getProperty("hibernate.connection.password");
         String driver = properties.getProperty("hibernate.connection.driver_class");

         database = CommandLineUtils.createDatabaseObject(loader, url, username, password, driver, null, null);
      }
      catch (IOException ioEx)
      {
         ioEx.printStackTrace();
         throw new RuntimeException("Failed to load hibernate.properties configuration");
      }
      catch (JDBCException jdbcEx)
      {
         jdbcEx.printStackTrace();
         throw new RuntimeException("Failed to construct database object for connection parameters");
      }

   }


   public static void main(String[] args) throws IOException, LiquibaseException, ParserConfigurationException
   {
      Logger.getLogger("org.springframework").setLevel(Level.WARNING);
      ApplicationContext context = new ClassPathXmlApplicationContext("persistorContext.xml");
      LiquibaseCommander liquibaseCommander = (LiquibaseCommander) context.getBean("liquibase-commander");

      if (args.length != 2)
      {
         System.out.println("Usage: java LiquibaseCommander {diffchangelog|update|updateSQL|generate_changelog} outputdirectory");
         return;
      }
      liquibaseCommander.setTargetDir(args[1]);
      String cmd = args[0];
      if ("diffchangelog".equals(cmd))
      {
         liquibaseCommander.doDiffToChangeLog();
      }
      else if ("update".equals(cmd))
      {
         liquibaseCommander.doUpdate(true);
      }
      else if ("updateSQL".equals(cmd))
      {
         liquibaseCommander.doUpdate(false);
      }
      else if ("generate_changelog".equals(cmd))
      {
         liquibaseCommander.doGenerateChangelog();
      }
   }

}
