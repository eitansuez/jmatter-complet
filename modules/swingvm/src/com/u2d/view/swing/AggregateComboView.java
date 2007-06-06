package com.u2d.view.swing;

import com.u2d.view.ComplexEView;
import com.u2d.model.Editor;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListDataListener;
import java.beans.PropertyChangeEvent;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jun 5, 2007
 * Time: 5:02:38 PM
 */
public class AggregateComboView
      extends JComboBox
      implements ComplexEView, Editor
{
   private ComplexEObject _value;
   public AggregateComboView(ComplexEObject value)
   {
      _value = value;
      setModel(new AggregateComboModel(value));
   }
   
   public int transferValue()
   {
      ComplexEObject selectedItem = (ComplexEObject) getModel().getSelectedItem();
      _value.setValue(selectedItem);
      return 0;
   }
   public int validateValue() { return getEObject().validate(); }

   public EObject getEObject() { return _value; }


   public boolean isMinimized() { return false; }

   public void propertyChange(PropertyChangeEvent evt) { } 
   public void stateChanged(ChangeEvent e) { }

   public void detach() { }
}

class AggregateComboModel implements ComboBoxModel
{
   private EObject _selectedItem;
   private ListModel _items;
   
   public AggregateComboModel(ComplexEObject value)
   {
      _items = value.type().list();
      _selectedItem = value;
   }

   public void setSelectedItem(Object anItem) { _selectedItem = (EObject) anItem; }
   public Object getSelectedItem() { return _selectedItem; }

   public Object getElementAt(int index) { return _items.getElementAt(index); }
   public int getSize() { return _items.getSize(); }

   public void addListDataListener(ListDataListener l) { _items.addListDataListener(l); }
   public void removeListDataListener(ListDataListener l) { _items.removeListDataListener(l); }
}
