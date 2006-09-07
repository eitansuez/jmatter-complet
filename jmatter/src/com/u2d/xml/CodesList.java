package com.u2d.xml;

import com.u2d.list.PlainListEObject;
import com.u2d.app.PersistenceMechanism;
import com.u2d.app.AppFactory;
import com.u2d.app.HBMPersistenceMechanism;
import com.u2d.type.USState;
import com.u2d.type.Sex;
import com.u2d.type.MarritalStatus;
import com.u2d.type.composite.ContactMethod;
import com.u2d.json.JSON;
import com.u2d.model.AbstractListEO;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.io.InputStream;
import java.io.IOException;
import java.text.ParseException;

import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.hibernate.Transaction;
import org.hibernate.HibernateException;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.json.JSONObject;
import org.json.JSONException;

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
      List items = (resourceName.endsWith(".json")) ? unmarshalJSON(resourceName)
            : unmarshalXML(klass, resourceName);
      if (items == null)
      {
         // redo this design entirely
         return;
      }
      saveItems(klass, items);
   }

   public static List unmarshalJSON(String resourceName)
   {
      InputStream stream = getStreamForResource(resourceName);
      String data = null;
      try
      {
         data = JSON.readInputStream(stream);
         JSONObject jso = new JSONObject(data);
         AbstractListEO leo = JSON.fromJsonList(jso);
         return leo.getItems();
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (JSONException e)
      {
         e.printStackTrace();
      }
      catch (ClassNotFoundException e)
      {
         e.printStackTrace();
      }
      catch (ParseException e)
      {
         e.printStackTrace();
      }
      
      return null;
   }

   private static InputStream getStreamForResource(String resourceName)
   {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      return loader.getResourceAsStream(resourceName);
   }

   public static List unmarshalXML(Class klass, String resourceName)
   {
      try
      {
         IBindingFactory _bfact;
         _bfact = BindingDirectory.getFactory(klass);
         IUnmarshallingContext uctx = _bfact.createUnmarshallingContext();

         InputStream stream = getStreamForResource(resourceName);
         CodesList codesList = (CodesList) uctx.unmarshalDocument(stream, null);

         return codesList.items();
      }
      catch (JiBXException ex)
      {
         System.err.println(ex);
         ex.printStackTrace();
      }
      return null;

   }  // end populateCodes()

   private static void saveItems(Class klass, List items)
   {
      PersistenceMechanism pmech = AppFactory.getInstance().getApp().getPersistenceMechanism();
      if (!(pmech instanceof HBMPersistenceMechanism))
         return;

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
         for (Iterator itr = items.iterator(); itr.hasNext(); )
            session.save(itr.next());
         tx.commit();
      }
      catch (HibernateException ex)
      {
         if (tx != null) tx.rollback();
         throw ex;
      }
   }


   public static void dumpItemsFor(Class klass) throws Exception
   {
      CodesList codesList = new CodesList();
      PersistenceMechanism pmech = AppFactory.getInstance().getApp().getPersistenceMechanism();
      codesList.setItems(pmech.list(klass));
      JibxBoiler boiler = new JibxBoiler(klass);
      boiler.marshal("dump.xml");
   }
}
