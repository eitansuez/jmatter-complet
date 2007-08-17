package com.u2d.model;

import com.u2d.pattern.Onion;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Sep 22, 2005
 * Time: 10:30:36 AM
 */
public class AtomicType
{
   private static transient Map<Class, AtomicType> _typeCache = new HashMap<Class, AtomicType>();

   public static AtomicType forClass(Class targetClass)
   {
      if (!(AtomicEObject.class.isAssignableFrom(targetClass)))
      {
         throw new RuntimeException("Cannot create Atomic Type for "+targetClass.getName());
      }

      if (_typeCache.get(targetClass) == null)
         _typeCache.put(targetClass, new AtomicType(targetClass));

      return (AtomicType) _typeCache.get(targetClass);
   }
   
   public static AtomicType forObject(AtomicEObject targetObject)
   {
      return forClass(targetObject.getClass());
   }
   
   // ====
   
   private transient Onion _commands;

   private AtomicType(Class instanceClass)
   {
         _commands = Harvester.simpleHarvestCommands(instanceClass, 
                                                     new Onion(), 
                                                     false, null);
   }
   
   public Onion commands() { return _commands; }
   
}
