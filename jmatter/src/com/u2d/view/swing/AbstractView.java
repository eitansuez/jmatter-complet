package com.u2d.view.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import com.u2d.element.Field;
import com.u2d.model.*;
import com.u2d.ui.Caption;
import com.u2d.ui.CardPanel;
import com.u2d.view.ComplexEView;
import com.u2d.view.EView;
import com.u2d.view.swing.atom.TypePicker;

public class AbstractView extends JPanel
                          implements ComplexEView, Editor, ItemListener
{
   private Field _field;
   private ComplexEObject _parent;
   private TypePicker _picker;
   private CardPanel _card;
   private boolean _editable;
   private Map _typeViewMap = new HashMap();
   
   public AbstractView(Field field, ComplexEObject parent)
   {
      _field = field;
      _parent = parent;
      
      Caption caption = new Caption(_field.name()+" type:");
      _picker = new TypePicker(_field.fieldtype());
      
      setLayout(new BorderLayout());
      JPanel top = new JPanel(new FlowLayout());
      top.add(caption);
      top.add(_picker);
      add(top, BorderLayout.NORTH);
      
      _card = new CardPanel();
      add(_card, BorderLayout.CENTER);

      AbstractListEO types = _field.fieldtype().concreteTypes();
      EObject eo = null;
      ComplexEObject value = (ComplexEObject) _field.get(_parent);
      ComplexType type;
      for (int i=0; i<types.getSize(); i++)
      {
         type = (ComplexType) types.getElementAt(i);
         eo = type.instance();
         EView view = null;
         if (type.equals(value.type()))
         {
            view = value.getView();
            _card.add((JComponent) view, type.name());
         }
         else
         {
            view = eo.getView();
            _card.add((JComponent) view, type.name());
         }
         _typeViewMap.put(type, view);
      }
      _card.show(value.type().name());
      _picker.getEObject().setValue(value.type());
      _picker.addItemListener(this);
   }
   
   public void itemStateChanged(ItemEvent e)
   {
      if (e.getStateChange() == ItemEvent.SELECTED)
      {
         ComplexType type = (ComplexType) e.getItem();
         _card.show(type.name());
      }
   }
   

   public void detach()
   {
      _picker.removeItemListener(this);
      _picker.detach();
      for (int i=0; i<_card.getComponentCount(); i++)
      {
         ((EView) _card.getComponent(i)).detach();
      }
   }

   public EObject getEObject() { return _field.get(_parent); }
   public boolean isMinimized() { return false; }

   public void stateChanged(ChangeEvent e) {}
   public void propertyChange(PropertyChangeEvent evt) {}

   public boolean isEditable() { return _editable; }

   public void setEditable(boolean editable)
   {
      _editable = editable;
      for (int i=0; i<_card.getComponentCount(); i++)
      {
         ((Editor) _card.getComponent(i)).setEditable(editable);
      }
   }

   public int transferValue()
   {
      EView view = (EView) _typeViewMap.get(_picker.getEObject());
      int result = ((Editor) view).transferValue();
      if (result == 0)
         _field.set(_parent, view.getEObject());
      return result;
   }
   
   
}
