package com.u2d.model;

import com.u2d.element.Command;
import com.u2d.pattern.Onion;
import com.u2d.list.RelationalList;
import com.u2d.list.CompositeList;

import javax.swing.*;
import java.util.Map;
import java.util.HashMap;
import java.awt.image.BufferedImage;
import java.awt.*;

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

      loadIcons(itemType);
   }
   
   public Onion commands() { return _commands; }
   public Command command(String commandName)
   {
      return (Command) _commands.find(Command.finder(commandName));
   }


   protected Icon _iconSm, _iconLg;
   protected String _iconSmResourceRef, _iconLgResourceRef;

   private void loadIcons(ComplexType itemType)
   {
      _iconLgResourceRef = IconResolver.pluralIconRef(itemType, "32");
      _iconSmResourceRef = IconResolver.pluralIconRef(itemType, "16");
      _iconLg = IconLoader.loadIcon(_iconLgResourceRef);

      // customize lg icon for lists if no custom image is provided:
      if (_iconLgResourceRef.endsWith("list32.png"))
      {
         BufferedImage bi = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
         Graphics g = bi.getGraphics();
         g.drawImage(((ImageIcon) _iconLg).getImage(), 0, 0, null);
         g.drawImage(((ImageIcon) itemType.iconSm()).getImage(), 16, 16, null);
         _iconLg = new ImageIcon(bi);
      }

      _iconSm = IconLoader.loadIcon(_iconSmResourceRef);
   }

   public Icon iconSm() { return _iconSm; }
   public Icon iconLg() { return _iconLg; }

   public String iconSmResourceRef() { return _iconSmResourceRef; }
   public String iconLgResourceRef() { return _iconLgResourceRef; }

}
