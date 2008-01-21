package com.u2d.view.swing;

import com.u2d.view.EView;
import com.u2d.model.EObject;
import com.u2d.model.ComplexEObject;
import com.u2d.ui.UIUtils;
import com.u2d.field.Association;
import com.u2d.interaction.Instruction;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.event.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Dec 26, 2007
 * Time: 10:05:28 PM
 */
public class InstructionView extends JPanel implements EView
{
   private Instruction _instruction;
   private Timer _dismissTimer;
   private JTextField _tf;
   private SimpleAssociationView _targetView;

   {
      _dismissTimer = new Timer(3000, new ActionListener() {
         public void actionPerformed(ActionEvent e)
         {
            new Thread() {
               public void run()
               {
                  _instruction.getActive().setValue(false);
               }
            }.start();
         }
      });
      _dismissTimer.setRepeats(false);
      _dismissTimer.setCoalesce(true);
   }
   
   public InstructionView(Instruction instruction)
   {
      _instruction = instruction;
      configure();
   }
   
   private void configure()
   {
      setLayout(new BorderLayout());
      
      Association association = _instruction.association("target");
      _targetView = new SimpleAssociationView(association);
      add(_targetView, BorderLayout.CENTER);

      _tf = new JTextField(12);
      _tf.getDocument().addDocumentListener(new DocumentListener() {
         public void insertUpdate(DocumentEvent e) { updateTarget(); }
         public void removeUpdate(DocumentEvent e) { updateTarget(); }
         public void changedUpdate(DocumentEvent e) { updateTarget(); }
      });
      _tf.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e)
         {
            ComplexEObject eo = _instruction.getTarget();
            if (eo != null)
            {
               try
               {
                  eo.defaultCommand().execute(eo, InstructionView.this);
                  _instruction.deactivate();
               }
               catch (InvocationTargetException e1)
               {
                  e1.printStackTrace();
               }
            }
         }
      });
      _tf.addKeyListener(new KeyAdapter() {
         public void keyPressed(KeyEvent e)
         {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            {
               _instruction.deactivate();
               e.consume();
            }
         }
      });
      add(_tf, BorderLayout.SOUTH);
      
      setVisibility();
      
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
   
   private void updateTarget()
   {
      _dismissTimer.restart();
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
         _dismissTimer.start();
      }
      else
      {
         _tf.setText("");
         _targetView.clear();
      }
      setVisible(_instruction.active());
   }

   public EObject getEObject() { return _instruction; }
   public void detach() { }
   public void stateChanged(ChangeEvent e) { }
}
