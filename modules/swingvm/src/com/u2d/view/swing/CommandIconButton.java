package com.u2d.view.swing;

import com.u2d.view.ComplexEView;
import com.u2d.view.EView;
import com.u2d.element.Command;
import com.u2d.model.EObject;
import com.u2d.css4swing.style.ComponentStyle;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.beans.PropertyChangeEvent;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Nov 13, 2006
 * Time: 2:02:03 PM
 */
public class CommandIconButton extends JButton implements ComplexEView
{
   private Command _cmd;
   private transient CommandAdapter _cmdAdapter;

   public CommandIconButton(Command cmd, EObject eo, EView source)
   {
      _cmd = cmd;
      _cmdAdapter = new CommandAdapter(cmd, eo, source);
//      _cmdAdapter.putValue(Action.LARGE_ICON_KEY, cmd.iconLg());
      setAction(_cmdAdapter);

//      setVerticalTextPosition(SwingConstants.BOTTOM);
//      setHorizontalTextPosition(SwingConstants.CENTER);
      setVerticalTextPosition(SwingConstants.CENTER);
      setHorizontalTextPosition(SwingConstants.RIGHT);

      ComponentStyle.addClass(this, "command");

      _cmd.getMnemonic().addChangeListener(this);
      _cmd.getDescription().addChangeListener(this);
      _cmd.getLabel().addChangeListener(this);
   }

   public EObject getEObject() { return _cmd; }
   public void detach()
   {
      _cmd.getMnemonic().removeChangeListener(this);
      _cmd.getDescription().removeChangeListener(this);
      _cmd.getLabel().removeChangeListener(this);
   }

   public boolean isMinimized() { return true; }

   public void propertyChange(PropertyChangeEvent evt) { }

   public void stateChanged(final ChangeEvent e)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            if (e.getSource().equals(_cmd.getMnemonic()))
            {
               _cmdAdapter.updateMnemonic();
            }
            else if (e.getSource().equals(_cmd.getDescription()))
            {
               _cmdAdapter.updateDescription();
            }
            else if (e.getSource().equals(_cmd.getLabel()))
            {
               _cmdAdapter.updateCaption();
            }
            revalidate(); repaint();
         }
      });
   }
}