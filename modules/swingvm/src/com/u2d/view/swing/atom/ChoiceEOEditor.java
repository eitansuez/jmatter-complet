package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicEditor;
import com.u2d.model.EObject;
import com.u2d.type.atom.ChoiceEO;
import javax.swing.*;

/**
 *
 * @author Eitan Suez
 */
public class ChoiceEOEditor extends JComboBox implements AtomicEditor
{
   private ComboBoxModel _model;

   public ChoiceEOEditor() {}

   public void render(AtomicEObject value)
   {
      if (_model == null)
      {
         _model = new ChoiceEOModel((ChoiceEO) value);
         setModel(_model);
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
