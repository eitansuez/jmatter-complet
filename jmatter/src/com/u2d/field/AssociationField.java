/*
 * Created on Mar 9, 2004
 */
package com.u2d.field;

import java.beans.*;
import java.lang.reflect.*;
import com.u2d.app.*;
import com.u2d.element.Field;
import com.u2d.model.*;
import com.u2d.pattern.*;
import com.u2d.type.Choice;
import com.u2d.validation.Required;
import com.u2d.view.*;

/**
 * @author Eitan Suez
 */
public class AssociationField extends Field implements Bidi, Associable
{
   private Field _inverse;
   
   private String _defaultSpec;  //  a valid hibernate query
     // (in the case of a Choice, can simply be a code)
   private boolean _specValid = false;
   protected ComplexEObject _defaultValue;
   
   protected transient Method _associator, _dissociator;
   
   public AssociationField(FieldParent parent, String name) 
      throws IntrospectionException
   {
      super(parent, name);
      introspectAssociator();
   }
   
   public AssociationField(FieldParent parent, PropertyDescriptor descriptor) 
   {
      super(parent, descriptor);
      introspectAssociator();
   }
   
   private void introspectAssociator()
   {
      try
      {
         String methodName = "associate" + Harvester.capitalize(name());
         _associator = _parent.getJavaClass().getMethod(methodName, 
              new Class[] { _clazz } );
      }
      catch (NoSuchMethodException ex)
      {
         // optional so no need to do anythin here.
      }
   }
   
   
   
   public void setInverse(String inverseFieldName)
   {
      if (inverseFieldName == null) return;
//      System.out.println("asssociationfield looking for "+inverseFieldName+" on "+type());
      _inverse = fieldtype().field(inverseFieldName);
//      System.out.println("Found inverse field: "+_inverse);
      ((Bidi) _inverse).setInverseField(this);
   }
   
   public void setInverseField(Field inverseField)
   {
      _inverse = inverseField;
   }

   /*
    * Should be called strictly from the parent ComplexEObject
    * and no one else!
    */
   public Association association(ComplexEObject parent)
   {
      if (_inverse == null)
         return new Association(this, parent);
      return new BidiAssociation(this, parent, _inverse);
   }
   
   public EObject get(ComplexEObject parent)
   {
      EObject eo = (EObject) reflectGet(parent);
      if (eo == null)
         eo = new NullAssociation(this, parent);
      else
         eo.setField(this, parent);
      return eo;
   }
   
   // set from persistence mechanism to restore objects..
   public void restore(ComplexEObject parent, Object value)
   {
      if (value instanceof NullComplexEObject)
         value = null;
      reflectSet(parent, value);
   }
   
   public void set(ComplexEObject parent, Object value)
   {
      if (value instanceof NullComplexEObject)
         value = null;
      if (hasAssociateMethod())
        reflectAssociate(parent, value);
      else
        reflectSet(parent, value);
   }
   
   protected boolean hasAssociateMethod() { return _associator != null; }

   protected void reflectSet(EObject parent, Object value)
   {
      reflectMethod(_setter, parent, value);
   }
   protected void reflectAssociate(EObject parent, Object value)
   {
//      if (value == null)
//      {
//         reflectMethod(_dissociator, parent, value);
//      }
//      else
//      {
         reflectMethod(_associator, parent, value);
//      }
   }
   
   protected void reflectMethod(Method method, EObject parent, Object value)
   {
      Class parentClass = parent().getJavaClass();
      try
      {
         if (!parentClass.isAssignableFrom(parent.getClass()))
         {
            throw new IllegalArgumentException("Invalid parent type: "+parent.getClass()+"; expected: "+parentClass);
         }
         
         method.invoke(parent, new Object[] { value } );
      }
      catch (IllegalAccessException ex)
      {
         System.err.println(ex.getMessage());
         ex.printStackTrace();
      }
      catch (InvocationTargetException ex)
      {
         System.err.println(ex.getMessage());
         ex.printStackTrace();
      }
   }
   
   public EView getView(ComplexEObject parent)
   {
      Association association = parent.association(name());
      return vmech().getAssociationView(association);
   }
   
   public int validate(ComplexEObject parent)
   {
      Association association = parent.association(name());
      Required required = getRequired(parent);
      if (required.isit() && association.isEmpty())
      {
         association.fireValidationException(required.getMsg());
         return 1;
      }
      association.fireValidationException("");
      return 0;
   }
   
   public void setState(ComplexEObject parent, State state)
   {
      // parent's state does not propagate to its associations.
      EObject eo = get(parent);
      if (!(eo instanceof ComplexEObject)) return;
      
      ComplexEObject ceo = (ComplexEObject) eo;
      checkAndSetDefault(parent, ceo);
      if (ceo instanceof NullComplexEObject) return; // nothing more to do
      if (ceo.isNullState())
         ceo.restoreState();
   }
   
   private void checkAndSetDefault(ComplexEObject parent, ComplexEObject ceo)
   {
      if (parent.isTransientState())
      {
         ComplexEObject defaultValue = getDefaultValue();
         if (ceo instanceof NullComplexEObject  && defaultValue != null)
         {
            parent.association(name()).set(defaultValue);
         }
      }
   }

   public ComplexEObject getDefaultValue()
   {
      if (_defaultValue == null && _specValid)
         _defaultValue = resolveSpec();
      
/*      if (_defaultValue == null)
         _defaultValue = type().getDefaultValue();
*/      
      return _defaultValue;
   }
   public void setDefaultSpec(String spec)
   {
      _defaultSpec = spec;
      _specValid = true;
   }
   
   private ComplexEObject resolveSpec()
   {
      PersistenceMechanism pmech = persistor();
      
      if (Choice.class.isAssignableFrom(getJavaClass()))
      {
         return (ComplexEObject) pmech.lookup(getJavaClass(), _defaultSpec);
      }

      if (pmech instanceof HBMPersistenceMechanism)
      {
         HBMPersistenceMechanism pmech2 = (HBMPersistenceMechanism) pmech;
         ComplexEObject ceo = pmech2.fetch(_defaultSpec);
         if (ceo == null)
            _specValid = false;
         return ceo;
      }
      return null;
   }

   public boolean isInterfaceType() { return _clazz.isInterface(); }
   public boolean isAbstract()
   {
      return _clazz.isInterface() || 
            Modifier.isAbstract(_clazz.getModifiers());
   }
   public ComplexType fieldtype() { return ComplexType.forClass(_clazz); }


}
