/*
 * Created on May 1, 2004
 */
package com.u2d.field;

import com.u2d.element.Field;
import com.u2d.model.ComplexEObject;
import com.u2d.model.AbstractListEO;
import com.u2d.pubsub.*;
import com.u2d.app.Context;

import java.util.List;

/**
 * @author Eitan Suez
 */
public class BidiAssociation extends Association
{
   private Field _otherSide;

   public BidiAssociation(Field field, ComplexEObject parent, Field otherSide)
   {
      super(field, parent);
      _otherSide = otherSide;
   }

   // this is interesting.  if both sides are in the same state (transient or read)
   // then set both sides of relationship immediately.
   // however, if either side is transient and the other is not, the setting of one 
   // persistent side of the relationship must be delayed until after the other side
   // has been saved
   public void set(final ComplexEObject value)
   {
      if (value != null && value.isTransientState())
      {
         _otherSide.set(value, _parent);

         value.addAppEventListener("ONCREATE", new AppEventListener()
            {
               public void onEvent(AppEvent evt)
               {
                  BidiAssociation.super.set(value);
               }
            });
      }
      else if (_parent.isTransientState())
      {
         _parent.addAppEventListener("ONCREATE", new AppEventListener()
            {
               public void onEvent(AppEvent evt)
               {
                  // important!: make sure that association _still_ holds
                  // (could have changed (user picking a different value
                  //  prior to clicking 'save')
                  if (get() == value)
                  {
                     _otherSide.set(value, _parent);
                  }
               }
            });
         super.set(value);
      }
      else
      {
         _otherSide.set(value, _parent);
         super.set(value);
      }
   }

   public void associateList(final List value)
   {
      ComplexEObject item;
      for (int i=0; i<value.size(); i++)
      {
         item = (ComplexEObject) value.get(i);
         _otherSide.set(item, _parent);
      }
      _field.set(_parent, value);
      if (!_parent.isEditableState()) _parent.save();
   }

   public void dissociate()
   {
      if (_otherSide.isIndexed())
      {
         ComplexEObject dissociateValue = get();
         AbstractListEO list = (AbstractListEO) _otherSide.get(dissociateValue);
         list.remove(dissociateValue);
         super.set(null);
      }
      else
      {
         super.dissociate();
      }
   }

   // lots of work to do here to deal with 1-many relationships
   public void dissociateItem(ComplexEObject eo)
   {
      AbstractListEO list = (AbstractListEO) _field.get(_parent);
      list.remove(eo);
      _otherSide.set(eo, null);
      Context.getInstance().getPersistenceMechanism().
            updateAssociation(eo, _parent);
   }

}