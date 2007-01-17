package com.u2d.view.swing;

import com.u2d.model.EObject;
import com.u2d.model.Editor;
import com.u2d.type.AbstractChoiceEO;
import com.u2d.view.*;
import com.u2d.ui.CardPanel;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
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
      
      // ensures desired alignment of component
      JPanel cbPanel = new JPanel();
      cbPanel.setOpaque(false);
      cbPanel.setLayout(new FormLayout("pref", "pref"));
      CellConstraints cc = new CellConstraints();
      cbPanel.add(_cb, cc.xy(1,1));

      add(cbPanel, "edit");
      add(_label, "view");

      setEditable(false); // start out read-only by default
   }
   
   public void detach()
   {
      _choice.removeChangeListener(this);
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

   public int transferValue() { return 0; }

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
      ChoiceComboBox() { init(); }
      ChoiceComboBox(ComboBoxModel model)
      {
         super(model);
         init();
      }
      private void init()
      {
         UIDefaults defaults = UIManager.getDefaults();
         defaults.put("ComboBox.disabledBackground", defaults.get("ComboBox.background"));
         defaults.put("ComboBox.disabledForeground", defaults.get("ComboBox.foreground"));
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
