/*
 * Created on May 12, 2004
 */
package com.u2d.find.inequalities;

import com.u2d.model.ComplexType;
import com.u2d.model.EObject;
import com.u2d.model.Editor;
import com.u2d.type.atom.DateEO;
import com.u2d.view.*;
import com.u2d.element.Field;
import org.hibernate.Criteria;
import org.hibernate.criterion.*;
import java.util.*;

/**
 * TimeSpan inequalities:
 *  notice that there is no need to provide a "between" inequality
 *  because this can be composed in the ui by combining a "before"
 *  and an "after" inequality.
 * 
 * @author Eitan Suez
 */
public class TimeSpanInequalities
{
//   private Field _field;
   private EView _ineqView;
   private List _inequalities;

   public TimeSpanInequalities()
   {
      _ineqView = new DateEO().getView();
      ((Editor) _ineqView).setEditable(true);
      
      _inequalities = new ArrayList();
      _inequalities.add(new SpanOn());
      _inequalities.add(new SpanBefore());
      _inequalities.add(new SpanAfter());
   }

   public TimeSpanInequalities(Field field)
   {
      this();
//      _field = field;
   }
   
   public List getInequalities() { return _inequalities; }
   
   
   public class SpanOn extends AbstractInequality
   {
      
      public void addExpression(Criteria criteria, Field field, EObject eo)
      {
         DateEO deo = (DateEO) eo;
         
         Date begindate = deo.dateValue();
         
         Calendar cal = Calendar.getInstance();
         cal.setTime(begindate);
         cal.add(Calendar.HOUR, 24);
         Date enddate = cal.getTime();
         
         Criterion firstCriterion = 
            Expression.gt(field.getCleanPath()+".start", begindate);
         
         Criterion secondCriterion = 
            Expression.lt(field.getCleanPath()+".end", enddate);
         
         Junction junction = Expression.conjunction();
         junction.add(firstCriterion);
         junction.add(secondCriterion);
         
         criteria.add(junction);
      }

      public String toString() { return ComplexType.localeLookupStatic("on"); }

      public EView getValueEditor() { return _ineqView; }
      public EObject getValue()
      {
         ((Editor) _ineqView).transferValue();
         return _ineqView.getEObject();
      }
   }
   
   public class SpanBefore extends AbstractInequality
   {
      public void addExpression(Criteria criteria, Field field, EObject eo)
      {
         DateEO deo = (DateEO) eo;
         Date begindate = deo.dateValue();
         Criterion criterion = Expression.lt(field.getCleanPath()+".end", begindate);
         criteria.add(criterion);
      }

      public String toString() { return ComplexType.localeLookupStatic("before"); }
      
      public EView getValueEditor() { return _ineqView; }
      public EObject getValue()
      {
         ((Editor) _ineqView).transferValue();
         return _ineqView.getEObject();
      }
   }

   public class SpanAfter extends AbstractInequality
   {
      public void addExpression(Criteria criteria, Field field, EObject eo)
      {
         DateEO deo = (DateEO) eo;
         
         Date begindate = deo.dateValue();
         Calendar cal = Calendar.getInstance();
         cal.setTime(begindate);
         cal.add(Calendar.HOUR, 24);
         Date enddate = cal.getTime();
         
         Criterion criterion = Expression.gt(field.getCleanPath()+".start", enddate);
         criteria.add(criterion);
      }

      public String toString() { return ComplexType.localeLookupStatic("after"); }
      
      public EView getValueEditor() { return _ineqView; }
      public EObject getValue()
      {
         ((Editor) _ineqView).transferValue();
         return _ineqView.getEObject();
      }
   }
   
}
