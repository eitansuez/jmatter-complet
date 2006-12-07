package com.u2d.persist;

import com.u2d.model.ComplexType;
import com.u2d.model.ComplexEObject;
import com.u2d.model.AbstractListEO;
import com.u2d.list.PlainListEObject;
import com.u2d.list.PagedList;
import com.u2d.element.Field;
import com.u2d.find.SimpleQuery;
import com.u2d.type.atom.Password;
import com.u2d.type.Choice;
import org.hibernate.*;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
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
   private SessionFactory _sessionFactory;
   private Map<org.wings.session.Session, Session> sessions = 
         new HashMap<org.wings.session.Session, Session>();

   public HBMWingSSession() {}

   public void initialize()
   {
      super.initialize();
      
      // automatically create database schema if necessary..
      //_cfg.setProperty(Environment.HBM2DDL_AUTO, "update");

      _sessionFactory = _cfg.buildSessionFactory();

//      SessionManager.getSession().addRequestListener(new SRequestListener()
//      {
//         public void processRequest(SRequestEvent e)
//         {
//            if (e.getType() == SRequestEvent.REQUEST_END)
//            {
//               getSession().disconnect();
//            }
//            // not necessary to reconnect connection explicitly:
//            //   automatic in hibernate v3
//         }
//      });
   }

   public Session getSession()
   {
      org.wings.session.Session wingsSession =  SessionManager.getSession();
      Session hbmSession = (Session) sessions.get(wingsSession);
      if (hbmSession == null)
      {
         hbmSession = _sessionFactory.openSession();
         sessions.put(wingsSession, hbmSession);
      }
      return hbmSession;
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

   public ComplexEObject fetch(String hql)
   {
      try
      {
         ComplexEObject ceo = (ComplexEObject) getSession().createQuery(hql).iterate().next();
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
            tx = getSession().beginTransaction();

            ComplexEObject parent = selfOrParentIfAggregate(ceo);

            if (ceo.isTransientState()) ceo.onBeforeCreate();

            getSession().saveOrUpdate(parent);

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

   public void updateAssociation(ComplexEObject one, ComplexEObject two)
   {
      _tracer.fine("Updating association between " + one + " and " + two);
      try
      {
         Transaction tx = null;
         try
         {
            tx = getSession().beginTransaction();

            while (one.field() != null && one.field().isAggregate())
               one = one.parentObject();

            // terrible hack:  Field types map to db as value types, not an entity
            if (!Field.class.isAssignableFrom(one.getClass()))
            {
               getSession().save(one);
            }
            if (!Field.class.isAssignableFrom(two.getClass()))
            {
               getSession().save(two);
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
            tx = getSession().beginTransaction();
            getSession().delete(ceo);
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
      return list(type.getJavaClass());
   }

   public boolean authenticate(String username, String password)
   {
      try
      {
         String queryString = "select user.password from com.u2d.app.User as user "
               + " where user.username = :username";
         Query query = getSession().createQuery(queryString);
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
