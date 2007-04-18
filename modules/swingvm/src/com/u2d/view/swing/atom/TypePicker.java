package com.u2d.view.swing.atom;

import java.awt.event.ItemListener;
import com.u2d.ui.CardPanel;
import com.u2d.view.EView;
import com.u2d.model.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;

/**
 * Date: Jun 8, 2005
 * Time: 1:05:45 PM
 *
 * @author Eitan Suez
 */
public class TypePicker extends CardPanel implements EView, Editor
{
   private ComplexType _itype;
   private TypeRenderer _renderer;
   private TypeEditor _editor;

   public TypePicker(ComplexType itype)
   {
      _itype = itype;
      _renderer = new TypeRenderer();
      _editor = new TypeEditor();

      JComponent rendererComponent = (JComponent) _renderer;
      JComponent editorComponent = (JComponent) _editor;

      rendererComponent.setOpaque(false);
      editorComponent.setOpaque(false);

      add(rendererComponent, "view");
      add(editorComponent, "edit");

      _renderer.setText(_editor.getSelectedItem().toString());
   }

   public void stateChanged(ChangeEvent evt) {}

   public int transferValue()
   {
      _renderer.setText(_editor.getSelectedItem().toString());
      return 0;
   }
   public int validateValue() { return 0; }

   private boolean _editable = false;
   public void setEditable(boolean editable)
   {
      _editable = editable;
      show((editable) ? "edit" : "view");
   }
   public boolean isEditable() { return _editable; }

   public EObject getEObject()
   {
      return (ComplexType) _editor.getSelectedItem();
   }

   public void detach() {}


   class TypeRenderer extends JLabel
   {
      TypeRenderer()
      {
         setOpaque(false);
      }
   }

   class TypeEditor extends JComboBox
   {
      TypeEditor()
      {
         setOpaque(false);
         AbstractListEO types = _itype.concreteTypes();
         for (int i=0; i<types.getSize(); i++)
         {
            addItem(types.getElementAt(i));
         }
      }
   }
   
   public void addItemListener(ItemListener l)
   {
      _editor.addItemListener(l);
   }
   public void removeItemListener(ItemListener l)
   {
      _editor.removeItemListener(l);
   }

}
