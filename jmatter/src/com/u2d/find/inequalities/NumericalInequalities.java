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
public class NumericalInequalities
{
   private EView _ineqView;
   private List _inequalities;
   
   public NumericalInequalities()
   {
      _inequalities = new ArrayList();
      _inequalities .add(new NumEquals());
      _inequalities .add(new NumLessThan());
      _inequalities .add(new NumGreaterThan());
//      _inequalities .add(new NumRange());
   }
   
   public NumericalInequalities(Field field)
   {
      this();

      _ineqView = field.createInstance().getView();
      ((Editor) _ineqView).setEditable(true);
   }
   
   public List getInequalities() { return _inequalities; }

   
   public class NumEquals extends AbstractInequality
   {
      public void addExpression(Criteria criteria, Field field, EObject eo)
      {
         Criterion criterion = Expression.eq(field.getCleanPath(), eo);
         criteria.add(criterion);
      }

      public String toString() { return "is equal to"; }
      
      public EView getValueEditor() { return _ineqView; }
      public EObject getValue()
      {
         ((Editor) _ineqView).transferValue();
         return _ineqView.getEObject();
      }
   }
   
   public class NumLessThan extends AbstractInequality
   {
      public void addExpression(Criteria criteria, Field field, EObject eo)
      {
         Criterion criterion = Expression.lt(field.getCleanPath(), eo);
         criteria.add(criterion);
      }

      public String toString() { return "is less than"; }
      
      public EView getValueEditor() { return _ineqView; }
      public EObject getValue()
      {
         ((Editor) _ineqView).transferValue();
         return _ineqView.getEObject();
      }
   }
   
   public class NumGreaterThan extends AbstractInequality
   {
      public void addExpression(Criteria criteria, Field field, EObject eo)
      {
         Criterion criterion = Expression.gt(field.getCleanPath(), eo);
         criteria.add(criterion);
      }

      public String toString() { return "is greater than"; }

      public EView getValueEditor() { return _ineqView; }
      public EObject getValue()
      {
         ((Editor) _ineqView).transferValue();
         return _ineqView.getEObject();
      }
   }

//   public class NumRange extends AbstractInequality
//   {
//      private JPanel _editor;  // num range has its own editor, it does not share it
//      private JTextField _from, _to;
//      
//      public JComponent getValueEditor()
//      {
//         if (_editor == null)
//         {
//            _editor = new JPanel();
//            _from = new JTextField(8);
//            _editor.add(_from);
//            _editor.add(new JLabel("and"));
//            _to = new JTextField(8);
//            _editor.add(_to);
//         }
//         return _editor;
//      }
//      public Criterion getExpression()
//      {
//         int fromvalue = Integer.parseInt( _from.getText() );
//         int tovalue = Integer.parseInt( _to.getText() );
//         
//         return Expression.between(_eo.field().getName(), new IntEO(fromvalue), new IntEO(tovalue));
//      }
//
//      public String toString() { return "between"; }
//   }
   
}
