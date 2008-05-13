package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicEditor;
import com.u2d.model.EObject;
import com.u2d.type.atom.ChoiceEO;
import javax.swing.*;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

/**
 *
 * @author Eitan Suez
 */
public class ChoiceEOEditor extends JComboBox implements AtomicEditor
{
   private ComboBoxModel _model;

   public ChoiceEOEditor() {}

   public void render(final AtomicEObject value)
   {
      if (_model == null)
      {
         _model = new ChoiceEOModel((ChoiceEO) value);
         setModel(_model);
         addItemListener(new ItemListener()
         {
            public void itemStateChanged(ItemEvent e)
            {
               if (e.getStateChange() == ItemEvent.SELECTED)
               {
                  bind(value);  // causes changes to reflect immediately; i.e. without having to wait for component to losefocus
               }
            }
         });
      }
      setSelectedItem(value);
   }

   public int bind(AtomicEObject value)
   {
      Object selectedItem = _model.getSelectedItem();
      if (selectedItem instanceof String)
      {
         ((ChoiceEO) value).setValue((String) selectedItem);
      }
      else
      {
         value.setValue((EObject) selectedItem);
      }
      return 0;
   }

   public void passivate() { }
}
