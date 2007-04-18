package com.u2d.view.swing.list;

import com.u2d.view.ListEView;
import com.u2d.model.AbstractListEO;
import com.u2d.model.EObject;
import com.u2d.model.Editor;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;

/**
 * First pass implementation of a multichoice view.
 * This view is returned at the moment for compositelists of choice types
 * 
 * Simplify: I should be able to refactor out the notion of choices altogether
 * and just treat them as lists of objects. (tbd)
 * 
 * Allows one to construct a list by picking from a list of choices, each with a checkbox
 */
public class MultiChoiceView extends JPanel
      implements ListEView, ChangeListener, Editor
{
   private AbstractListEO _leo;
   private AbstractListEO _choices;
   private java.util.List<MyCheckbox> _checkboxes = new ArrayList<MyCheckbox>();
   private boolean _editable = false;

   public MultiChoiceView(AbstractListEO leo)
   {
      _leo = leo;
      _choices = leo.type().list();

      setOpaque(false);
      layItOut();
   }

   private void layItOut()
   {
      FormLayout layout = new FormLayout("left:pref, 5px, left:pref, 5px, left:pref", "");
      DefaultFormBuilder builder = new DefaultFormBuilder(layout, this);
      CellConstraints cc = new CellConstraints();

      EObject choice;
      for (int i=0; i<_choices.getSize(); i++)
      {
         choice = (EObject) _choices.getElementAt(i);
         MyCheckbox checkbox = new MyCheckbox(choice, _leo.contains(choice));
         _checkboxes.add(checkbox);
         
         int column = (i % 3);
         int colConstraint = 1 + (2 * column);
         
         if (column == 0) builder.appendRow("pref");
         builder.add(checkbox, cc.xy(colConstraint, builder.getRow()));
         if (column == 2) builder.nextLine();
      }
   }
   
   class MyCheckbox extends JCheckBox
   {
      EObject _obj;
      
      public MyCheckbox(EObject obj, boolean selected)
      {
         super(obj.toString(), selected);
         setOpaque(false);
         _obj = obj;
      }
      
      public EObject getObject() { return _obj; }
   }

   public EObject getEObject() { return _leo; }

   public void detach() { }

   public void stateChanged(ChangeEvent e) {}

   public int transferValue()
   {
      java.util.List items = new ArrayList();
      MyCheckbox box;
      for (int i=0; i<_checkboxes.size(); i++)
      {
         box = _checkboxes.get(i);

         if (box.isSelected())
         {
            items.add(box.getObject());
         }
      }
      _leo.setItems(items);
      return 0;
   }

   public int validateValue() { return _leo.validate(); }

   public void setEditable(boolean editable)
   {
      _editable = editable;
      for (int i=0; i<_checkboxes.size(); i++)
      {
         _checkboxes.get(i).setEnabled(_editable);
      }
   }

   public boolean isEditable() { return _editable; }

   public void intervalAdded(ListDataEvent e) { }
   public void intervalRemoved(ListDataEvent e) { }
   public void contentsChanged(ListDataEvent e) { }

   public boolean isMinimized() { return false; }
}
