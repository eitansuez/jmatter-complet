package com.u2d.view.swing;

import com.u2d.view.ComplexEView;
import com.u2d.model.EObject;
import com.u2d.model.ComplexType;
import com.u2d.ui.UIUtils;
import com.u2d.interaction.Instruction;
import com.u2d.element.Command;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Dec 26, 2007
 * Time: 10:05:28 PM
 */
public class InstructionView extends JPanel
      implements ComplexEView
{
   private Instruction _instruction;
   
   private SimpleAssociationView _targetView, _cmdView;
   private Timer _dismissTimer;
   private final static String TIP_START_TEXT = "Type to start matching a type";
   private JLabel _tipLabel = new JLabel();

   public InstructionView(Instruction instruction)
   {
      _instruction = instruction;
      
      configureTimer();
      configureView();
      
      _instruction.getActive().addChangeListener(new ChangeListener() {
         public void stateChanged(ChangeEvent e)
         {
            SwingUtilities.invokeLater(new Runnable() {
               public void run()
               {
                  setVisibility();
               }
            });
         }
      });
   }
   
   private void configureTimer()
   {
      _dismissTimer = new Timer(3000, new ActionListener() {
         public void actionPerformed(ActionEvent e)
         {
            new Thread() {
               public void run()
               {
                  _instruction.deactivate();
               }
            }.start();
         }
      });
      _dismissTimer.setRepeats(false);
      _dismissTimer.setCoalesce(true);
   }
   
   private void configureView()
   {
      _targetView = new SimpleAssociationView(_instruction, "target");
      _cmdView = new SimpleAssociationView(_instruction, "action");
      
      _instruction.addPropertyChangeListener("target", new PropertyChangeListener()
      {
         public void propertyChange(PropertyChangeEvent evt)
         {
            _cmdView.clear();
         }
      });
      _instruction.addPropertyChangeListener("action", new PropertyChangeListener()
      {
         public void propertyChange(PropertyChangeEvent evt)
         {
            Command cmd = _instruction.getAction();
            if (cmd == null || cmd.parent() == null) return;
            if (!cmd.getDescription().isEmpty())
            {
               _tipLabel.setText(cmd.description());
               return;
            }
            
            String parentPluralName = ((ComplexType) cmd.parent()).getPluralName();
            String helpText = cmd.title().append(parentPluralName).toString();
            _tipLabel.setText(helpText);
         }
      });
         

      FormLayout layout = new FormLayout("180px, 180px", "fill:180px, pref");
      CellConstraints cc = new CellConstraints();
      setLayout(layout);
      
      add(_targetView, cc.rc(1, 1));
      add(_cmdView, cc.rc(1, 2));
      _tipLabel.setText(TIP_START_TEXT);
      add(_tipLabel, cc.rcw(2, 1, 2));
      
      setVisibility();
   }
   
   
   private void setVisibility()
   {
      if (_instruction.active())
      {
         setSize(getPreferredSize());
         setLocation(UIUtils.computeCenter(this.getParent(), this));
         SwingUtilities.invokeLater(new Runnable() {
            public void run()
            {
               _targetView.focus();
            }
         });
//         _dismissTimer.start();
      }
      else
      {
         _targetView.clear();
         _instruction.clear();
         _tipLabel.setText(TIP_START_TEXT);
      }
      setVisible(_instruction.active());
   }

   public EObject getEObject() { return _instruction; }
   public void detach() { }
   public void stateChanged(ChangeEvent e) { }

   public void propertyChange(PropertyChangeEvent evt) { }
   public boolean isMinimized() { return false; }
}
