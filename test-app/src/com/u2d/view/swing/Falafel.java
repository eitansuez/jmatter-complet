package com.u2d.view.swing;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.StringEO;
import com.u2d.type.atom.IntEO;
import com.u2d.type.atom.FloatEO;
import com.u2d.type.atom.DateEO;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Mar 17, 2006
 * Time: 1:44:24 PM
 */
public class Falafel extends AbstractComplexEObject
{
   private final StringEO name = new StringEO();
   private final IntEO count = new IntEO();
   private final FloatEO weight = new FloatEO();
   private final DateEO when = new DateEO();

   public static final String[] fieldOrder = {"name", "count", "weight", "when"};

   public Falafel() {}
   public Falafel(String name, int count, double weight, Date when)
   {
      this.name.setValue(name);
      this.count.setValue(count);
      this.weight.setValue(weight);
      this.when.setValue(when);
      setStartState();
   }

   public StringEO getName() { return name; }
   public IntEO getCount() { return count; }
   public FloatEO getWeight() { return weight; }
   public DateEO getWhen() { return when; }

   public Title title()
   {
      return name.title().append(when);
   }
}
