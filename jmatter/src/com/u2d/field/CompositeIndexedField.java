package com.u2d.field;

import com.u2d.list.CompositeList;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.model.FieldParent;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Apr 12, 2006
 * Time: 11:07:15 AM
 */
public class CompositeIndexedField extends IndexedField
{
   public CompositeIndexedField() {}
   
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
      for (Iterator itr = list.iterator(); itr.hasNext(); )
      {
         ComplexEObject ceo = (ComplexEObject) itr.next();
         ceo.setField(CompositeIndexedField.this, list.parentObject());
      }
      return list;
   }

   public int validate(ComplexEObject parent)
   {
      int count = super.validate(parent);
      count += get(parent).validate();
      return count;
   }


   public void restore(ComplexEObject parent, Object value)
   {
      super.restore(parent, value);
      final CompositeList list = (CompositeList) super.get(parent);
      for (Iterator itr = list.iterator(); itr.hasNext(); )
      {
         ComplexEObject ceo = (ComplexEObject) itr.next();
         list.setParent(ceo);
      }
   }
}
