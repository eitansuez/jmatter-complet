/*
 * Created on Jan 30, 2004
 */
package com.u2d.model;

import java.util.Iterator;
import javax.swing.*;
import com.u2d.element.Command;
import com.u2d.element.CommandInfo;
import com.u2d.pattern.Onion;
import com.u2d.view.*;
import com.u2d.reflection.Cmd;

/**
 * @author Eitan Suez
 */
public class NullComplexEObject extends AbstractComplexEObject
{
   protected Icon _iconSm, _iconLg;

   public NullComplexEObject(ComplexType type)
   {
      _type = type;
      propagateCmdRestrictions();
   }

   private void propagateCmdRestrictions()
   {
      for (Iterator itr = _commands.deepIterator(); itr.hasNext(); )
      {
         Command command = (Command) itr.next();
         Command typeCommand = _type.command(command.name());
         if (typeCommand != null)  // TODO:  revisit  // related to removing typecommand New from ComplexType.
         {
            command.applyRestriction(typeCommand.restriction());
         }
      }
   }

   protected Icon disabledIcon(Icon icon)
   {
      java.awt.Image grayImage =
         GrayFilter.createDisabledImage(((ImageIcon) icon).getImage());
      return new ImageIcon(grayImage);
   }

   public synchronized javax.swing.Icon iconSm()
   {
      if (_iconSm == null)
      {
         _iconSm = disabledIcon(_type.iconSm());
      }
      return _iconSm;
   }

   public synchronized javax.swing.Icon iconLg()
   {
      if (_iconLg == null)
      {
         _iconLg = disabledIcon(_type.iconLg());
      }
      return _iconLg;
   }

   public Title title() { return new Title("["+_type.getNaturalName()+"]"); }
   public boolean isEmpty() { return true; }

   public EView getView() { return getListItemView(); }


   protected static Onion _commands;
   static
   {
      _commands = Harvester.simpleHarvestCommands(NullComplexEObject.class,
                                                  new Onion(), false, null);
   }
   public Onion commands() { return _commands; }
   public Command command(String commandName)
   {
      return (Command) commands().find(Command.finder(commandName));
   }


   @Cmd
   public ComplexEObject New(CommandInfo cmdInfo)
   {
      return _type.New(cmdInfo);
   }
   @Cmd
   public AbstractListEO Browse(CommandInfo cmdInfo)
   {
      return _type.Browse(cmdInfo);
   }
   @Cmd
   public View Find(CommandInfo cmdInfo)
   {
      return _type.Find(cmdInfo);
   }

   public String defaultCommandName() { return "Browse"; }

   public boolean equals(Object obj)
   {
      if (obj == null) return true;
      if (obj == this) return true;
      if (!(obj instanceof NullComplexEObject)) return false;
      NullComplexEObject nceo = (NullComplexEObject) obj;
      return _type.equals(nceo.type());
   }

   public int hashCode()
   {
      return _type.hashCode();
   }
}
