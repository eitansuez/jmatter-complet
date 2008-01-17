package com.u2d.sympster;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.StringEO;
import com.u2d.persist.Persist;

@Persist
public class City
      extends AbstractComplexEObject
{
   private final StringEO name = new StringEO();

   public City()
   {
   }

   public StringEO getName()
   {
      return name;
   }

   public Title title()
   {
      return name.title();
   }
}
