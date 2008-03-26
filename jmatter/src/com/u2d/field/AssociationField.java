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
import com.u2d.find.QuerySpecification;
import com.u2d.find.CompositeQuery;
import org.hibernate.HibernateException;

/**
 * @author Eitan Suez
 */
public class AssociationField extends Field implements Bidi, Associable
{
   private Field _inverseField = null;
   protected String _inverseFieldName = null;
   
   private String _defaultSpec;  //  a valid hibernate query
     // (in the case of a Choice, can simply be a code)
   private boolean _specValid = false;
   protected ComplexEObject _defaultValue;
   
   protected transient Method _associator, _dissociator;
   
   protected transient Method _associationConstraint;
   
   public AssociationField() {}
   
   public AssociationField(FieldParent parent, String name) 
      throws IntrospectionException
   {
      super(parent, name);
      introspectAssociator();
      _inverseFieldName =
         (String) Harvester.introspectField(parent.getJavaClass(),
                                            name() + "InverseFieldName");
      checkForAssociationConstraint();
   }
   
   public AssociationField(FieldParent parent, PropertyDescriptor descriptor) 
   {
      super(parent, descriptor);
      introspectAssociator();
      _inverseFieldName =
         (String) Harvester.introspectField(parent.getJavaClass(),
                                            name() + "InverseFieldName");
      checkForAssociationConstraint();
   }
   

   private void checkForAssociationConstraint()
   {
      try
      {
         String methodName = String.format("%sOptions", name());
         _associationConstraint = _parent.getJavaClass().getMethod(methodName);
      }
      catch (NoSuchMethodException e)
      {
         // ignore
      }
   }
   public boolean hasAssociationConstraint() { return _associationConstraint != null; }
   public boolean isQueryType()
   {
      return _associationConstraint.getReturnType().equals(QuerySpecification.class);
   }
   public AbstractListEO associationOptions(Object instance)
   {
      if ( ! AbstractListEO.class.isAssignableFrom(_associationConstraint.getReturnType()) )
      {
         throw new RuntimeException("association options method must return a jmatter list type (for association field "+this+")"); 
      }

      try
      {
         return (AbstractListEO) _associationConstraint.invoke(instance);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         throw new RuntimeException("Likely invalid method signation on options for association field "+this, e);
      }
   }
   public void bindConstraintTo(CompositeQuery query, Object instance)
   {
      if (!hasAssociationConstraint()) return;
      if (!isQueryType()) return;
      try
      {
         QuerySpecification spec = (QuerySpecification) _associationConstraint.invoke(instance);
         if (spec == null) return;
         query.addSpecification(spec);
      }
      catch (IllegalAccessException e)
      {
         // ignore.
      }
      catch (InvocationTargetException e)
      {
         e.printStackTrace();
      }
   }
   
   private void introspectAssociator()
   {
      try
      {
         String methodName = "associate" + Harvester.capitalize(name());
         _associator = _parent.getJavaClass().getMethod(methodName, _clazz);
      }
      catch (NoSuchMethodException ex)
      {
         // optional so no need to do anythin here.
      }
   }
   
   public boolean isBidirectionalRelationship()
   {
      return (_inverseFieldName != null);
   }
   public String getInverseFieldName()
   {
      if (!isBidirectionalRelationship())
         throw new IllegalArgumentException("not a bidirectional relationship");
      return _inverseFieldName;
   }
   
   public void setInverseField(Field inverseField)
   {
      _inverseField = inverseField;
   }

   /*
    * Should be called strictly from the parent ComplexEObject
    * and no one else!
    */
   public Association association(ComplexEObject parent)
   {
      if (_inverseFieldName == null)
      {
         return new Association(this, parent);
      }

      if (_inverseField == null)
      {
         _inverseField = fieldtype().field(_inverseFieldName);
         ((Bidi) _inverseField).setInverseField(this);
      }

      return new Association(this, parent, _inverseField);
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
//      Class parentClass = parent().getJavaClass();
      try
      {
//         if (!parentClass.isAssignableFrom(parent.getClass()))
//         {
//            throw new IllegalArgumentException("Invalid parent type: "+parent.getClass()+"; expected: "+parentClass);
//         }
         
         method.invoke(parent, value);
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
      Association association = association(parent);
      return vmech().getAssociationView(association);
   }
   
   public int validate(ComplexEObject parent)
   {
      Association association = association(parent);
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

   // noops..
   public void pushState(ComplexEObject parent, State state) { }
   public void popState(ComplexEObject parent) { }
   public void setStartState(ComplexEObject parent) {}

   private void checkAndSetDefault(ComplexEObject parent, ComplexEObject ceo)
   {
      if (parent.isTransientState())
      {
         ComplexEObject defaultValue = getDefaultValue();
         
         if (defaultValue == null && _specValid)
         {
            defaultValue = resolveSpec();
            _defaultValue = defaultValue;
         }
         
         if (ceo instanceof NullComplexEObject  && defaultValue != null)
         {
            association(parent).set(defaultValue);
         }
      }
   }

   public ComplexEObject getDefaultValue() { return _defaultValue; }
   public void setDefaultValue(ComplexEObject defaultValue)
   {
      ComplexEObject oldValue = _defaultValue;
      _defaultValue = defaultValue;
      firePropertyChange("defaultValue", oldValue, _defaultValue);
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
         try
         {
            return pmech2.fetch(_defaultSpec);
         }
         catch (HibernateException ex)
         {
            Tracing.tracer().warning("Is default specification: "+_defaultSpec+" valid?");
            _specValid = false;
         }
      }
      return null;
   }

}
