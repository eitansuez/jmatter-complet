package com.u2d.view.swing;

import com.u2d.view.ComplexEView;
import com.u2d.model.EObject;
import com.u2d.model.ComplexType;
import com.u2d.ui.UIUtils;
import com.u2d.interaction.Instruction;
import com.u2d.element.Command;
import javax.swing.*;
import javax.swing.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import net.miginfocom.swing.MigLayout;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Dec 26, 2007
 * Time: 10:05:28 PM
 */
public class InstructionView extends JPanel implements ComplexEView
{
   private Instruction _instruction;
   
   private SimpleAssociationView _targetView, _cmdView;
   private final static String TIP_START_TEXT = "Type to start matching a type";
   private JLabel _tipLabel = new JLabel();
   private ChangeListener _activeChangeListener;
   private PropertyChangeListener targetChangeListener;
   private PropertyChangeListener actionChangeListener;

   public InstructionView()
   {
      configureView();
      
      _activeChangeListener = new ChangeListener() {
         public void stateChanged(ChangeEvent e)
         {
            SwingUtilities.invokeLater(new Runnable() {
               public void run()
               {
                  setVisibility();
               }
            });
         }
      };
   }
   public InstructionView(Instruction instruction)
   {
      this();
      bind(instruction);
   }
   
   public void bind(Instruction instruction)
   {
      if (_instruction != null)
      {
         detach();
      }
      
      _instruction = instruction;
      _instruction.getActive().addChangeListener(_activeChangeListener);
      _targetView.bind(_instruction);
      _cmdView.bind(_instruction);
      _instruction.addPropertyChangeListener("target", targetChangeListener);
      _instruction.addPropertyChangeListener("action", actionChangeListener);
      setVisibility();
   }
   public void detach()
   {
      _instruction.getActive().removeChangeListener(_activeChangeListener);
      _instruction.removePropertyChangeListener("target", targetChangeListener);
      _instruction.removePropertyChangeListener("action", actionChangeListener);
      _targetView.detach();
      _cmdView.detach();
   }
   
   private void configureView()
   {
      _targetView = new SimpleAssociationView("target");
      _cmdView = new SimpleAssociationView("action");

      targetChangeListener = new PropertyChangeListener()
      {
         public void propertyChange(PropertyChangeEvent evt)
         {
            _cmdView.clear();
         }
      };
      actionChangeListener = new PropertyChangeListener()
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
      };

      MigLayout layout = new MigLayout("fill, wrap 2", "[180][180]", "[180][pref]");
      setLayout(layout);
      
      add(_targetView, "grow");
      add(_cmdView, "grow");
      _tipLabel.setText(TIP_START_TEXT);
      add(_tipLabel, "span");
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
   public void stateChanged(ChangeEvent e) { }

   public void propertyChange(PropertyChangeEvent evt) { }
   public boolean isMinimized() { return false; }
}
