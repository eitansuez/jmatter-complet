/*
 * Created on Mar 9, 2004
 */
package com.u2d.field;

import java.beans.*;
import java.util.List;
import com.u2d.app.PersistenceMechanism;
import com.u2d.element.Field;
import com.u2d.model.AbstractListEO;
import com.u2d.model.ComplexEObject;
import com.u2d.model.ComplexType;
import com.u2d.model.EObject;
import com.u2d.model.FieldParent;
import com.u2d.model.Harvester;
import com.u2d.model.NullComplexEObject;
import com.u2d.pattern.*;
import com.u2d.validation.Required;
import com.u2d.view.*;
import com.u2d.list.CompositeList;
import com.u2d.reflection.IdxFld;

/**
 * @author Eitan Suez
 */
public class IndexedField extends Field implements Bidi, Associable
{
   protected ComplexType _fieldtype;
   protected String _inverseFieldName = null;
   protected Field _inverseField = null;
   protected Boolean _inverseSide = null;
   
   public IndexedField() {}

   public IndexedField(FieldParent parent, PropertyDescriptor descriptor)
   {
      super(parent, descriptor);
      _inverseFieldName =
         (String) Harvester.introspectField(parent.getJavaClass(),
                                            name() + "InverseFieldName");
   }

   public IndexedField(FieldParent parent, String name)
         throws IntrospectionException
   {
      super(parent, name);
      _inverseFieldName =
         (String) Harvester.introspectField(parent.getJavaClass(),
                                            name() + "InverseFieldName");
   }

   public Field inverseField()
   {
      if (_inverseFieldName == null) return null;

      if (_inverseField == null)
      {
//         System.out.println("indexedfield looking for "+_inverseFieldName+" on "+type());
         _inverseField = fieldtype().field(_inverseFieldName);
//         System.out.println("Found inverse field: "+_inverseField);
         ((Bidi) _inverseField).setInverseField(this);
      }
      return _inverseField;
   }

   public void setInverseField(Field inverseField)
   {
      _inverseField = inverseField;
   }

   /*
    * Should be called strictly from the parent ComplexEObject
    * and no one else!
    */
   public Association association(ComplexEObject ceo)
   {
      if (_inverseFieldName == null)
      {
         return new Association(this, ceo);
      }
      else
      {
         return new Association(this, ceo, inverseField());
      }
   }

   public boolean isBidirectionalRelationship()
   {
      return (_inverseFieldName != null);
   }
   public String getInverseFieldName()
   {
      if (_inverseFieldName == null)
         throw new IllegalArgumentException("not a bidirectional relationship");
      return _inverseFieldName;
   }

   public boolean isInverse()
   {
      if (_inverseSide == null)
      {
         try
         {
            java.lang.reflect.Field f = _parent.getJavaClass().getField(getName()+"RelationIsInverse");
            _inverseSide = (Boolean) f.get(null);
         }
         catch (Exception ex)
         {
            _inverseSide = Boolean.FALSE;
         }
      }
      return _inverseSide.booleanValue();
   }

   public ComplexType fieldtype()
   {
      if (_fieldtype == null)
      {
         // a new mechanism to determine item type statically (developer provides info in class def)
         String fldName = getName()+"Type";
         try
         {
            java.lang.reflect.Field f = _parent.getJavaClass().getField(fldName);
            Class fieldCls = (Class) f.get(null);
            _fieldtype = ComplexType.forClass(fieldCls);
         }
         catch (NoSuchFieldException ex)
         {
            throw new RuntimeException("A field named '" + fldName +
                  "' must be declared on " + _parent.getJavaClass());
         }
         catch (IllegalAccessException ex)
         {
            throw new RuntimeException("Failed to gain proper access to field " +
                  fldName + "(Class: " + _parent.getJavaClass() + ")");
         }
      }
      return _fieldtype;
   }

   public boolean isComposite() { return false; }

   private Boolean _manyToMany = null;
   public boolean isManyToMany()
   {
      if (_manyToMany == null)
      {
         try
         {
            java.lang.reflect.Field f = _parent.getJavaClass().getField(getName()+"RelationType");
            int relationType = ((Integer) f.get(null)).intValue();
            _manyToMany = Boolean.valueOf(relationType == PersistenceMechanism.MANY_TO_MANY);
         }
         catch (NoSuchFieldException ex)
         {
            _manyToMany = Boolean.FALSE;
         }
         catch (IllegalAccessException ex)
         {
            _manyToMany = Boolean.FALSE;
         }
      }
      return _manyToMany.booleanValue();
   }

   public EObject get(ComplexEObject parent)
   {
      EObject eo = (EObject) reflectGet(parent);
      eo.setField(this, parent);
      return eo;
   }

   public EView getView(ComplexEObject parent)
   {
      AbstractListEO value = (AbstractListEO) get(parent);
      EView view = value.getView();
      return (viewHandler == null ? view : viewHandler.getView(value, view));
   }

   public void set(ComplexEObject parent, Object value)
   {
      if (value instanceof AbstractListEO)
      {
         AbstractListEO eo = (AbstractListEO) get(parent);
         eo.setValue((AbstractListEO) value);
      }
      else if (value instanceof List)
      {
         AbstractListEO eo = (AbstractListEO) get(parent);
         eo.setItems((List) value);
      }
      else
      {
         reflectSet(parent, value);
      }
   }
   public void restore(ComplexEObject parent, Object value)
   {
      AbstractListEO eo = (AbstractListEO) get(parent);
      eo.restoreItems((List) value);
   }

   protected void reflectSet(EObject parent, Object value)
   {
      Class parentClass = parent().getJavaClass();
      if (!parentClass.isAssignableFrom(parent.getClass()))
      {
         throw new IllegalArgumentException("Invalid parent type: "+parent.getClass()+"; expected: "+parentClass);
      }

      AbstractListEO leo = (AbstractListEO) reflectGet(parent);
      leo.add((ComplexEObject) value);
   }


   public int validate(ComplexEObject parent)
   {
      EObject value = get(parent);
      Required required = getRequired(parent);
      if (required.isit() && value.isEmpty())
      {
         value.fireValidationException(required.getMsg());
         return 1;
      }

      value.fireValidationException("");  // to reset the msg
      return 0;
   }

   // assume that one-many is associative, not aggregative/compositive
   public void setState(ComplexEObject parent, State state)
   {
      AbstractListEO leo = (AbstractListEO) get(parent);
      
      if (leo instanceof CompositeList) // propagate state to children
      {
         ((CompositeList)leo).setState(state);
         return;
      }

      for (java.util.Iterator itr = leo.iterator(); itr.hasNext(); )
      {
         ComplexEObject ceo = (ComplexEObject) itr.next();
//         ceo.setState(state, true /* shallow */);
         if (ceo instanceof NullComplexEObject) continue;
         if (ceo.isNullState())
            ceo.restoreState();
      }
   }

   public void pushState(ComplexEObject parent, State state)
   {
      AbstractListEO leo = (AbstractListEO) get(parent);

      if (leo instanceof CompositeList) // propagate state to children
      {
         ((CompositeList)leo).pushState(state);
      }
   }

   public void popState(ComplexEObject parent)
   {
      AbstractListEO leo = (AbstractListEO) get(parent);

      if (leo instanceof CompositeList) // propagate state to children
      {
         ((CompositeList)leo).popState();
      }
   }

   private boolean _ordered;
   public boolean isOrdered() { return _ordered; }
   public void setOrdered(boolean ordered) { _ordered = ordered; }

   public void applyMetadata()
   {
      super.applyMetadata();
      if (_getter.isAnnotationPresent(IdxFld.class))
      {
         IdxFld fat = (IdxFld) _getter.getAnnotation(IdxFld.class);
         _ordered = fat.ordered();
      }
   }
}
