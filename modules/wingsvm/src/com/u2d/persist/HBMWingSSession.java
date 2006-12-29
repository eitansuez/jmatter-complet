package com.u2d.persist;

import org.hibernate.*;
import org.hibernate.Session;
import org.wings.session.*;
import java.util.*;

/**
 * A Persistence Mechanism designed to work together with
 * the WingS-based view mechanism
 * 
 * @author Eitan Suez
 */
public class HBMWingSSession extends HibernatePersistor
{
   private Map<org.wings.session.Session, Session> sessions = 
         new HashMap<org.wings.session.Session, Session>();

   public HBMWingSSession() {}


   public void initialize()
   {
      super.initialize();
      _sessionFactory = _cfg.buildSessionFactory();
   }

   public Session getSession()
   {
      org.wings.session.Session wingsSession = SessionManager.getSession();
      Session hbmSession = (Session) sessions.get(wingsSession);
      if (hbmSession == null)
      {
         hbmSession = _sessionFactory.openSession();
         sessions.put(wingsSession, hbmSession);
      }
      return hbmSession;
   }

   // simply discard the session from hashmap..new one obtained automatically
   public void newSession()
   {
      org.wings.session.Session key = SessionManager.getSession();
      Session session = (Session) sessions.get(key);
      if (session != null)
      {
         try
         {
            session.close();
            sessions.remove(key);
         }
         catch (HibernateException ex)
         {
            System.err.println("Error attempting to close hbm session..");
            System.err.println("Hibernate Exception: "+ex.getMessage());
            ex.printStackTrace();
         }
      }
   }

}
