/*
 * Created on May 12, 2004
 */
package com.u2d.find.inequalities;

import com.u2d.type.Choice;
import com.u2d.view.*;
import com.u2d.element.Field;
import com.u2d.model.ComplexEObject;
import com.u2d.model.ComplexType;
import com.u2d.model.EObject;
import com.u2d.model.Editor;
import com.u2d.find.Inequality;
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
   private List<Inequality> _inequalities;
   
   public IdentityInequality()
   {
      _inequalities = new ArrayList<Inequality>();
      _inequalities.add(new Equals());
      _inequalities.add(new NotEquals());
   }
   
   public IdentityInequality(ComplexType type)
   {
      _inequalities = new ArrayList<Inequality>();
      _type = type;
      if (_type.hasConcreteSubTypes())
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

      if (_type != null && _type.hasConcreteSubTypes())
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
            Criterion criterion = Restrictions.eq(field.getCleanPath()+".code", code);
            criteria.add(criterion);
         }
         else
         {
            Criterion criterion = Restrictions.eq(field.getCleanPath(), eo);
            criteria.add(criterion);
         }
      }

      public String toString() { return ComplexType.localeLookupStatic("is"); }

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

   public class NotEquals extends AbstractInequality
   {
      public void addExpression(Criteria criteria, Field field, EObject eo)
      {
         if (field.isChoice())
         {
            String code = ((Choice) eo).code();
            Criterion criterion = Restrictions.ne(field.getCleanPath()+".code", code);
            criteria.add(criterion);
         }
         else
         {
            Criterion criterion = Restrictions.ne(field.getCleanPath(), eo);
            criteria.add(criterion);
         }
      }

      public String toString() { return ComplexType.localeLookupStatic("is_not"); }

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
      EView _picker;
      public void addExpression(Criteria criteria, Field field, EObject eo)
      {
         ComplexType type = (ComplexType) eo;
         String clsName = type.getJavaClass().getName();
         Criterion criterion = Restrictions.eq(field.getCleanPath()+".class", clsName);
         criteria.add(criterion);
      }
      public String toString() { return ComplexType.localeLookupStatic("type_is"); }

      public EView getValueEditor()
      {
         if (_picker == null)
         {
            _picker = vmech().getTypePicker(_type);
            ((Editor) _picker).setEditable(true);
         }
         return _picker;
      }

      public EObject getValue()
      {
         ((Editor) _picker).transferValue();
         return _picker.getEObject();
      }
   }

}
