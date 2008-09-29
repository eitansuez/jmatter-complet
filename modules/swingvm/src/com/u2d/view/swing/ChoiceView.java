package com.u2d.view.swing;

import com.u2d.model.EObject;
import com.u2d.model.Editor;
import com.u2d.type.AbstractChoiceEO;
import com.u2d.view.*;
import com.u2d.ui.CardPanel;
import javax.swing.*;
import javax.swing.event.*;
import java.beans.PropertyChangeEvent;

/**
 * @author Eitan Suez
 */
public class ChoiceView extends CardPanel implements ComplexEView, Editor
{
   private AbstractChoiceEO _choice;
   private ChoiceComboBox _cb;
   private JLabel _label = new JLabel();
   {
      _label.setOpaque(false);
   }

   public ChoiceView(AbstractChoiceEO choice)
   {
      _choice = choice;
      _cb = new ChoiceComboBox(_choice);

      _choice.addChangeListener(this);
      stateChanged(null);  // initialize the label text..
      
      add(_cb, "edit");
      add(_label, "view");

      setEditable(false); // start out read-only by default
   }
   
   public void detach()
   {
      _choice.removeChangeListener(this);
      _choice.removeListDataListener(_cb);
   }
   
   public void stateChanged(ChangeEvent evt)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            _label.setText(_choice.title().toString());
         }
      });
   }
   public void propertyChange(PropertyChangeEvent evt) {}

   public int transferValue()
   {
      _choice.setValue((EObject) _cb.getSelectedItem());
      stateChanged(null);
      return 0;
   }


   public int validateValue() { return _choice.validate(); }

   public void setEditable(boolean editable)
   {
      _cb.setEnabled(editable);
      show((editable) ? "edit" : "view");
   }
   public boolean isEditable() { return _cb.isEnabled(); }
   
   public EObject getEObject() { return _choice; }
   public boolean isMinimized() { return false; }
   
   class ChoiceComboBox extends JComboBox
   {
      ChoiceComboBox()
      {
         setOpaque(false);
      }

      ChoiceComboBox(ComboBoxModel model)
      {
         super(model);
         setOpaque(false);
      }

      public void contentsChanged(final ListDataEvent e)
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               ChoiceComboBox.super.contentsChanged(e);
            }
         });
      }

      public void intervalAdded(final ListDataEvent e)
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               ChoiceComboBox.super.intervalAdded(e);
            }
         });
      }

      public void intervalRemoved(final ListDataEvent e)
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               ChoiceComboBox.super.intervalRemoved(e);
            }
         });
      }
   }
   
}
