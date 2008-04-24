package com.u2d.view.swing;

import com.u2d.view.ComplexEView;
import com.u2d.view.EView;
import com.u2d.view.swing.list.CommandsContextMenuView;
import com.u2d.element.Command;
import com.u2d.model.EObject;
import com.u2d.ui.LockedButton;
import com.u2d.ui.DefaultButton;
import com.u2d.ui.NormalButton;
import com.u2d.css4swing.style.ComponentStyle;

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
   private transient boolean _isDefault;
   
   public CommandButton(Command cmd, EObject eo, EView source, boolean defaultBtn)
   {
      _cmd = cmd;
      _isDefault = defaultBtn;
      
      setLayout(new BorderLayout());
      
      _cmdAdapter = new CommandAdapter(cmd, eo, source);
      setupBtn();
      
      _cmd.getMnemonic().addChangeListener(this);
      _cmd.getDescription().addChangeListener(this);
      _cmd.getLabel().addChangeListener(this);
      _cmd.getSensitive().addChangeListener(this);
   }
   
   private void setupBtn()
   {
      JButton btn;
      if (_cmd.sensitive())
      {
         btn = new LockedButton(_cmdAdapter);
      }
      else if (_isDefault)
      {
         btn = new DefaultButton(_cmdAdapter);
      }
      else
      {
         btn = new NormalButton(_cmdAdapter);
      }
      ComponentStyle.addClass(btn, "command");
      
      _cmdsView.detach();
      _cmdsView.bind(_cmd, btn, this);
      add(btn, BorderLayout.CENTER);
   }

   public EObject getEObject() { return _cmd; }
   public void detach()
   {
      _cmdAdapter.detach();
      _cmd.getMnemonic().removeChangeListener(this);
      _cmd.getDescription().removeChangeListener(this);
      _cmd.getLabel().removeChangeListener(this);
      _cmd.getSensitive().removeChangeListener(this);
      _cmdsView.detach();
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
            else if (e.getSource().equals(_cmd.getSensitive()))
            {
               remove(0);  // replace button..
               setupBtn();
            }
            revalidate(); repaint();
         }
      });
   }
   
}
