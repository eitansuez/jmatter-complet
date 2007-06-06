package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEditor;
import com.u2d.model.AtomicEObject;
import com.u2d.view.ActionNotifier;
import com.u2d.type.atom.StringEO;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.util.List;

/**
 * Date: Jun 8, 2005
 * Time: 12:51:34 PM
 *
 * @author Eitan Suez
 */
public class ComboEditor
      extends JComboBox
      implements AtomicEditor, ActionNotifier
{
   public ComboEditor(final List options)
   {
      setModel(new ComboBoxModel()
      {
         private Object selectedItem;
         public Object getSelectedItem() { return selectedItem; }
         public void setSelectedItem(Object anItem) { selectedItem = anItem; }
         public int getSize() { return options.size(); }
         public Object getElementAt(int index) { return options.get(index); }
         // the list is static..
         public void addListDataListener(ListDataListener l) { } 
         public void removeListDataListener(ListDataListener l) { }
      });
   }
   
   public void render(AtomicEObject value)
   {
      StringEO eo = (StringEO) value;
      setSelectedItem(eo.stringValue());
   }
   public int bind(AtomicEObject value)
   {
      StringEO eo = (StringEO) value;
      eo.setValue((String) getSelectedItem());
      return 0;
   }

   public void passivate() { }

}
