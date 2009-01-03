package com.u2d.sympster;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.StringEO;
import javax.persistence.Entity;

@Entity
public class City extends AbstractComplexEObject
{

   public City() { }

   private final StringEO name = new StringEO();
   public StringEO getName() { return name; }

   public Title title()
   {
      return new Title("<html><i>" + name.title().toString() + "</i></html>");
   }
}
