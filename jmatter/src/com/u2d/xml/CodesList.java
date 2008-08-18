package com.u2d.xml;

import com.u2d.list.PlainListEObject;
import com.u2d.app.PersistenceMechanism;
import com.u2d.app.HBMPersistenceMechanism;
import com.u2d.app.Context;
import com.u2d.json.JSON;
import com.u2d.model.AbstractListEO;
import com.u2d.persist.HBMBlock;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Set;
import java.io.InputStream;
import java.io.IOException;
import java.text.ParseException;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.hibernate.Criteria;
import org.hibernate.Session;
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

   public static void populateCodes(PersistenceMechanism pmech, Set<Class> classList)
   {
      for (Iterator iter = classList.iterator(); iter.hasNext();) {
		Class clazz = (Class) iter.next();
		String resourceNameSingular = clazz.getSimpleName().toLowerCase();

		//Singular->Plural
		String resourceNamePlural;
		// category->categories 
		if (resourceNameSingular.endsWith("y")) {
			resourceNamePlural = resourceNameSingular.substring(0, resourceNameSingular.length() -1) + "ies"; 
		} else if (resourceNameSingular.endsWith("status")) {
			resourceNamePlural = resourceNameSingular.substring(0, resourceNameSingular.length() -6) + "stati"; 
		//sex->sexes
		} else if (resourceNameSingular.endsWith("x")) {
			resourceNamePlural = resourceNameSingular + "es";
		//usstate -> usstates
		} else {
			resourceNamePlural = resourceNameSingular + "s";
		}

		String resourceName = resourceNamePlural + ".xml";
		//System.out.println("************ Finding resource: " + resourceName);
		if ( getStreamForResource(resourceName) != null) {
			//System.out.println("************ Resource found!");
			populateCodesFor(pmech, classList, clazz, resourceName);
		}
	}
//  	populateCodesFor(pmech, classList, USState.class, "usstates.xml");
//      populateCodesFor(pmech, classList, ContactMethod.class, "contactmethods.xml");
//      populateCodesFor(pmech, classList, Sex.class, "sexes.xml");
//      populateCodesFor(pmech, classList, MarritalStatus.class, "marritalstati.xml");
   }
   
   private static void populateCodesFor(PersistenceMechanism pmech,
                                        Set<Class> classList, 
                                        Class cls, String resourceName)
   {
      if (classList.contains(cls))
      {
         populateItemsFor(pmech, cls, resourceName);
      }
   }
   
   public static void populateItemsFor(PersistenceMechanism pmech, 
                                       Class klass, String resourceName)
   {
      List items = (resourceName.endsWith(".json")) ? unmarshalJSON(resourceName)
            : unmarshalXML(klass, resourceName);
      if (items == null)
      {
         // redo this design entirely
         return;
      }
      saveItems(pmech, klass, items);
   }

   public static List unmarshalJSON(String resourceName)
   {
      InputStream stream = getStreamForResource(resourceName);
      String data;
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

   private static void saveItems(PersistenceMechanism pmech, Class klass, final List items)
   {
      if (!(pmech instanceof HBMPersistenceMechanism))
         return;

      HBMPersistenceMechanism hbm = (HBMPersistenceMechanism) pmech;
      org.hibernate.Session session = hbm.getSession();

      Criteria criteria = session.createCriteria(klass);
      Criteria countCriteria = criteria.setProjection(Projections.rowCount());
      int count = (Integer) countCriteria.uniqueResult();

      if (count > 0)
         return;

      hbm.transaction(new HBMBlock()
      {
         public void invoke(Session session)
         {
            for (Iterator itr = items.iterator(); itr.hasNext(); )
               session.save(itr.next());
         }
      });
   }


   public static void dumpItemsFor(Class klass) throws Exception
   {
      CodesList codesList = new CodesList();
      PersistenceMechanism pmech = Context.getInstance().getPersistenceMechanism();
      codesList.setItems(pmech.list(klass));
      JibxBoiler boiler = new JibxBoiler(klass);
      boiler.marshal("dump.xml");
   }
}
