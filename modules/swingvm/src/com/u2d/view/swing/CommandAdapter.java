/*
 * Created on Jan 19, 2004
 */
package com.u2d.view.swing;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import com.u2d.element.Command;
import com.u2d.element.EOCommand;
import com.u2d.view.*;
import com.u2d.type.atom.StringEO;
import com.u2d.ui.desktop.Positioning;

/**
 * Adapter for javax.swing.Action
 * 
 * @author Eitan Suez
 */
public class CommandAdapter extends AbstractAction
{
   protected Command _command;
   protected Object _value;
   protected EView _source;  // the view from which the command was invoked
   protected PropertyChangeListener changePropagator;

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
      if (_command.hasIconref())  // prefer no icon to default one for buttons and menu items.
      {
         putValue(Action.SMALL_ICON, _command.iconSm());
      }

      changePropagator = new PropertyChangeListener()
      {
         public void propertyChange(final PropertyChangeEvent evt)
         {
            if (SwingUtilities.isEventDispatchThread())
            {
               firePropertyChange("enabled", evt.getOldValue(), evt.getNewValue());
            }
            else
            {
               SwingUtilities.invokeLater(new Runnable() {
                  public void run()
                  {
                     firePropertyChange("enabled", evt.getOldValue(), evt.getNewValue());
                  }
               });
            }
         }
      };
      _command.addPropertyChangeListener("enabled", changePropagator);

   }

   public boolean isEnabled() { return _command.isEnabled(); }
   // to setEnabled, call command.setEnabled() directly.


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

   public void detach()
   {
      _value = null;
      _command.removePropertyChangeListener("enabled", changePropagator);
      changePropagator = null;
      _source = null;
   }

   public void actionPerformed(ActionEvent evt)
   {
      final JComponent sourceComp = (JComponent) _source;
      if (_command.blocks())
      {
         sourceComp.setEnabled(false);
      }

      if ((_command instanceof EOCommand) && ((EOCommand) _command).returnsAView())
      {
         executeCommand();
         if (_command.blocks())
         {
            sourceComp.setEnabled(true);  // if the view has closed or detached, then still have a ref to source
              // to re-enable it
         }
      }
      else
      {
         SwingViewMechanism.invokeSwingAction(new SwingAction()
         {
            public void offEDT()
            {
               executeCommand();
            }
            public void backOnEDT()
            {
               if (_command.blocks())
               {
                  sourceComp.setEnabled(true);  // if the view has closed or detached, then still have a ref to source
                    // to re-enable it
               }
            }
         });
      }
   }

   private void executeCommand()
   {
      try
      {
         //System.out.println("cmdAdapter:: executing command, passing source: "+_source);
         _command.execute(_value, _source);
      }
      catch (java.lang.reflect.InvocationTargetException ex)
      {
         SwingViewMechanism.getInstance().displayViewFor(ex.getCause(), _source, Positioning.NEARMOUSE);
      }
   }

}
