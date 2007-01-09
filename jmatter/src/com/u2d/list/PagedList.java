/*
 * Created on Oct 11, 2004
 */
package com.u2d.list;

import javax.swing.event.ListDataListener;
import com.u2d.element.CommandInfo;
import com.u2d.element.Command;
import com.u2d.model.ComplexEObject;
import com.u2d.model.Harvester;
import com.u2d.model.ComplexType;
import com.u2d.pubsub.*;
import com.u2d.pattern.*;
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
      type().addAppEventListener("ONCREATE", _addListener);
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
        type().removeAppEventListener("ONCREATE", _addListener);
        _addListener = null;
      }
   }

   private static Onion _commands3;
   static
   {
      _commands3 = Harvester.
            simpleHarvestCommands(PagedList.class,
                                  new Onion(), false, null);
   }
   public Onion commands() { return _commands3; }
   public Command command(String commandName)
   {
      return (Command) _commands3.find(Command.finder(commandName));
   }

}
