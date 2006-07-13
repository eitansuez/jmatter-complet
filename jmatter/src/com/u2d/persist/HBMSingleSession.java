/*
 * Created on Mar 10, 2004
 */
package com.u2d.persist;

import com.u2d.app.*;
import com.u2d.list.PagedList;
import com.u2d.list.PlainListEObject;
import com.u2d.model.AbstractListEO;
import com.u2d.model.ComplexEObject;
import com.u2d.model.ComplexType;
import com.u2d.type.Choice;
import com.u2d.type.atom.Password;
import com.u2d.find.SimpleQuery;
import com.u2d.element.Field;
import org.hibernate.*;
import org.hibernate.criterion.Expression;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author Eitan Suez
 */
public class HBMSingleSession extends HibernatePersistor
{
   private static PersistenceMechanism _instance = null;
   private SessionFactory _sessionFactory;
   private Session _session;

   private static transient Logger _tracer = Tracing.tracer();

   public static PersistenceMechanism getInstance()
   {
      if (_instance == null) _instance = new HBMSingleSession();
      return _instance;
   }

   private HBMSingleSession()
   {
      super();
   }

   public void init(Class[] classList)
   {
      for (int i=0; i<classList.length; i++)
      {
         try
         {
            _tracer.fine("Adding class "+classList[i].getName()+" to hibernate configuration");
            _cfg.addClass(classList[i]);
         }
         catch (HibernateException ex)
         {
            System.err.println("HibernateException: "
                  + ex.getMessage());
            ex.printStackTrace();
         }
      }

      _cfg.addClass(ComplexType.class);

      // automatically create database schema?
      // cfg.setProperty(Environment.HBM2DDL_AUTO, "create");

      // properties file has default name and is in classpath base so should be picked up automatically
      Date start = new Date();
      _sessionFactory = _cfg.buildSessionFactory();
      _session = _sessionFactory.openSession();
      long time = new Date().getTime() - start.getTime();
      _tracer.info("time to build session factory and open session: "+
            time + "(ms)");


      // problem with this:  two listeners for onlogout
      // doing things at the same time:  application tries
      // to save user desktop and hbmsinglesession tries
      // to create a new session.  however, these must happen
      // sequentially, not in parallel (save desktop, then newSession())
      // postgresql v8 correctly catches this error and throws an exception
      // (see AppFrame.java)

//      AppFactory.getInstance().getApp().
//      addAppEventListener("LOGOUT", new AppEventListener()
//      {
//         public void onEvent(com.u2d.pubsub.AppEvent evt)
//         {
//            newSession();
//         }
//      });
   }

   public Session getSession() { return _session; }

   public ComplexEObject load(Class clazz, Long id)
   {
      try
      {
         ComplexEObject ceo = (ComplexEObject) _session.load(clazz, id);
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

   public ComplexEObject fetch(String hql)
   {
      try
      {
         ComplexEObject ceo = (ComplexEObject) _session.createQuery(hql).iterate().next();
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

   public AbstractListEO hql(String hql)
   {
      return hqlQuery(_session.createQuery(hql));
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

   public ComplexEObject fetchSingle(Class clazz)
   {
      try
      {
         Criteria criteria = _session.createCriteria(clazz);
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
         try
         {
            tx = _session.beginTransaction();

            ComplexEObject parent = selfOrParentIfAggregate(ceo);

            if (ceo.isTransientState()) ceo.onBeforeCreate();

            _session.saveOrUpdate(parent);

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

   private ComplexEObject selfOrParentIfAggregate(ComplexEObject ceo)
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
   
   public void saveMany(java.util.Set ceos)
   {
      try
      {
         Transaction tx = null;
         try
         {
            tx = _session.beginTransaction();

            Iterator itr = ceos.iterator();
            ComplexEObject ceo = null;
            while (itr.hasNext())
            {
               ceo = (ComplexEObject) itr.next();
               if (ceo.isTransientState()) ceo.onBeforeCreate();
               ceo.onBeforeSave();
               _session.save(ceo);
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

   public void updateAssociation(ComplexEObject one, ComplexEObject two)
   {
      _tracer.fine("Updating association between " + one + " and " + two);
      try
      {
         Transaction tx = null;
         try
         {
            tx = _session.beginTransaction();

            while (one.field() != null && one.field().isAggregate())
               one = one.parentObject();

            // terrible hack:  Field types map to db as value types, not an entity
            if (!Field.class.isAssignableFrom(one.getClass()))
            {
               _session.save(one);
            }
            if (!Field.class.isAssignableFrom(two.getClass()))
            {
               _session.save(two);
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

   public void delete(ComplexEObject ceo)
   {
      try
      {
         Transaction tx = null;
         try
         {
            tx = _session.beginTransaction();
            _session.delete(ceo);
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

   public AbstractListEO browse(Class clazz)
   {
      return new PagedList(new SimpleQuery(ComplexType.forClass(clazz)));
   }

   public PlainListEObject list(Class clazz)
   {
      try
      {
         Criteria criteria = _session.createCriteria(clazz);
         java.util.List items = criteria.list();

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
      return list(type.getJavaClass());
   }

   public boolean authenticate(String username, String password)
   {
      try
      {
         String queryString = "select user.password from com.u2d.app.User as user "
               + " where user.username = :username";
         Query query = _session.createQuery(queryString);
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
         Criteria criteria = _session.createCriteria(clazz).add(
               Expression.eq("code", code));
         java.util.List items = criteria.list();
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

   public void newSession()
   {

      if (_session != null)
      {
         try
         {
            _session.close();
            _session = null;
         }
         catch (HibernateException ex)
         {
            System.err.println("Error attempting to close hbm session..");
            System.err.println("Hibernate Exception: "+ex.getMessage());
            ex.printStackTrace();
         }
      }

      try
      {
         _session = _sessionFactory.openSession();
         _tracer.info("Got a new hbm session");
      }
      catch (HibernateException ex)
      {
         ex.printStackTrace();
         System.err.println("Error attempting to initialize hibernate");
         throw ex;
      }
   }

}
