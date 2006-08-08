package com.u2d.field;

import com.u2d.model.*;
import com.u2d.list.CompositeList;
import com.u2d.pattern.Block;

import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Apr 12, 2006
 * Time: 11:07:15 AM
 */
public class CompositeIndexedField extends IndexedField
{
   public CompositeIndexedField(FieldParent parent, PropertyDescriptor descriptor)
   {
      super(parent, descriptor);
   }
   public CompositeIndexedField(FieldParent parent, String name)
         throws IntrospectionException
   {
      super(parent, name);
   }

   public boolean isComposite() { return true; }

   public EObject get(ComplexEObject parent)
   {
      final CompositeList list = (CompositeList) super.get(parent);
      list.forEach(new Block()
      {
         public void each(ComplexEObject ceo)
         {
            ceo.setField(CompositeIndexedField.this, list.parentObject());
         }
      });
         
      return list;
   }

   public int validate(ComplexEObject parent)
   {
      int count = super.validate(parent);
      count += get(parent).validate();
      return count;
   }
}
