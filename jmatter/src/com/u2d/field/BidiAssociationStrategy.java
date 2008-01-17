/*
 * Created on May 1, 2004
 */
package com.u2d.field;

import com.u2d.element.Field;
import com.u2d.model.ComplexEObject;
import com.u2d.model.AbstractListEO;
import com.u2d.pubsub.*;
import static com.u2d.pubsub.AppEventType.*;
import com.u2d.app.Context;
import java.util.List;

/**
 * @author Eitan Suez
 */
public class BidiAssociationStrategy
      extends BasicAssociationStrategy
{
   private Field _otherSide;

   public BidiAssociationStrategy(Field field, ComplexEObject parent, Field otherSide)
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
      if (value != null && value.isEditableState())
      {
         _otherSide.set(value, parent());

         AppEventType signal = (value.isTransientState() ? BEFORECREATE : BEFORESAVE);
         value.addAppEventListener(signal, new AppEventListener()
            {
               public void onEvent(AppEvent evt)
               {
                  BidiAssociationStrategy.super.set(value);
               }
            });
      }
      else if (parent().isEditableState())
      {
         AppEventType signal = (parent().isTransientState() ? BEFORECREATE : BEFORESAVE);
         parent().addAppEventListener(signal, new AppEventListener()
            {
               public void onEvent(AppEvent evt)
               {
                  // important!: make sure that association _still_ holds
                  // (could have changed (user picking a different value
                  //  prior to clicking 'save')
                  if (get() == value)
                  {
                     _otherSide.set(value, parent());
                  }
               }
            });
         super.set(value);
      }
      else
      {
         _otherSide.set(value, parent());
         super.set(value);
      }
   }

   public void associateList(final List value)
   {
      ComplexEObject item;
      for (int i=0; i<value.size(); i++)
      {
         item = (ComplexEObject) value.get(i);
         _otherSide.set(item, parent());
      }
      field().set(parent(), value);
      if (!parent().isEditableState()) parent().save();
   }

   public void dissociate()
   {
      if (_otherSide.isIndexed())
      {
         ComplexEObject dissociateValue = get();
         AbstractListEO list = (AbstractListEO) _otherSide.get(dissociateValue);
         list.remove(_parent);
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
      AbstractListEO list = (AbstractListEO) field().get(parent());
      list.remove(eo);
      
      if (_otherSide instanceof IndexedField)
      {  // many to many situation
         IndexedField otherListField = (IndexedField) _otherSide;
         AbstractListEO otherList = (AbstractListEO) otherListField.get(eo);
         otherList.remove(parent());
      }
      else
      {
         _otherSide.set(eo, null);
      }

      Context.getInstance().getPersistenceMechanism().
            updateAssociation(eo, parent());
   }

}