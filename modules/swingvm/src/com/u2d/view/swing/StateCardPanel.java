package com.u2d.view.swing;

import com.u2d.view.EView;
import com.u2d.view.ComplexEView;
import com.u2d.model.EObject;
import com.u2d.model.ComplexEObject;
import com.u2d.model.Editor;
import com.u2d.ui.CardPanel;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.beans.PropertyChangeEvent;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: May 24, 2007
 * Time: 2:08:11 PM
 */
public class StateCardPanel
      extends CardPanel
      implements ComplexEView, Editor
{
   EView _readView, _editView;
   
   public StateCardPanel(EView readView, EView editView)
   {
      _readView = readView;
      _editView = editView;
      add((JComponent) _readView, "read");
      add((JComponent) _editView, "edit");
      
      ComplexEObject parentObject = getEObject().parentObject();
      parentObject.addChangeListener(this);

      // initialization..
      show (parentObject.isEditableState() ? "edit" : "read");
   }
   
   public EObject getEObject() { return _readView.getEObject(); }

   public void detach()
   {
      getEObject().parentObject().removeChangeListener(this);
      _readView.detach();
      _editView.detach();
   }

   public void stateChanged(ChangeEvent e)
   {
      ComplexEObject ceo = (ComplexEObject) e.getSource();
      show (ceo.isEditableState() ? "edit" : "read");
   }

   public void propertyChange(PropertyChangeEvent evt) { } 
   public boolean isMinimized() { return false; }


   // Editor Interface:  Delegate to Edit View..
   public int transferValue() { return ((Editor) _editView).transferValue(); }
   public void setEditable(boolean editable) { ((Editor) _editView).setEditable(editable); }
   public boolean isEditable() { return ((Editor) _editView).isEditable(); }
   public int validateValue() { return ((Editor) _editView).validateValue(); }
}
