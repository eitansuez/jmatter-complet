package com.u2d.view.swing;

import com.u2d.view.ComplexEView;
import com.u2d.view.swing.list.JListSelectionView;
import com.u2d.field.Association;
import com.u2d.model.EObject;
import com.u2d.model.ComplexEObject;
import com.u2d.model.AbstractListEO;
import com.u2d.interaction.Instruction;
import com.u2d.ui.KeyPressAdapter;
import javax.swing.*;
import javax.swing.event.*;
import java.beans.PropertyChangeEvent;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;

import org.jdesktop.swingx.JXPanel;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Sep 7, 2005
 * Time: 1:04:42 PM
 */
public class SimpleAssociationView extends JXPanel
      implements ComplexEView
{
   private Instruction _instruction;
   private Association _association;
   private CustomIconView iconView = new CustomIconView();
   
   private JTextField _matcherField;
   private JPopupMenu _popup;
   private JListSelectionView _matchesList;

   private boolean targetAssociation;
   private AbstractListEO matches;


   public SimpleAssociationView()
   {
      init();
   }
   public SimpleAssociationView(Association a)
   {
      init();
      bind(a);
   }
   public SimpleAssociationView(Instruction instruction, String assocFieldName)
   {
      _instruction = instruction;
      targetAssociation = ("target".equals(assocFieldName));
      matches = targetAssociation ? _instruction.getTargetMatches() : 
                                          _instruction.getActionMatches();
      init();
      bind(instruction.association(assocFieldName));
   }
   
   private void init()
   {
      setLayout(new BorderLayout());
      add(iconView, BorderLayout.CENTER);
      setupTextfield();
      iconView.setFilterDoc(_matcherField.getDocument());
      setupPopup();
   }
   
   private void setupTextfield()
   {
      //_matcherField = new JTextField(12);
      _matcherField = new InvisibleTextField();
      _matcherField.getDocument().addDocumentListener(new DocumentListener() {
         public void insertUpdate(DocumentEvent e) { updateAssociation(); }
         public void removeUpdate(DocumentEvent e) { updateAssociation(); }
         public void changedUpdate(DocumentEvent e) { updateAssociation(); }
      });
      _matcherField.addActionListener(new CommandAdapter(_instruction.command("Invoke"), _instruction, this));
      _matcherField.addKeyListener(new KeyPressAdapter(new KeyAdapter() {
            public void keyPressed(KeyEvent e)
            {
               if (_popup.isVisible())
               {
                  _popup.setVisible(false);
               }
               else
               {
                  _instruction.deactivate();
                  e.consume();
               }
            }
         } , KeyEvent.VK_ESCAPE));
      add(_matcherField, BorderLayout.SOUTH);
   }
   
   private void setupPopup()
   {
      _popup = new JPopupMenu("Matches");
      _matchesList = new JListSelectionView(matches);
      _popup.add(_matchesList);

      _matcherField.addKeyListener(new KeyPressAdapter(new KeyAdapter()
      {
         public void keyPressed(KeyEvent e)
         {
            if (!matches.isEmpty())
            {
               _popup.show(_matcherField, 0, _matcherField.getHeight()+1);
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
               _association.set(eo);
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
               _association.set(eo);
               _popup.setVisible(false);
               _matcherField.requestFocusInWindow();
            }
         }
      }, KeyEvent.VK_ENTER));
   }
   
   private void updateAssociation()
   {
//      _dismissTimer.restart();
      if (targetAssociation)
      {
         _instruction.matchTargetText(_matcherField.getText());
      }
      else
      {
         _instruction.matchActionText(_matcherField.getText());
      }
   }
   
   public void focus()
   {
      _matcherField.requestFocusInWindow();
   }
   public void clear()
   {
      _matcherField.setText("");
   }

   public void bind(Association a)
   {
      if (_association != null)
      {
         detach();
      }
      _association = a;
      _association.addPropertyChangeListener(this);
      bindIconView();
   }
   public void detach()
   {
      _association.removePropertyChangeListener(this);
      iconView.detach();
   }
   
   public void propertyChange(PropertyChangeEvent evt)
   {
      if (_association.getName().equals(evt.getPropertyName()))
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               bindIconView();
            }
         });
      }
   }
   
   private void bindIconView()
   {
      iconView.detach();
      ComplexEObject value = (ComplexEObject) getEObject();
      iconView.bind(value);
   }

   public void stateChanged(ChangeEvent e) { }

   public Association getAssociation() { return _association; }
   public EObject getEObject() { return _association.get(); }
   public boolean isMinimized() { return false; }
   
   
   class InvisibleTextField extends JTextField implements FocusListener
   {
      
      InvisibleTextField()
      {
         addFocusListener(this);
      }

      protected void paintBorder(Graphics g)
      {
         
      }

      protected void paintComponent(Graphics g)
      {
         
      }

      Color focusBg = new Color(0x554cff);
      Color bgCol = new Color(0x666666);
      
      public void focusGained(FocusEvent e)
      {
         SimpleAssociationView.this.setBackground(focusBg);
      }

      public void focusLost(FocusEvent e)
      {
         SimpleAssociationView.this.setBackground(bgCol);
      }
   }
}
