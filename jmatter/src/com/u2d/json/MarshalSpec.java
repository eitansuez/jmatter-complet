package com.u2d.json;

import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Jun 24, 2008
 * Time: 1:25:06 PM
 */
public class MarshalSpec
{
   private Set<String> byRef = new HashSet<String>();
   private Map<String, MarshalSpec> includes = new HashMap<String, MarshalSpec>();
   private Map<String, Set<String>> compositeChildrenByRef = new HashMap<String, Set<String>>();

   public MarshalSpec() {}

   public MarshalSpec includeByValue(String... associations)
   {
      for (String association : associations)
      {
         includeByValue(association, new MarshalSpec());
      }
      return this;
   }
   public MarshalSpec includeByValue(String association, MarshalSpec childSpec)
   {
      includes.put(association, childSpec);
      return this;
   }
   private boolean associationIsOnChildComposite(String association)
   {
      return association.indexOf(".") > 0;
   }
   private void addChildAssociationByRef(String association)
   {
      int index = association.indexOf(".");
      String key = association.substring(0, index);
      String value = association.substring(index+1);
      Set<String> set = compositeChildrenByRef.get(key);
      if (set == null)
      {
         set = new HashSet<String>();
         compositeChildrenByRef.put(key, set);
      }
      set.add(value);
   }
   public MarshalSpec includeByReference(String... associations)
   {
      for (String association : associations)
      {
         if (associationIsOnChildComposite(association))
         {
            addChildAssociationByRef(association);
         }
         else
         {
            byRef.add(association);
         }
      }
      return this;
   }

   public boolean includes(String association)
   {
      return includes.keySet().contains(association);
   }
   public MarshalSpec specFor(String association)
   {
      MarshalSpec spec = includes.get(association);
      return (spec == null) ? new MarshalSpec() : spec;
   }
   public boolean byRef(String association)
   {
      return byRef.contains(association);
   }

   public MarshalSpec specForComposite(String fieldname)
   {
      MarshalSpec spec = new MarshalSpec();
      Set<String> childByRefs = compositeChildrenByRef.get(fieldname);

      if (childByRefs == null) return spec;

      for (String item : childByRefs)
      {
         spec.includeByReference(item);
      }
      return spec;
   }
}
