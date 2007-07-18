/*
 * Created on Oct 11, 2004
 */
package com.u2d.list;

import javax.swing.event.ListDataListener;
import com.u2d.model.ComplexEObject;
import com.u2d.pubsub.*;
import static com.u2d.pubsub.AppEventType.*;
import com.u2d.find.Query;

/**
 * @author Eitan Suez
 */
public class PagedList extends CriteriaListEO
{
   private AppEventListener _addListener =
     new AppEventListener()
       {
          public void onEvent(AppEvent evt)
          {
             add((ComplexEObject) evt.getEventInfo());
          }
       };

   public PagedList(Query query)
   {
      this(query, 1);
   }
   public PagedList(Query query, int pageNum)
   {
      super(query, pageNum);
      type().addAppEventListener(CREATE, _addListener);
   }

   public void removeListDataListener(ListDataListener l)
   {
      super.removeListDataListener(l);
      if (_listDataListenerList.getListenerCount() == 0)
      {
        type().removeAppEventListener(CREATE, _addListener);
        _addListener = null;
      }
   }

}
