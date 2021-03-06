package com.u2d.persist;

import org.hibernate.Session;
import org.hibernate.HibernateException;
import java.util.Map;
import java.util.HashMap;
import com.u2d.app.EchoEntryPoint;
import nextapp.echo.webcontainer.Connection;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Dec 28, 2006
 * Time: 2:07:20 PM
 */
public class HBMEchoSession extends HibernatePersistor
{
   public HBMEchoSession() {}
   
   private Map<Connection, Session> sessions = 
         new HashMap<Connection, Session>();
   
   public void initialize()
   {
      super.initialize();
      _sessionFactory = _cfg.buildSessionFactory();
   }
   
   public synchronized Session getSession()
   {
      Connection connection = activeConnection();
      Session hbmSession = sessions.get(connection);
      if (hbmSession == null)
      {
         hbmSession = _sessionFactory.openSession();
         sessions.put(connection, hbmSession);
      }
      return hbmSession;
   }
   
   private Connection activeConnection()
   {
      return EchoEntryPoint.getActiveConnection();
   }

   // simply discard the session from hashmap..new one obtained automatically
   public void newSession()
   {
      Connection key = activeConnection();
      Session session = sessions.get(key);
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
