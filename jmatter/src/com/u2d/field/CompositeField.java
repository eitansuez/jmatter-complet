/*
 * Created on Mar 2, 2004
 */
package com.u2d.field;

import java.beans.*;
import com.u2d.element.Field;
import com.u2d.model.*;

/**
 * @author Eitan Suez
 */
public abstract class CompositeField extends Field
{
   public CompositeField() {}
   
   public CompositeField(FieldParent parent, PropertyDescriptor descriptor) 
   {
      super(parent, descriptor);
   }

   public CompositeField(FieldParent parent, String name) throws IntrospectionException
   {
      init(parent, name);
   }

   protected void init(FieldParent parent, String name) throws IntrospectionException
   {
      String getterName = Harvester.makeGetterName(name);
      PropertyDescriptor descriptor = new PropertyDescriptor(name, parent.getJavaClass(),
            getterName, null);
      super.init(parent, descriptor);
   }


   public EObject get(ComplexEObject parent)
   {
      EObject eo = (EObject) reflectGet(parent);
      if (eo == null)
      {
         throw new RuntimeException(
               "get() returned null on a composite! check your code;  field is: " + 
               this + "; parent is "+parent);
      }
      eo.setField(this, parent);
      return eo;
   }
   
   public Object reflectGet(EObject ancestor)
   {
      Class parentClass = parent().getJavaClass();
      if (parentClass.isAssignableFrom(ancestor.getClass()))
      {
         return super.reflectGet(ancestor);
      }
      
      if (parent() instanceof Field)
      {
         Field parentField = (Field) parent();
         EObject directParent = (EObject) parentField.reflectGet(ancestor);
         return super.reflectGet(directParent);
      }
      else
      {
         return super.reflectGet(ancestor);
//         throw new IllegalArgumentException("Invalid parent type: "+ancestor.getClass()+"; expected: "+parentClass);
      }
   }

   public void set(ComplexEObject parent, Object value)
   {
      EObject eo = get(parent);
      eo.setValue((EObject) value);
   }
   
   public boolean _identity = false;
   public boolean isIdentity() { return _identity; }
   public void setIdentity()
   {
      _identity = true;
      ((ComplexEObject) parent()).type().addIdentityField(this);
      // by default identity fields should also be required:
      getRequired().setValue(true);
   }
   
   /*
    * restrictedit is layered restriction system on top of default
    * definitions of fields as readonly or not.  need to be able to apply
    * or lift layer.  that's why a separate variable.  rest is implementation
    * making sure to take into account parent aggregate field imposing its
    * readonly state on its children.
    */
   public boolean isReadOnly()
   {
      if (_parent instanceof CompositeField)
      {
         CompositeField parentField = (CompositeField) _parent;
         return _readOnly || parentField.isReadOnly() || restrictReadOnly();
      }
      return _readOnly || restrictReadOnly();
   }

}
