package com.u2d.view.wings.atom;

import com.u2d.model.AtomicEditor;
import com.u2d.model.AtomicEObject;
import com.u2d.model.EObject;
import com.u2d.type.atom.ChoiceEO;
import java.util.Iterator;
import org.wings.SComboBox;

/**
 *
 * @author Eitan Suez
 */
public class ChoiceEOEditor
      extends SComboBox
      implements AtomicEditor
{
   public ChoiceEOEditor() {}

   public void render(AtomicEObject value)
   {
      if (getItemCount() == 0)
         initialize((ChoiceEO) value);

      ChoiceEO eo = (ChoiceEO) value;
      setSelectedItem(eo.code());
   }

   private void initialize(ChoiceEO eo)
   {
      for (Iterator itr = eo.entries().iterator(); itr.hasNext(); )
         addItem(itr.next());
   }

   public int bind(AtomicEObject value)
   {
      ChoiceEO eo = (ChoiceEO) value;
      Object item = getSelectedItem();
      if (item instanceof String)
      {
         eo.setValue((String) item);
      }
      else if (item instanceof EObject)
      {
         eo.setValue((EObject) item);
      }
      return 0;
   }

   public void passivate() { }
}
