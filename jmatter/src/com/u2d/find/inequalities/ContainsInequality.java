/*
 * Created on May 12, 2004
 */
package com.u2d.find.inequalities;

//import com.u2d.view.*;
import com.u2d.field.IndexedField;
//import com.u2d.find.Inequality;
import java.util.*;

/**
 * @author Eitan Suez
 */
public class ContainsInequality
{
//   private IndexedField _field;
// private Inequality _equals;
//   private EView _ineqView;
   private List _inequalities;
   
   public ContainsInequality(IndexedField field)
   {
//      _field = field;
//      IdentityInequality sub = new IdentityInequality(_field);
//      _equals = (Inequality) sub.getInequalities().get(0);
//      _ineqView = _equals.getValueEditor();
      
      _inequalities = new ArrayList();
//      _inequalities.add(new Contains());
   }
   
   public List getInequalities() { return _inequalities; }

   
//   public class Contains extends AbstractInequality
//   {
//      public void addExpression(Criteria criteria) throws HibernateException
//      {
//         if (_ineqView instanceof Editor)
//            ((Editor) _ineqView).transferValue();
//         
//         Criteria subCriteria = criteria.createCriteria(_field.getCleanPath());
//         _equals.addExpression(subCriteria);
//      }
//      public void addExpression(Criteria criteria, Field field, EObject eo)
//      {
//         try
//         {
//            Criteria subCriteria = criteria.createCriteria(field.getCleanPath());
//            _equals.addExpression(subCriteria);
//         }
//         catch (HibernateException ex)
//         {
//            System.err.println("HibernateException: "+ex.getMessage());
//            ex.printStackTrace();
//         }
//      }
//
//      public EView getValueEditor() { return _ineqView; }
//
//      public String toString() { return "contains"; }
//   }

}
