package com.u2d.json;

import com.u2d.model.ComplexEObject;
import com.u2d.model.AbstractListEO;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Aug 14, 2008
 * Time: 11:09:42 AM
 */
public class MapDereferencer implements Dereferencer
{
   private Map<Class, Map<Long, ComplexEObject>> container;

   public MapDereferencer()
   {
      container = new HashMap<Class, Map<Long, ComplexEObject>>();
   }

   public void add(Class cls, Long id, ComplexEObject value)
   {
      ensure(cls);
      container.get(cls).put(id, value);
   }

   private void ensure(Class cls)
   {
      if (!container.containsKey(cls))
      {
         container.put(cls, new HashMap<Long, ComplexEObject>());
      }
   }

   public boolean has(Class cls, Long id)
   {
      return container.containsKey(cls) && container.get(cls).containsKey(id);
   }
   public ComplexEObject get(Class cls, Long id)
   {
      if (!has(cls, id)) return null;
      return container.get(cls).get(id);
   }


   public void addAll(AbstractListEO list)
   {
      Class type = list.type().getJavaClass();
      for (int i=0; i<list.getSize(); i++)
      {
         ComplexEObject eo = (ComplexEObject) list.getElementAt(i);
         add(type, eo.getID(), eo);
      }
   }

}
