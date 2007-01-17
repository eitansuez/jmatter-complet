/*
 * Created on Mar 10, 2004
 */
package com.u2d.persist;

import com.u2d.app.*;
import com.u2d.model.ComplexEObject;
import com.u2d.model.ComplexType;
import com.u2d.model.AbstractListEO;
import com.u2d.list.PlainListEObject;
import com.u2d.list.PagedList;
import com.u2d.element.Field;
import com.u2d.find.SimpleQuery;
import com.u2d.type.atom.Password;
import com.u2d.type.Choice;
import org.hibernate.cfg.*;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.hibernate.*;
import org.hibernate.criterion.Expression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Iterator;
import java.util.Set;
import java.util.List;
import java.io.File;


/**
 * @author Eitan Suez
 */
public abstract class HibernatePersistor implements HBMPersistenceMechanism
{
   protected Configuration _cfg;
   protected transient Logger _tracer = Tracing.tracer();
   protected Set<Class> _classes;
   protected SessionFactory _sessionFactory;

   public HibernatePersistor() {}
   
   public abstract Session getSession();
   public abstract void newSession();

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
            Session session = getSession();
            tx = session.beginTransaction();

            for (Iterator itr = ceos.iterator(); itr.hasNext(); )
            {
               ComplexEObject ceo = (ComplexEObject) itr.next();
               if (ceo.isTransientState()) ceo.onBeforeCreate();
               ceo.onBeforeSave();
               session.save(ceo);
            }
            tx.commit();

            for (Iterator itr = ceos.iterator(); itr.hasNext(); )
            {
               ComplexEObject ceo = (ComplexEObject) itr.next();
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
   
   public void deleteMany(java.util.Set ceos)
   {
      try
      {
         Transaction tx = null;
         try
         {
            Session session = getSession();
            tx = session.beginTransaction();

            for (Iterator itr = ceos.iterator(); itr.hasNext(); )
            {
               ComplexEObject ceo = (ComplexEObject) itr.next();
               session.delete(ceo);
            }
            tx.commit();

            for (Iterator itr = ceos.iterator(); itr.hasNext(); )
            {
               ComplexEObject ceo = (ComplexEObject) itr.next();
               ceo.onDelete();
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

   private String outputFilePath(String path)
   {
      if (!path.endsWith(File.separator))
         path += File.separator;
      return path + "schema.sql";
   }

   public AbstractListEO hql(String hql)
   {
      return hqlQuery(getSession().createQuery(hql));
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

   public ComplexEObject fetch(String hql)
   {
      try
      {
         ComplexEObject ceo = (ComplexEObject) getSession().createQuery(hql).uniqueResult();
         if (ceo == null) return null;
         ceo.onLoad();
         return ceo;
      }
      catch (HibernateException ex)
      {
         System.err.println("hbm query failed: " + ex.getMessage());
         System.err.println("Query was: " + hql);
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
   
   public AbstractListEO browse(Class clazz)
   {
      return new PagedList(new SimpleQuery(ComplexType.forClass(clazz)));
   }

   public PlainListEObject list(Class clazz)
   {
      try
      {
         Criteria criteria = getSession().createCriteria(clazz);
         List items = criteria.list();

         for (int i = 0; i < items.size(); i++)
         {
            ((ComplexEObject) items.get(i)).onLoad();
         }
         return new PlainListEObject(clazz, items);
      }
      catch (HibernateException ex)
      {
         ex.printStackTrace();
         newSession();
         throw ex;
      }
   }

   public PlainListEObject list(ComplexType type)
   {
      PlainListEObject list = list(type.getJavaClass());
      list.resolveType();
      return list;
   }

   public boolean authenticate(String username, String password)
   {
      try
      {
         Session session = getSession();
         String queryString = "select user.password from com.u2d.app.User as user " + 
               " where user.username = :username";
         Query query = session.createQuery(queryString);
         query.setString("username", username);
         Password hash = (Password) query.uniqueResult();
         if (hash == null) return false; // no such user
         return Password.match(hash.hashValue(), password);
      }
      catch (HibernateException ex)
      {
         System.err.println("HibernateException: " + ex.getMessage());
         ex.printStackTrace();
         newSession();
         return false; // TODO: throw an exception that translates into a
                       // truthful message to the end user
      }
   }

   public com.u2d.type.Choice lookup(Class clazz, String code)
   {
      try
      {
         Criteria criteria = getSession().createCriteria(clazz).add(
               Expression.eq("code", code));
         List items = criteria.list();
         if (items.isEmpty()) return null;
         ComplexEObject ceo = (ComplexEObject) items.iterator().next();
         ceo.onLoad();
         return (Choice) ceo;
      }
      catch (HibernateException ex)
      {
         ex.printStackTrace();
         newSession();
         throw ex;
      }
   }
   
   public void delete(ComplexEObject ceo)
   {
      Session session = getSession();
      try
      {
         Transaction tx = null;
         try
         {
            tx = session.beginTransaction();
            session.delete(ceo);
            tx.commit();

            ceo.onDelete();
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

   public void updateAssociation(ComplexEObject one, ComplexEObject two)
   {
      _tracer.fine("Updating association between " + one + " and " + two);
      Session session = getSession();
      try
      {
         Transaction tx = null;
         try
         {
            tx = session.beginTransaction();

            while (one.field() != null && one.field().isAggregate())
               one = one.parentObject();

            // terrible hack:  Field types map to db as value types, not an entity
            if (!Field.class.isAssignableFrom(one.getClass()))
            {
               session.save(one);
            }
            if (!Field.class.isAssignableFrom(two.getClass()))
            {
               session.save(two);
            }

            tx.commit();

            one.onSave();
            two.onSave();
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

   public ComplexEObject fetchSingle(Class clazz)
   {
      try
      {
         Criteria criteria = getSession().createCriteria(clazz);
         List list = criteria.list();

         if (list.isEmpty())
            return null;

         ComplexEObject ceo = (ComplexEObject) list.get(0);
         ceo.onLoad();
         return ceo;
      }
      catch (HibernateException ex)
      {
         System.err.println("failed to fetch singleton for class: " + clazz.getName());
         ex.printStackTrace();
         throw ex;
      }
   }

   public ComplexEObject load(Class clazz, Long id)
   {
      try
      {
         ComplexEObject ceo = (ComplexEObject) getSession().load(clazz, id);
         ceo.onLoad();
         return ceo;
      }
      catch (HibernateException ex)
      {
         System.err.println("failed to load "+clazz.getName()+" instance with id: "+id);
         ex.printStackTrace();
         newSession();
         throw ex;
      }
   }

   public void save(ComplexEObject ceo)
   {
      // this is not necessarily the right course of action:
      //  problem statement:  when saving something, if it has an association
      //  to something transient, the save will fail.  one solution is to 
      //  cascade the save, which is do here:
//      if (ceo.isTransientState())
//      {
//         Set set = new HashSet();
//         for (int i=0; i<ceo.childFields().size(); i++)
//         {
//            Field field = (Field) ceo.childFields().get(i);
//            if (field.isAssociation())
//            {
//               ComplexEObject assocValue = (ComplexEObject) field.get(ceo);
//               if (assocValue.isTransientState())
//               {
//                  set.add(field.get(ceo));
//               }
//            }
//         }
//         if (set.size() > 0)
//         {
//            set.add(selfOrParentIfAggregate(ceo));
//            saveMany(set);
//            return;
//         }
//      }
      // the other way to go is to through a validation exception and let the user
      // decide.  this might be cleaner.
      if (ceo.isTransientState())
      {
         for (int i=0; i<ceo.childFields().size(); i++)
         {
            Field field = (Field) ceo.childFields().get(i);
            if (field.isAssociation())
            {
               ComplexEObject assocValue = (ComplexEObject) field.get(ceo);
               if (assocValue.isTransientState())
               {
                  String msg = "Must first save associated object " + assocValue.toString() +
                        " before saving " + ceo;
                  ceo.fireValidationException(msg);
                  return;
               }
            }
         }
      }

      _tracer.info("Saving " + ceo.type() + ": " + ceo);
      ceo.onBeforeSave();

      try
      {
         Transaction tx = null;
         Session session = getSession();
         try
         {
            tx = session.beginTransaction();

            ComplexEObject parent = selfOrParentIfAggregate(ceo);

            if (ceo.isTransientState()) ceo.onBeforeCreate();

            session.saveOrUpdate(parent);

            tx.commit();

            if (ceo.isTransientState())
               ceo.onCreate();
            else
               ceo.onSave();

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

   // == tool-related ==

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


   
}