/*
 * Created on Mar 10, 2004
 */
package com.u2d.persist;

import org.hibernate.*;

/**
 * @author Eitan Suez
 */
public class HBMSingleSession extends HibernatePersistor
{
   private Session _session;

   public HBMSingleSession() { super(); }

   public void initialize()
   {
      super.initialize();
      _sessionFactory = _cfg.buildSessionFactory();
      _session = _sessionFactory.openSession();
   }

   public Session getSession() { return _session; }

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
         System.err.println("Error attempting to obtain a new hibernate sessino");
         throw ex;
      }
   }

}
