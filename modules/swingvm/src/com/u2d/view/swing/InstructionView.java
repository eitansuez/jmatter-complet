package com.u2d.view.swing;

import com.u2d.view.ComplexEView;
import com.u2d.view.swing.list.JListView;
import com.u2d.view.swing.list.JListSelectionView;
import com.u2d.model.EObject;
import com.u2d.model.ComplexEObject;
import com.u2d.ui.UIUtils;
import com.u2d.ui.KeyPressAdapter;
import com.u2d.field.Association;
import com.u2d.interaction.Instruction;
import com.u2d.element.Command;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.beans.PropertyChangeEvent;
import org.jdesktop.swingx.JXPanel;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Dec 26, 2007
 * Time: 10:05:28 PM
 */
public class InstructionView extends JXPanel
      implements ComplexEView
{
   private Instruction _instruction;
   private Timer _dismissTimer;
   private JTextField _tf;
   private SimpleAssociationView _targetView, _cmdView;

   public InstructionView(Instruction instruction)
   {
      _instruction = instruction;
      configure();
   }
   
   private void configure()
   {
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
      Association targetAssociation = _instruction.association("target");
      _targetView = new SimpleAssociationView(targetAssociation);

      Association cmdAssociation = _instruction.association("action");
      _cmdView = new SimpleAssociationView(cmdAssociation);

      setupTextfield();
      setupPopup();
      
      FormLayout layout = new FormLayout("120px, 120px", "top:120px, pref");
      CellConstraints cc = new CellConstraints();
      setLayout(layout);
      
      add(_targetView, cc.rc(1, 1));
      add(_cmdView, cc.rc(1, 2));
      add(_tf, cc.rcw(2, 1, 2));
      
      setVisibility();
   }
   
   private void setupTextfield()
   {
      _tf = new JTextField(12);
      _tf.getDocument().addDocumentListener(new DocumentListener() {
         public void insertUpdate(DocumentEvent e) { updateTarget(); }
         public void removeUpdate(DocumentEvent e) { updateTarget(); }
         public void changedUpdate(DocumentEvent e) { updateTarget(); }
      });
      _tf.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e)
         {
            ComplexEObject value = _instruction.getTarget();
            if (value != null)
            {
               try
               {
                  Command command = _instruction.getAction();
                  command.execute(value, InstructionView.this);
                  _instruction.deactivate();
               }
               catch (InvocationTargetException e1)
               {
                  e1.printStackTrace();
               }
            }
         }
      });
      _tf.addKeyListener(new KeyPressAdapter(new KeyAdapter() {
            public void keyPressed(KeyEvent e)
            {
               if (_popup.isVisible())
               {
                  _popup.setVisible(false);
               } else
               {
                  _instruction.deactivate();
                  e.consume();
               }
            }
         } , KeyEvent.VK_ESCAPE));
   }
   
   
   private JPopupMenu _popup;
   private JListSelectionView _matchesList;
   private void setupPopup()
   {
      _popup = new JPopupMenu("Matches");
      _matchesList = new JListSelectionView(_instruction.getTargetMatches());
      _popup.add(_matchesList);

      _tf.addKeyListener(new KeyPressAdapter(new KeyAdapter()
      {
         public void keyPressed(KeyEvent e)
         {
            if (!_instruction.getTargetMatches().isEmpty())
            {
               _popup.show(_tf, 0, _tf.getHeight()+1);
               _matchesList.requestFocusInWindow();
            }
         }
      }, KeyEvent.VK_DOWN));
      
      _matchesList.addListSelectionListener(new ListSelectionListener()
      {
         public void valueChanged(ListSelectionEvent e)
         {
            if (e.getValueIsAdjusting()) return;
            ComplexEObject eo = _matchesList.selectedEO();
            if (eo != null)
            {
               _instruction.setTarget(eo);
            }
         }
      });
      _matchesList.addKeyListener(new KeyPressAdapter(new KeyAdapter()
      {
         public void keyPressed(KeyEvent e)
         {
            ComplexEObject eo = _matchesList.selectedEO();
            if (eo != null)
            {
               _instruction.setTarget(eo);
               _popup.setVisible(false);
               _tf.requestFocusInWindow();
            }
         }
      }, KeyEvent.VK_ENTER));
   }
   
   private void updateTarget()
   {
//      _dismissTimer.restart();
      _instruction.matchText(_tf.getText());
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
               _tf.requestFocusInWindow();
            }
         });
//         _dismissTimer.start();
      }
      else
      {
         _tf.setText("");
         _instruction.clear();
      }
      setVisible(_instruction.active());
   }

   public EObject getEObject() { return _instruction; }
   public void detach() { }
   public void stateChanged(ChangeEvent e) { }

   public void propertyChange(PropertyChangeEvent evt) { }
   public boolean isMinimized() { return false; }
}
