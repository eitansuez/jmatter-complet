package com.u2d.persist;

import com.u2d.app.PersistenceMechanism;
import com.u2d.model.ComplexEObject;
import com.u2d.model.AbstractListEO;
import com.u2d.model.ComplexType;
import com.u2d.list.PagedList;
import com.u2d.list.PlainListEObject;
import com.u2d.find.SimpleQuery;
import com.u2d.type.atom.Password;
import org.hibernate.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Sep 14, 2005
 * Time: 12:23:04 PM
 */
public class MultiSessionPersistor extends HibernatePersistor
{
   private static PersistenceMechanism _instance = null;
   private SessionFactory _sessionFactory;

   public static PersistenceMechanism getInstance()
   {
      if (_instance == null)
         _instance = new MultiSessionPersistor();
      return _instance;
   }

   private MultiSessionPersistor()
   {
      super();
   }


   public void init(Class[] classList)
   {
      for (int i=0; i<classList.length; i++)
      {
         try
         {
//            System.out.println("Adding class "+classList[i].getName()+" to hibernate configuration");
            _cfg.addClass(classList[i]);
         }
         catch (HibernateException ex)
         {
            System.err.println("HibernateException: "
                  + ex.getMessage());
            ex.printStackTrace();
         }
      }

      // automatically create database schema?
      // cfg.setProperty(Environment.HBM2DDL_AUTO, "create");

      // properties file has default name and is in classpath base so should 
      //  be picked up automatically
      _sessionFactory = _cfg.buildSessionFactory();
   }

   public ComplexEObject load(Class clazz, Long id)
   {
      Session session = _sessionFactory.openSession();
      Transaction tx = null;
      try
      {
         tx = session.beginTransaction();
         ComplexEObject ceo = (ComplexEObject) session.load(clazz, id);
         tx.commit();
         ceo.onLoad();
         return ceo;
      }
      catch (HibernateException ex)
      {
         if (tx != null) tx.rollback();
         throw ex;
      }
      finally
      {
         session.close();
      }
   }

   public ComplexEObject fetchSingle(Class clazz)
   {
      Session session = _sessionFactory.openSession();
      Transaction tx = null;
      try
      {
         tx = session.beginTransaction();
         Criteria criteria = session.createCriteria(clazz);
         java.util.List results = criteria.list();
         if (results.size() > 0)
            return (ComplexEObject) criteria.list().get(0);
         tx.commit();
      }
      catch (HibernateException ex)
      {
         if (tx != null) tx.rollback();
         throw ex;
      }
      finally
      {
         session.close();
      }
      return null;
   }

   public void save(ComplexEObject ceo)
   {
      Session session = _sessionFactory.openSession();
      Transaction tx = null;
      try
      {
         tx = session.beginTransaction();

         while (ceo.field() != null && ceo.field().isAggregate())
            ceo = ceo.parentObject();

         if (ceo.isTransientState())
            ceo.onBeforeCreate();

         session.saveOrUpdate(ceo);

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
      finally
      {
         session.close();
      }
   }

   // TODO:  must set up persistor as some kind of lifecycle listener
   // somehow.  it must propagate onLoad() events to complexeobjects.
   // example:  if i request to load obj A and obj A has a simple 
   // 1-1 association to obj B, then obj B's state is not set to 
   // readState because it never received the onLoad() message.
   // A did, but not B.  the difference is that A was requested explicitly,
   // while B was retrieved by virtue of being associated to A.

   public void updateAssociation(ComplexEObject one, ComplexEObject two)
   {
      Session session = _sessionFactory.openSession();
      Transaction tx = null;
      try
      {
         tx = session.beginTransaction();

         while (one.field() != null && one.field().isAggregate())
            one = one.parentObject();

         session.saveOrUpdate(one);
         session.saveOrUpdate(two);

         tx.commit();

         one.onSave();
         two.onSave();
      }
      catch (HibernateException ex)
      {
         if (tx != null) tx.rollback();
         throw ex;
      }
      finally
      {
         session.close();
      }
   }

   public void delete(ComplexEObject ceo)
   {
      Session session = _sessionFactory.openSession();
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
      finally
      {
         session.close();
      }
   }

   public AbstractListEO browse(Class clazz)
   {
      return new PagedList(new SimpleQuery(ComplexType.forClass(clazz)));
   }

   public PlainListEObject list(Class clazz)
   {
      Session session = _sessionFactory.openSession();
      Transaction tx = null;
      try
      {
         tx = session.beginTransaction();

         Criteria criteria = session.createCriteria(clazz);
         java.util.List items = criteria.list();
         tx.commit();

         for (int i=0; i<items.size(); i++)
         {
            ((ComplexEObject) items.get(i)).onLoad();
         }
         return new PlainListEObject(clazz, items);
      }
      catch (HibernateException ex)
      {
         if (tx != null)
            tx.rollback();
         throw ex;
      }
      finally
      {
         session.close();
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
         Session session = _sessionFactory.openSession();
         try
         {
            Query query = session.createQuery("select user.password from com.u2d.app.User as user where user.username = :username");
            query.setString("username", username);
            Password hash = (Password) query.uniqueResult();
            if (hash == null) return false;  // no such user
            return Password.match(hash.hashValue(), password);
         }
         catch (HibernateException ex)
         {
            System.err.println("HibernateException: "+ex.getMessage());
            ex.printStackTrace();
            return false;  // TODO: throw an exception that translates into a truthful message to the end user
         }
         finally
         {
            session.close();
         }
      }
      catch (HibernateException ex)
      {
         ex.printStackTrace();
      }
      return false;
   }

   public com.u2d.type.Choice lookup(Class clazz, String code)
   {
      return null; // tbd
   }

   public Session getSession()
   {
      throw new RuntimeException("Not Yet Implemented");
   }

   public ComplexEObject fetch(String query)
   {
      throw new RuntimeException("Not Yet Implemented");
   }

   public AbstractListEO hql(String query)
   {
      throw new RuntimeException("Not Yet Implemented");
   }
}
