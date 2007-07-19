package com.u2d.view.swing;

import com.u2d.model.EObject;
import com.u2d.model.ComplexEObject;
import com.u2d.field.Association;
import com.u2d.app.Tracing;
import javax.swing.*;
import javax.swing.event.ListDataListener;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jun 5, 2007
 * Time: 5:02:38 PM
 */
public class AssociationComboView
      extends BaseComboView
{
   private Association _association;
   public AssociationComboView(Association association)
   {
      super();
      _association = association;
      setModel(new AssociationComboModel(association));
   }
   public int transferValue()
   {
      ComplexEObject selectedItem = (ComplexEObject) getModel().getSelectedItem();
      Tracing.tracer().fine("transferring value: "+selectedItem);
      _association.set(selectedItem);
      return 0;
   }
   public EObject getEObject() { return _association.get(); }
}

class AssociationComboModel implements ComboBoxModel
{
   private EObject _selectedItem;
   private ListModel _items;
   
   public AssociationComboModel(Association association)
   {
      _items = association.type().list();
      _selectedItem = association.get();
   }

   public void setSelectedItem(Object anItem) { _selectedItem = (EObject) anItem; }
   public Object getSelectedItem() { return _selectedItem; }

   public Object getElementAt(int index) { return _items.getElementAt(index); }
   public int getSize() { return _items.getSize(); }

   public void addListDataListener(ListDataListener l) { _items.addListDataListener(l); }
   public void removeListDataListener(ListDataListener l) { _items.removeListDataListener(l); }
}
