/*
 * Created on Apr 27, 2005
 */
package com.u2d.find.inequalities;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import com.u2d.find.Inequality;
import com.u2d.model.AbstractEObject;
import com.u2d.model.EObject;
import com.u2d.model.Title;
import com.u2d.pattern.Onion;
import com.u2d.view.EView;

import javax.swing.*;

/**
 * @author Eitan Suez
 */
public abstract class AbstractInequality extends AbstractEObject
                                         implements Inequality
{
   public Title title() { return new Title(toString()); }

   // class equality implies equivalence
   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (!(obj instanceof Inequality)) return false;
      return obj.getClass().equals(getClass());
   }

   public int hashCode() { return getClass().hashCode(); }

   public static Inequality get(Class clazz) throws Exception
   {
      Class parentClass = clazz.getDeclaringClass();
      Object parentInstance = parentClass.newInstance();
      Method method = parentClass.getMethod("getInequalities", null);
      List inequalities = (List) method.invoke(parentInstance, null);
      Iterator itr = inequalities.iterator();
      Inequality ineq = null;
      while (itr.hasNext())
      {
         ineq = (Inequality) itr.next();
         if (clazz.getName().equals(ineq.getClass().getName()))
         {
            return ineq;
         }
      }
      return null;
   }

   // === implementation of eobject..

   public EView getView() { return null; }  // see FieldFilter
   public EView getMainView() { return null; }
   public boolean isEmpty() { return false; }
   public EObject makeCopy() { return this; }
   public void setValue(EObject value) {}

   public Onion commands() { return null; }
   public int validate() { return 0; }


   public Icon iconSm() { return null; }
   public Icon iconLg() { return null; }
}
