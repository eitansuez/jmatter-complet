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
   private Instruction ins;
   private Timer dismissTimer;
   private JTextField _tf;
   private SimpleAssociationView _targetView;

   {
      dismissTimer = new Timer(2000, new ActionListener() {
         public void actionPerformed(ActionEvent e)
         {
            new Thread() {
               public void run()
               {
                  ins.getActive().setValue(false);
               }
            }.start();
         }
      });
      dismissTimer.setRepeats(false);
      dismissTimer.setCoalesce(true);
   }
   public InstructionView(Instruction instruction)
   {
      ins = instruction;
      configure();
   }
   
   private void configure()
   {
      setLayout(new BorderLayout());
      
      Association association = ins.association("target");
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
            ComplexEObject eo = ins.getTarget();
            if (eo != null)
            {
               try
               {
                  eo.defaultCommand().execute(eo, InstructionView.this);
                  ins.deactivate();
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
               ins.deactivate();
               e.consume();
            }
         }
      });
      add(_tf, BorderLayout.SOUTH);
      
      setVisibility();
      
      ins.getActive().addChangeListener(new ChangeListener() {
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
      dismissTimer.restart();
      ins.matchText(_tf.getText());
   }
   
   private void setVisibility()
   {
      if (ins.active())
      {
         setSize(getPreferredSize());
         setLocation(UIUtils.computeCenter(this.getParent(), this));
         SwingUtilities.invokeLater(new Runnable() {
            public void run()
            {
               _tf.requestFocusInWindow();
            }
         });
         dismissTimer.start();
      }
      else
      {
         _tf.setText("");
         _targetView.clear();
      }
      setVisible(ins.active());
   }

   public EObject getEObject() { return ins; }
   public void detach() { }
   public void stateChanged(ChangeEvent e) { }
}
