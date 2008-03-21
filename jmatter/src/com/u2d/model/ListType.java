package com.u2d.model;

import com.u2d.element.Command;
import com.u2d.pattern.Onion;
import com.u2d.list.RelationalList;
import com.u2d.list.CompositeList;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jun 12, 2007
 * Time: 10:11:46 PM
 */
public class ListType
{
   // for lists, keep a unique set of commands per listtype, per item type.
   // this allows me, for example, to exclude a New command on lists of ComplexTypes, which should not be instantiated
   // or for example to add a BrowseInCalendar command to lists of calendarable types..
   
   private static transient Map<Class, Map<Class, ListType>> _listtypeMap = 
         new HashMap<Class, Map<Class, ListType>>();
   
   public static synchronized ListType forClass(Class listClass, Class itemClass)
   {
      if (!(AbstractListEO.class.isAssignableFrom(listClass)))
      {
         throw new RuntimeException("Cannot create List Type for "+listClass.getName());
      }

      if (!_listtypeMap.containsKey(listClass))
      {
         _listtypeMap.put(listClass, new HashMap<Class, ListType>());
      }

      Map<Class, ListType> typeMap = _listtypeMap.get(listClass);
      if (!typeMap.containsKey(itemClass))
      {
         typeMap.put(itemClass, new ListType(listClass, itemClass));
      }
      
      return typeMap.get(itemClass);
   }
   
   private Onion _commands;
   
   private ListType(Class listClass, Class itemClass)
   {
      _commands = Harvester.simpleHarvestCommands(listClass, new Onion(), false, null);
      
      ComplexType itemType = ComplexType.forClass(itemClass);
      _commands.wrap(itemType.listCommands());
      
      // exclude RelationalList and CompositeList
      if (listClass == RelationalList.class || listClass == CompositeList.class)
         return;

      if (itemClass != ComplexType.class) // no dynamic type creation at runtime! :-)
      {
         // Add type.new command
         Command newCmd = itemType.command("New");
         newCmd.getLabel().setValue("New "+itemType.getNaturalName());
         _commands.add(newCmd);
      }
      
   }
   
   public Onion commands() { return _commands; }
   public Command command(String commandName)
   {
      return (Command) _commands.find(Command.finder(commandName));
   }
}
