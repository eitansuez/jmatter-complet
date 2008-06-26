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
   public MarshalSpec includeByReference(String... associations)
   {
      for (String association : associations)
      {
         byRef.add(association);
      }
      return this;
   }

   public boolean includes(String association)
   {
      return includes.keySet().contains(association);
   }
   public MarshalSpec specFor(String association)
   {
      return includes.get(association);
   }
   public boolean byRef(String association)
   {
      return byRef.contains(association);
   }
}
