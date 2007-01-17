/*
 * Created on Apr 28, 2004
 */
package com.u2d.list;

import com.u2d.element.CommandInfo;
import com.u2d.element.Command;
import com.u2d.model.ComplexEObject;
import com.u2d.model.ComplexType;
import com.u2d.model.Harvester;
import com.u2d.pubsub.*;
import java.util.*;
import javax.swing.event.ListDataListener;
import com.u2d.pattern.*;
import com.u2d.reflection.Cmd;

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
      itemType.addAppEventListener("ONCREATE", _addListener);
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
         _itemType.addAppEventListener("ONCREATE", _addListener);
      }
      return _itemType;
   }

   @Cmd
   public ComplexEObject New(CommandInfo cmdInfo)
   {
      return type().New(cmdInfo);
   }

   public void removeListDataListener(ListDataListener l)
   {
      super.removeListDataListener(l);
      if (_listDataListenerList.getListenerCount() == 0)
      {
        type().removeAppEventListener("ONCREATE", _addListener);
        _addListener = null;

        // remove ondelete listener from items
        ComplexEObject ceo = null;
        for (Iterator itr = _items.iterator(); itr.hasNext(); )
        {
           ceo = (ComplexEObject) itr.next();
           ceo.removeAppEventListener("ONDELETE", this);
        }
      }
   }

   private static Onion _commands2;
   static
   {
      _commands2 = Harvester.simpleHarvestCommands(PlainListEObject.class,
                                                   new Onion(), false, null);
   }
   public Onion commands() { return _commands2; }
   public Command command(String commandName)
   {
      return (Command) _commands2.find(Command.finder(commandName));
   }
   
}
