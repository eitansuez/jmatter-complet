package com.u2d.view.wings;

import java.awt.event.ItemListener;
import com.u2d.view.EView;
import com.u2d.model.*;
import javax.swing.event.ChangeEvent;
import org.wings.SComponent;
import org.wings.SLabel;
import org.wings.SComboBox;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Dec 27, 2006
 * Time: 4:25:24 PM
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

      SComponent rendererComponent = (SComponent) _renderer;
      SComponent editorComponent = (SComponent) _editor;

//      rendererComponent.setOpaque(false);
//      editorComponent.setOpaque(false);

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


   class TypeRenderer extends SLabel
   {
      TypeRenderer()
      {
//         setOpaque(false);
      }
   }

   class TypeEditor extends SComboBox
   {
      TypeEditor()
      {
//         setOpaque(false);
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
