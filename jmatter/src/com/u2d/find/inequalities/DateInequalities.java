/*
 * Created on May 12, 2004
 */
package com.u2d.find.inequalities;

import com.u2d.view.*;
import com.u2d.element.Field;
import com.u2d.model.EObject;
import com.u2d.model.Editor;
import org.hibernate.Criteria;
import org.hibernate.criterion.*;

import java.util.*;

/**
 * Using delegation to allow various number-based atomic types
 * to share implementation of numerical inequalities
 * 
 * @author Eitan Suez
 */
public class DateInequalities
{
   private EView _ineqView;
   private List _inequalities;
   
   public DateInequalities()
   {
      _inequalities = new ArrayList();
      _inequalities.add(new DateBefore());
      _inequalities.add(new DateIs());
      _inequalities.add(new DateAfter());
   }
    
   public DateInequalities(Field field, boolean wantEqToo)
   {
      _ineqView = field.createInstance().getView();
      ((Editor) _ineqView).setEditable(true);
      
      _inequalities = new ArrayList();
      _inequalities.add(new DateBefore());
      if (wantEqToo)
         _inequalities.add(new DateIs());
      _inequalities.add(new DateAfter());
   }
   
   public List getInequalities() { return _inequalities; }

   public class DateBefore extends AbstractInequality
   {
      public void addExpression(Criteria criteria, Field field, EObject eo)
      {
         Criterion criterion = Expression.lt(field.getCleanPath(), eo);
         criteria.add(criterion);
      }

      public String toString() { return "is before"; }
      
      public EView getValueEditor() { return _ineqView; }
      public EObject getValue()
      {
         ((Editor) _ineqView).transferValue();
         return _ineqView.getEObject();
      }
   }
   
   public class DateAfter extends AbstractInequality
   {
      public void addExpression(Criteria criteria, Field field, EObject eo)
      {
         Criterion criterion = Expression.gt(field.getCleanPath(), eo);
         criteria.add(criterion);
      }

      public String toString() { return "is after"; }
      
      public EView getValueEditor() { return _ineqView; }
      public EObject getValue()
      {
         ((Editor) _ineqView).transferValue();
         return _ineqView.getEObject();
      }
   }
   
   public class DateIs extends AbstractInequality
   {
      public void addExpression(Criteria criteria, Field field, EObject eo)
      {
         Criterion criterion = Expression.eq(field.getCleanPath(), eo);
         criteria.add(criterion);
      }
      
      public String toString() { return "is"; }
      
      public EView getValueEditor() { return _ineqView; }
      public EObject getValue()
      {
         ((Editor) _ineqView).transferValue();
         return _ineqView.getEObject();
      }
   }
   
}
