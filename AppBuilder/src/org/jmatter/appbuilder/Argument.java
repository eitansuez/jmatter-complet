package org.jmatter.appbuilder;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.StringEO;

import javax.persistence.Entity;

@Entity
public class Argument extends AbstractComplexEObject
{
   public static String[] fieldOrder = {"name", "argType", "caption"};

   public Argument() { }

   private final StringEO name = new StringEO();
   public StringEO getName() { return name; }

   private final StringEO argType = new StringEO();
   public StringEO getArgType() { return argType; }

   private final StringEO caption = new StringEO();
   public StringEO getCaption() { return caption; }

   public Title title() { return name.title(); }
}
