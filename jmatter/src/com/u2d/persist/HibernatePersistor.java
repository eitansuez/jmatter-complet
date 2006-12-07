/*
 * Created on Mar 10, 2004
 */
package com.u2d.persist;

import com.u2d.app.*;
import com.u2d.model.ComplexEObject;
import com.u2d.model.ComplexType;
import com.u2d.model.AbstractListEO;
import com.u2d.list.PlainListEObject;
import com.u2d.element.Field;
import org.hibernate.cfg.*;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.hibernate.Transaction;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Iterator;
import java.util.Set;
import java.io.File;


/**
 * @author Eitan Suez
 */
public abstract class HibernatePersistor implements HBMPersistenceMechanism
{
   protected Configuration _cfg;
   protected transient Logger _tracer = Tracing.tracer();
   protected Set<Class> _classes;

   public HibernatePersistor() {}
   
   public Set<Class> getClasses() { return _classes; }
   public void setClasses(Set<Class> classes) { _classes = classes; }

   public void initialize()
   {
      // reduce verboseness of hibernate logger..
      Logger.getLogger("org.hibernate").setLevel(Level.WARNING);
      _cfg = new Configuration();
      
      for (Class cls : _classes)
      {
         _tracer.fine("Adding class "+cls.getName()+" to hibernate configuration");
         _cfg.addClass(cls);
      }
      _cfg.addClass(ComplexType.class);
   }
   
   public void saveMany(java.util.Set ceos)
   {
      try
      {
         Transaction tx = null;
         try
         {
            tx = getSession().beginTransaction();

            Iterator itr = ceos.iterator();
            ComplexEObject ceo = null;
            while (itr.hasNext())
            {
               ceo = (ComplexEObject) itr.next();
               if (ceo.isTransientState()) ceo.onBeforeCreate();
               ceo.onBeforeSave();
               getSession().save(ceo);
            }
            tx.commit();

            itr = ceos.iterator();
            ceo = null;
            while (itr.hasNext())
            {
               ceo = (ComplexEObject) itr.next();
               if (ceo.isTransientState()) ceo.onCreate();
               else ceo.onSave();
            }
         }
         catch (HibernateException ex)
         {
            if (tx != null) tx.rollback();
            throw ex;
         }
      }
      catch (HibernateException ex)
      {
         ex.printStackTrace();
         newSession();
         throw ex;
      }
   }
   
   public abstract void newSession();

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
      ApplicationContext context = new ClassPathXmlApplicationContext("persistorContext.xml");
      HibernatePersistor persistor = (HibernatePersistor) context.getBean("persistor");

      if (args.length != 2)
      {
         System.out.println("Usage: java HibernatePersistor {export|update} {outputfilepath}");
         return;
      }
      if ("export".equals(args[0]))
      {
         persistor.exportSchema(args[1]);
      }
      else if ("update".equals(args[0]))
      {
         persistor.updateSchema();
      }
   }


   public AbstractListEO hqlQuery(Query query)
   {
      try
      {
         java.util.List results = query.list();

         if (results.isEmpty())
         {
            return null;
         }
         else
         {
            for (int i = 0; i < results.size(); i++)
            {
               ((ComplexEObject) results.get(i)).onLoad();
            }

            /* this is not correct all the time.
              the right way to do this is to use the hibernate
              hql parser and ask it for the cls
            */
            Class cls = ((ComplexEObject) results.get(0)).type().getJavaClass();
            return new PlainListEObject(cls, results);
         }
      }
      catch (HibernateException ex)
      {
         System.err.println("hbm query failed: " + ex.getMessage());
         System.err.println("Query was: " + query.getQueryString());
         newSession();
         ex.printStackTrace();
      }
      return null;
   }


   public void refresh(ComplexEObject eo)
   {
      getSession().refresh(selfOrParentIfAggregate(eo));
   }

   protected ComplexEObject selfOrParentIfAggregate(ComplexEObject ceo)
   {
      ComplexEObject parent = ceo;
      Field field = parent.field();
      while ( (field != null) && 
              ( field.isAggregate() || 
                (field.isIndexed() && field.isComposite()) )
            )
      {
         parent = parent.parentObject();
         field = parent.field();
      }
      return parent;
   }
   
}