/*
 * Created on May 12, 2004
 */
package com.u2d.find.inequalities;

import com.u2d.model.EObject;
import com.u2d.model.Editor;
import com.u2d.type.atom.StringEO;
import com.u2d.view.*;
import com.u2d.element.Field;
import org.hibernate.Criteria;
import org.hibernate.criterion.*;
import java.util.*;

/**
 * Using delegation to allow various text-based atomic types
 * to share implementation of text-base inequalities
 * 
 * @author Eitan Suez
 */
public class TextualInequalities
{
   private EView _ineqView;
   private List _inequalities;
   
   public TextualInequalities()
   {
      _inequalities = new ArrayList();
      _inequalities.add(new TextContains());
      _inequalities.add(new TextStarts());
      _inequalities.add(new TextEnds());
      _inequalities.add(new TextIs());
   }
   
   public TextualInequalities(Field field)
   {
      this();

      //_ineqView = field.createInstance().getView();
      // explanation:
      //  view for textual inequalities should be a textual view.
      //  that is, for usphones if use usphoneview then the view would
      //  attempt to validate a fraction of a phone number (valid for queries
      //  based on the textual content of the phonenumber) and would not transfer
      //  the view's value to the object.
      
      EObject eo = field.createInstance();
      StringEO seo = new StringEO(eo.toString());
      _ineqView = seo.getView();
      
      ((Editor) _ineqView).setEditable(true);
   }
   
   public List getInequalities() { return _inequalities; }

   
   public class TextIs extends AbstractInequality
   {
      public TextIs() {}
      
      public void addExpression(Criteria criteria, Field field, EObject eo)
      {
         Criterion criterion = Expression.eq(field.getCleanPath(), eo).ignoreCase();
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
   
   public class TextContains extends AbstractInequality
   {
      public TextContains() {}
      
      public void addExpression(Criteria c, Field field, EObject eo)
      {
         Criterion criterion = 
            Expression.ilike(field.getCleanPath(), eo.toString(), MatchMode.ANYWHERE);
         c.add(criterion);
      }

      public String toString() { return "contains"; }
      
      public EView getValueEditor() { return _ineqView; }
      public EObject getValue()
      {
         ((Editor) _ineqView).transferValue();
         return _ineqView.getEObject();
      }
   }

   public class TextStarts extends AbstractInequality
   {
      public TextStarts() {}
      
      public void addExpression(Criteria c, Field field, EObject eo)
      {
         Criterion criterion = 
            Expression.ilike(field.getCleanPath(), eo.toString(), MatchMode.START);
         c.add(criterion);
      }

      public String toString() { return "starts with"; }
      
      public EView getValueEditor() { return _ineqView; }
      public EObject getValue()
      {
         ((Editor) _ineqView).transferValue();
         return _ineqView.getEObject();
      }
   }
   
   public class TextEnds extends AbstractInequality
   {
      public TextEnds() {}
      
      public void addExpression(Criteria c, Field field, EObject eo)
      {
         Criterion criterion = 
            Expression.ilike(field.getCleanPath(), eo.toString(), MatchMode.END);
         c.add(criterion);
      }

      public String toString() { return "ends with"; }
      
      public EView getValueEditor() { return _ineqView; }
      public EObject getValue()
      {
         ((Editor) _ineqView).transferValue();
         return _ineqView.getEObject();
      }
   }

}
