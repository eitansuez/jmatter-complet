/*
 * Created on Apr 28, 2004
 */
package com.u2d.list;

import com.u2d.model.ComplexEObject;
import com.u2d.model.ComplexType;
import com.u2d.pubsub.*;
import static com.u2d.pubsub.AppEventType.*;
import java.util.*;
import javax.swing.event.ListDataListener;

/**
 * A list object that is not related to a model.  used for lists of eobjects
 * that might be a result of a query (carrying a result set, so to speak)
 * 
 * @author Eitan Suez
 */
public class PlainListEObject extends SimpleListEO
{

   private AppEventListener _addListener =
      new AppEventListener()
        {
           public void onEvent(AppEvent evt)
           {
              add((ComplexEObject) evt.getEventInfo());
           }
        };

   public PlainListEObject(ComplexType itemType)
   {
      super(itemType);
      itemType.addAppEventListener(CREATE, _addListener);
   }

   public PlainListEObject(Class clazz, List items)
   {
      this(clazz);
      setItems(items);
   }

   public PlainListEObject(Class clazz)
   {
      super(clazz);
   }
   
   // call to ensure/force that type is resolved and appeventlistener added
   public void resolveType() { type(); }

   // lazy derivation of type
   public ComplexType type()
   {
      if (_itemType == null)
      {
         _itemType = ComplexType.forClass(_clazz);
         _itemType.addAppEventListener(CREATE, _addListener);
      }
      return _itemType;
   }

   public void removeListDataListener(ListDataListener l)
   {
      super.removeListDataListener(l);
      if (_listDataListenerList.getListenerCount() == 0)
      {
         type().removeAppEventListener(CREATE, _addListener);
         _addListener = null;

         synchronized (this)
         {
            // remove ondelete listener from items
            ComplexEObject ceo;
            for (Iterator itr = _items.iterator(); itr.hasNext();)
            {
               ceo = (ComplexEObject) itr.next();
               ceo.removeAppEventListener(DELETE, this);
            }
         }
      }
   }

}
