package com.u2d.model;

import com.u2d.element.Command;
import com.u2d.pattern.Onion;
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
   private static transient Map<Class, ListType> _listtypeMap = new HashMap<Class, ListType>();

   public static ListType forClass(Class targetClass)
   {
      if (!(AbstractListEO.class.isAssignableFrom(targetClass)))
      {
         throw new RuntimeException("Cannot create List Type for "+targetClass.getName());
      }

      if (_listtypeMap.get(targetClass) == null)
         _listtypeMap.put(targetClass, new ListType(targetClass));

      return (ListType) _listtypeMap.get(targetClass);
   }
   
   private Onion _commands;
   
   private ListType(Class typeClass)
   {
      _commands = Harvester.simpleHarvestCommands(typeClass, new Onion(), false, null);
   }
   
   public Onion commands() { return _commands; }
   public Command command(String commandName)
   {
      return (Command) _commands.find(Command.finder(commandName));
   }
}
