package com.u2d.view.swing;

import com.u2d.view.ComplexEView;
import com.u2d.view.EView;
import com.u2d.view.swing.list.CommandsContextMenuView;
import com.u2d.element.Command;
import com.u2d.model.EObject;
import com.u2d.ui.LockedButton;
import com.u2d.ui.DefaultButton;
import com.u2d.ui.NormalButton;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.beans.PropertyChangeEvent;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Nov 13, 2006
 * Time: 2:02:03 PM
 */
public class CommandButton extends JPanel
      implements ComplexEView
{
   private Command _cmd;
   private transient CommandsContextMenuView _cmdsView = new CommandsContextMenuView();
   private transient CommandAdapter _cmdAdapter;
   
   public CommandButton(Command cmd, EObject eo, EView source, boolean defaultBtn)
   {
      _cmd = cmd;
      
      setLayout(new BorderLayout());
      
      _cmdAdapter = new CommandAdapter(cmd, eo, source);
      JButton btn;
      if (cmd.isSensitive())
      {
         btn = new LockedButton(_cmdAdapter);
      }
      else if (defaultBtn)
      {
         btn = new DefaultButton(_cmdAdapter);
      }
      else
      {
         btn = new NormalButton(_cmdAdapter);
      }
      
      _cmdsView.bind(_cmd, btn, this);
      add(btn, BorderLayout.CENTER);
      
      _cmd.getMnemonic().addChangeListener(this);
      _cmd.getDescription().addChangeListener(this);
      _cmd.getLabel().addChangeListener(this);
   }

   public EObject getEObject() { return _cmd; }
   public void detach()
   {
   }

   public boolean isMinimized() { return true; }

   public void propertyChange(PropertyChangeEvent evt)
   {
   }
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
