package com.u2d.view.swing;

import com.u2d.view.ComplexEView;
import com.u2d.model.Editor;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.beans.PropertyChangeEvent;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jul 19, 2007
 * Time: 11:55:49 AM
 */
public abstract class BaseComboView extends JPanel
      implements ComplexEView, Editor
{
   protected JComboBox _combo;
   
   public BaseComboView()
   {
      _combo = new JComboBox();
      setLayout(new BorderLayout());
      add(_combo, BorderLayout.CENTER);
   }
   
   public int validateValue() { return getEObject().validate(); }

   public void setEditable(boolean editable) { _combo.setEnabled(editable); }
   public boolean isEditable() { return _combo.isEnabled(); }

   public void setModel(ComboBoxModel model) { _combo.setModel(model); }
   public ComboBoxModel getModel() { return _combo.getModel(); }
   
   public boolean isMinimized() { return false; }

   public void propertyChange(PropertyChangeEvent evt) { } 
   public void stateChanged(ChangeEvent e) { }

   public void detach() { }
}
