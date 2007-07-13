/*
 * Created on Oct 11, 2004
 */
package com.u2d.list;

import javax.swing.event.ListDataListener;
import com.u2d.element.CommandInfo;
import com.u2d.model.ComplexEObject;
import com.u2d.model.ComplexType;
import com.u2d.pubsub.*;
import static com.u2d.pubsub.AppEventType.*;
import com.u2d.find.Query;
import com.u2d.reflection.Cmd;

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

      ComplexType itemType = query.getQueryType();
      command("New").getLabel().setValue("New "+itemType.getNaturalName());
   }
   
   // See NullAssociation for comments
   @Cmd
   public ComplexEObject New(CommandInfo cmdInfo)
   {
      return New(cmdInfo, type());
   }
   @Cmd
   public ComplexEObject New(CommandInfo cmdInfo, ComplexType type)
   {
      return type.New(cmdInfo);
   }
   public ComplexType baseType()
   {
      return type().baseType();
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
