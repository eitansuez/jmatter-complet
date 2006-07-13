/*
 * Created on May 12, 2004
 */
package com.u2d.find.inequalities;

import com.u2d.type.Choice;
import com.u2d.view.*;
import com.u2d.view.swing.atom.TypePicker;
import com.u2d.element.Field;
import com.u2d.model.ComplexEObject;
import com.u2d.model.ComplexType;
import com.u2d.model.EObject;
import com.u2d.model.Editor;
import org.hibernate.Criteria;
import org.hibernate.criterion.*;
import java.util.*;

/**
 * @author Eitan Suez
 */
public class IdentityInequality
{
   private ComplexType _type;
   private EView _ineqView;
   private List _inequalities;
   
   public IdentityInequality()
   {
      _inequalities = new ArrayList();
      _inequalities.add(new Equals());
   }
   
   public IdentityInequality(ComplexType type)
   {
      _inequalities = new ArrayList();
      _type = type;
      if (_type.isAbstract())
         _inequalities.add(new TypeInequality());
   }
   public IdentityInequality(Field field)
   {
      this();
      
      _type = field.fieldtype();
      
      if (field.isAssociation() || field.isIndexed())
      {
         ComplexType type = (ComplexType) field.parent();
         ComplexEObject parentInstance = type.instance();
         _ineqView = field.getView(parentInstance);
      }
      else
      {
         _ineqView = field.createInstance().getView();
         ((Editor) _ineqView).setEditable(true);
      }

      if (_type != null && _type.isAbstract())
         _inequalities.add(new TypeInequality());
   }
   
   public List getInequalities() { return _inequalities; }

   
   public class Equals extends AbstractInequality
   {
      public void addExpression(Criteria criteria, Field field, EObject eo)
      {
         if (field.isChoice())
         {
            String code = ((Choice) eo).code();
            Criterion criterion = Expression.eq(field.getCleanPath()+".code", code);
            criteria.add(criterion);
         }
         else
         {
            Criterion criterion = Expression.eq(field.getCleanPath(), eo);
            criteria.add(criterion);
         }
      }

      public String toString() { return "is"; }

      public EView getValueEditor() { return _ineqView; }
      public EObject getValue()
      {
         if (_ineqView instanceof Editor)
            ((Editor) _ineqView).transferValue();
         EObject eo = _ineqView.getEObject();
         if (eo.isEmpty())
            eo = null;
         return eo;
      }
   }

   public class TypeInequality extends AbstractInequality
   {
      TypePicker _picker;
      public void addExpression(Criteria criteria, Field field, EObject eo)
      {
         ComplexType type = (ComplexType) eo;
         String clsName = type.getJavaClass().getName();
         Criterion criterion = Expression.eq(field.getCleanPath()+".class", clsName);
         criteria.add(criterion);
      }
      public String toString() { return "type is"; }

      public EView getValueEditor()
      {
         if (_picker == null)
         {
            _picker = new TypePicker(_type);
            _picker.setEditable(true);
         }
         return _picker;
      }

      public EObject getValue()
      {
         _picker.transferValue();
         return _picker.getEObject();
      }
   }

}
