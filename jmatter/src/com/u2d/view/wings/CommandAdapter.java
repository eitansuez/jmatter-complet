package com.u2d.view.wings;

import com.u2d.element.Command;
import com.u2d.view.EView;
import javax.swing.Action;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;

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

      putValue(Action.NAME, _command.label());
      putValue(Action.ACTION_COMMAND_KEY, _command.name());
      putValue(Action.MNEMONIC_KEY, new Integer(_command.mnemonic()));
   }

   public CommandAdapter(Command command, Object value, EView source)
   {
      this(command, source);
      attach(value);
   }

   public void attach(Object value) { _value = value; }
   public void detach() { _value = null; }

   public void actionPerformed(ActionEvent evt)
   {
      try
      {
         //System.out.println("cmdAdapter:: executing command, passing source: "+_source);
         _command.execute(_value, _source);
      }
      catch (java.lang.reflect.InvocationTargetException ex)
      {
//         SwingViewMechanism.getInstance().displayFrame(new ExceptionFrame(ex));
      }
   }

}
