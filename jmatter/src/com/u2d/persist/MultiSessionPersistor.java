package com.u2d.persist;

import com.u2d.model.ComplexEObject;
import org.hibernate.*;
import org.hibernate.cfg.Environment;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Sep 14, 2005
 * Time: 12:23:04 PM
 */
public class MultiSessionPersistor extends HibernatePersistor
{
   public MultiSessionPersistor() { super(); }

   public void initialize()
   {
      super.initialize();
      _cfg.setProperty(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
      _sessionFactory = _cfg.buildSessionFactory();
   }

   public Session getSession()
   {
      return _sessionFactory.getCurrentSession();
   }

   public void newSession() { /* noop */ }
   
   
   
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

}
