package com.u2d.xml;

import com.u2d.list.PlainListEObject;
import com.u2d.app.PersistenceMechanism;
import com.u2d.app.AppFactory;
import com.u2d.app.HBMPersistenceMechanism;
import com.u2d.type.USState;
import com.u2d.type.Sex;
import com.u2d.type.MarritalStatus;
import com.u2d.type.composite.ContactMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.io.InputStream;

import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.hibernate.Transaction;
import org.hibernate.HibernateException;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Feb 22, 2006
 * Time: 11:02:15 AM
 */
public class CodesList
{
   private final ArrayList _items = new ArrayList();

   public CodesList() {}

   public List items() { return _items; }
   public void setItems(PlainListEObject items)
   {
      _items.addAll(items.getItems());
   }

   public String toString()
   {
      return "Items: " + _items.size();
   }

   public static void main(String[] args)
   {
//      populateItemsFor();
   }
   
   public static void populateCodes(List classList)
   {
      if (classList.contains(USState.class))
      {
         CodesList.populateItemsFor(USState.class, "usstates.xml");
      }
      if (classList.contains(ContactMethod.class))
      {
         CodesList.populateItemsFor(ContactMethod.class,  "contactmethods.xml");
      }
      if (classList.contains(Sex.class))
      {
         CodesList.populateItemsFor(Sex.class,  "sexes.xml");
      }
      if (classList.contains(MarritalStatus.class))
      {
         CodesList.populateItemsFor(MarritalStatus.class,  "marritalstati.xml");
      }
   }

   public static void populateItemsFor(Class klass, String resourceName)
   {
      try
      {
         IBindingFactory _bfact;
         _bfact = BindingDirectory.getFactory(klass);
         IUnmarshallingContext uctx = _bfact.createUnmarshallingContext();

         ClassLoader loader = Thread.currentThread().getContextClassLoader();
         InputStream stream = loader.getResourceAsStream(resourceName);
         CodesList codesList = (CodesList) uctx.unmarshalDocument(stream, null);

         PersistenceMechanism pmech = AppFactory.getInstance().getApp().getPersistenceMechanism();
         if (pmech instanceof HBMPersistenceMechanism)
         {
            HBMPersistenceMechanism hbm = (HBMPersistenceMechanism) pmech;
            org.hibernate.Session session = hbm.getSession();

            Criteria criteria = session.createCriteria(klass);
            Criteria countCriteria = criteria.setProjection(Projections.rowCount());
            int count = ((Integer) countCriteria.uniqueResult()).intValue();

            if (count > 0)
               return;

            Transaction tx = null;
            try
            {
               tx = session.beginTransaction();
               for (Iterator itr = codesList.items().iterator(); itr.hasNext(); )
                  session.save(itr.next());
               tx.commit();
            }
            catch (HibernateException ex)
            {
               if (tx != null) tx.rollback();
               throw ex;
            }
         }  // end if
      }
      catch (JiBXException ex)
      {
         System.err.println(ex);
         ex.printStackTrace();
      }

   }  // end populateCodes()


   public static void dumpItemsFor(Class klass) throws Exception
   {
      CodesList codesList = new CodesList();
      PersistenceMechanism pmech = AppFactory.getInstance().getApp().getPersistenceMechanism();
      codesList.setItems(pmech.list(klass));
      JibxBoiler boiler = new JibxBoiler(klass);
      boiler.marshal("dump.xml");
   }
}
