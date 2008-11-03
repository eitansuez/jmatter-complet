package com.u2d.examples;

import com.u2d.model.*;
import com.u2d.type.atom.IntEO;
import com.u2d.reflection.Fld;
import javax.persistence.Entity;

@Entity
public class CalculatedField extends AbstractComplexEObject
{
   public static String[] readOnly = {"sum"};
   
   private final IntEO a = new IntEO();
   public IntEO getA() { return a; }

   private final IntEO b = new IntEO();
   public IntEO getB() { return b; }

   private final IntEO sum = new IntEO();
   @Fld(persist=false)
   public IntEO getSum() { return sum; }

   public CalculatedField()
   {
      configureAsDerivedField(sum, new EObject[] {a, b}, new Updater()
      {
         public EObject update()
         {
            return a.add(b);
         }
      });
   }

   public Title title()
   {
      return a.title().append(" +", b).append(" =", sum);
   }
}
