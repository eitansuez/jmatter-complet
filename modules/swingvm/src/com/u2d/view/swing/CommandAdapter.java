/*
 * Created on Jan 19, 2004
 */
package com.u2d.view.swing;

import javax.swing.*;
import java.awt.event.ActionEvent;
import com.u2d.element.Command;
import com.u2d.view.*;
import com.u2d.type.atom.StringEO;
import com.u2d.model.IconLoader;

/**
 * Adapter for javax.swing.Action
 * 
 * @author Eitan Suez
 */
public class CommandAdapter extends AbstractAction
{
   private Command _command;
   private Object _value;
   private EView _source;  // the view from which the command was invoked

   public CommandAdapter(Command command, EView source)
   {
      if (command == null)
         throw new IllegalArgumentException("CommandAdapter cannot be constructed for a Null command");

      _command = command;
      _source = source;

      updateCaption();
      putValue(Action.ACTION_COMMAND_KEY, _command.name());
      updateMnemonic();
      updateDescription();
      if (_command.hasIconref())
      {
         putValue(Action.SMALL_ICON, IconLoader.cmdIcon(_command));
      }
   }
   
   public void updateCaption()
   {
      putValue(Action.NAME, _command.label());
   }
   public void updateMnemonic()
   {
      char mnemonic = Character.toUpperCase(_command.mnemonic());  // (*)
      putValue(Action.MNEMONIC_KEY, new Integer(mnemonic));
   }
   public void updateDescription()
   {
      if (!StringEO.isEmpty(_command.description()))
         putValue(Action.SHORT_DESCRIPTION, _command.description());
   }
   
   
   /*  (*)
    * Note: when dealing with Swing: JButtons bound to swing Action's
    * 
    * Then the mnemonic is set via a call to putValue() which takes
    * as an argument, the Integer value of the mnemonic character.
    * 
    * I have verified that there's a bug in java where the integer value
    * must be the integer code of the upper case version of the mnemonic.
    * Otherwise, invoking the mnemonic won't work (although it will display
    * correctly).
    * 
    * This should explain the referenced implementation. 
    */
   
   
   public CommandAdapter(Command command, Object value, EView source)
   {
      this(command, source);
      attach(value);
   }

   public void attach(Object value) { _value = value; }
   public void detach() { _value = null; }

   public void actionPerformed(ActionEvent evt)
   {
      if (_command.blocks())
      {
         ((JComponent) _source).setEnabled(false);
      }
      SwingViewMechanism.invokeSwingAction(new SwingAction()
      {
         public void offEDT()
         {
            try
            {
               //System.out.println("cmdAdapter:: executing command, passing source: "+_source);
               _command.execute(_value, _source);
            }
            catch (java.lang.reflect.InvocationTargetException ex)
            {
               SwingViewMechanism.getInstance().displayFrame(new ExceptionFrame(ex));
            }
         }
         public void backOnEDT()
         {
            if (_command.blocks())
            {
               ((JComponent) _source).setEnabled(true);
            }
         }
      });
   }

}
