package org.jmatter.j1mgr;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.StringEO;
import com.u2d.persist.Persist;

@Persist
public class Track extends AbstractComplexEObject
{
   private final StringEO name = new StringEO();
   
   public static String[] identities = {"name"};

   public Track()
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
