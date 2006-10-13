/*
 * Created on Feb 9, 2004
 */
package com.u2d.persist;

import com.u2d.app.*;
import java.util.*;
import com.u2d.list.SimpleListEO;
import com.u2d.list.PlainListEObject;
import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.AbstractListEO;
import com.u2d.model.ComplexEObject;
import com.u2d.model.ComplexType;
import com.u2d.type.Choice;
import com.u2d.type.USState;

/**
 * @author Eitan Suez
 */
public class InMemoryPersistor implements PersistenceMechanism
{
   private Map store = new HashMap();
   private Map IDs = new HashMap();
   
   private static PersistenceMechanism _instance = null;
   
   public static PersistenceMechanism getInstance()
   {
      if (_instance == null)
      {
         _instance = new InMemoryPersistor();
         USState state = new USState("TX", "Texas");
         _instance.save(state);
         state = new USState("MA", "Massachussets");
         _instance.save(state);
      }
      return _instance;
   }
   
   private InMemoryPersistor() {}
   
   public ComplexEObject load(Class clazz, Long id)
   {
      Map typeMap = getTypeMap(clazz);
      ComplexEObject item = (ComplexEObject) typeMap.get(id);
      item.onLoad();
      return item;
   }
   
   public ComplexEObject fetchSingle(Class clazz)
   {
      SimpleListEO leo = list(clazz);
      if (leo.isEmpty()) return null;
      return (ComplexEObject) leo.iterator().next();
   }

   private Map getTypeMap(Class clazz)
   {
      Map typeMap = (Map) store.get(clazz);
      if (typeMap == null)
      {
         store.put(clazz, new HashMap());
         IDs.put(clazz, new Long(1));
      }
      return (Map) store.get(clazz);
   }

   public void save(ComplexEObject ceo)
   {
      if (ceo.getID() != null)
      {
         update(ceo);
         return;
      }
      getTypeMap(ceo.getClass());  // for side effect on IDs
      Long id = fabricateID(ceo);
      store(ceo, id);
   }
   
   public void updateAssociation(ComplexEObject one, ComplexEObject two)
   {
      // noop
   }
   
   
   Map choiceMap = new HashMap();
   
   private void update(ComplexEObject ceo)
   {
      getTypeMap(ceo.getClass());
      ceo.onSave();
   }
   private void store(ComplexEObject ceo, Long id)
   {
      ceo.setID(id);
      
      Class clazz = ceo.getClass();
      while ( !clazz.equals(AbstractComplexEObject.class) )
      {
         getTypeMap(clazz).put(id, ceo);
         clazz = clazz.getSuperclass();
      }
      
      if (ceo instanceof Choice)
      {
         Choice choice = (Choice) ceo;
         Map map = (Map) choiceMap.get(choice.getClass());
         if (map == null)
         {
            map = new HashMap();
         }
         map.put(choice.code(), choice);
         choiceMap.put(choice.getClass(), map);
      }
      
      ceo.onCreate();
   }
   
   public Choice lookup(Class clazz, String code)
   {
      Map map = (Map) choiceMap.get(clazz);
      if (map == null) return null;
      return (Choice) map.get(code);
   }
   
   private Long fabricateID(ComplexEObject ceo)
   {
      Long id = (Long) IDs.get(ceo.getClass());
      Long newid = new Long(id.longValue() + 1);
      IDs.put(ceo.getClass(), newid);
      return newid;
   }
   
   public void delete(ComplexEObject ceo)
   {
      Map typeMap = getTypeMap(ceo.getClass());
      typeMap.remove(ceo.getID());
      ceo.onDelete();
   }

   public AbstractListEO browse(Class clazz)
   {
      return list(clazz);
   }
   
   public PlainListEObject list(Class clazz)
   {
      Map typeMap = getTypeMap(clazz);
      Collection items = typeMap.values();
      Iterator itr = items.iterator();
      while (itr.hasNext())
      {
         ((ComplexEObject) itr.next()).onLoad();
      }
      List itemsList = new ArrayList();
      itemsList.addAll(items);
      return new PlainListEObject(clazz, itemsList);
   }
   public PlainListEObject list(ComplexType type)
   {
      return list(type.getJavaClass());
   }
   
   public boolean authenticate(String username, String password)
   {
      // assume a mock mechanism..
      return "blah".equalsIgnoreCase(password);
      // (TODO: read password from config file) 
   }
   
}
