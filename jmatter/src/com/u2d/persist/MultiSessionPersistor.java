package com.u2d.persist;

import com.u2d.app.PersistenceMechanism;
import com.u2d.app.Tracing;
import com.u2d.model.ComplexEObject;
import com.u2d.model.AbstractListEO;
import com.u2d.model.ComplexType;
import com.u2d.list.PagedList;
import com.u2d.list.PlainListEObject;
import com.u2d.find.SimpleQuery;
import com.u2d.type.atom.Password;
import org.hibernate.*;
import org.hibernate.cfg.Environment;
import org.wings.session.SessionManager;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Sep 14, 2005
 * Time: 12:23:04 PM
 */
public class MultiSessionPersistor extends HibernatePersistor
{
   private SessionFactory _sessionFactory;

   public MultiSessionPersistor() { super(); }

   public void initialize()
   {
      super.initialize();
      
      _cfg.setProperty(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");

      // automatically create database schema?
      // cfg.setProperty(Environment.HBM2DDL_AUTO, "create");

      // properties file has default name and is in classpath base so should 
      //  be picked up automatically
      _sessionFactory = _cfg.buildSessionFactory();
   }

   public ComplexEObject load(Class clazz, Long id)
   {
      Session session = getSession();
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
   }

   public ComplexEObject fetchSingle(Class clazz)
   {
      Session session = getSession();
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
      return null;
   }

   public void save(ComplexEObject ceo)
   {
      Session session = getSession();
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
      Session session = getSession();
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
   }

   public void delete(ComplexEObject ceo)
   {
      Session session = getSession();
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

   public AbstractListEO browse(Class clazz)
   {
      return new PagedList(new SimpleQuery(ComplexType.forClass(clazz)));
   }

   public PlainListEObject list(Class clazz)
   {
      Session session = getSession();
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
   }
   public PlainListEObject list(ComplexType type)
   {
      return list(type.getJavaClass());
   }


   public boolean authenticate(String username, String password)
   {
      try
      {
         Session session = getSession();
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
      return _sessionFactory.getCurrentSession();
   }

   public ComplexEObject fetch(String hql)
   {
      ComplexEObject ceo = (ComplexEObject) getSession().createQuery(hql).iterate().next();
      if (ceo == null) return null;
      ceo.onLoad();
      return ceo;
   }

   public AbstractListEO hql(String hql)
   {
      return hqlQuery(getSession().createQuery(hql));
   }
   public AbstractListEO hqlQuery(Query query)
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

   public void newSession() { /* noop */ }
   
}
