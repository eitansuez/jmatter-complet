/*
 * Created on May 13, 2004
 */
package com.u2d.view.swing;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;
import java.beans.*;
import com.u2d.view.*;
import com.u2d.view.swing.dnd.*;
import com.u2d.model.AbstractListEO;
import com.u2d.model.ComplexEObject;
import com.u2d.model.ComplexType;
import com.u2d.model.EObject;
import com.u2d.model.Editor;
import com.u2d.model.NullComplexEObject;
import com.u2d.ui.*;

/**
 * @author Eitan Suez
 */
public class ComplexPickView extends JPanel 
                             implements ComplexEView, PopupClient, Editor
{
   private ComplexType _type;
   private ComplexEObject _ceo;
   private ComplexEView _view;
   
   private PopupButton2 _trigger;
   private JList _contents = null;
   
   public ComplexPickView(ComplexType type)
   {
      _type = type;
      setLayout(new FlowLayout(FlowLayout.LEFT));
      updateCeo(new NullComplexEObject(_type));
      _trigger = new PopupButton2(_type.iconSm(), this, this, this);
      add(_trigger);
   }
   
   // implementation of PopupClient.  trigger delegates to this method
   // when it's time for it to show a list of items to pick from.
   public JComponent getContents()
   {
      if (_contents == null)
      {
         AbstractListEO pickList = _type.Browse(null);
         _contents = (JList) pickList.getPickView();
         _contents.addListSelectionListener(new ListSelectionListener()
            {
               public void valueChanged(ListSelectionEvent evt)
               {
                  Object obj = _contents.getSelectedValue();
                  ComplexEObject selectedItem = (ComplexEObject) obj;
                  updateCeo(selectedItem);
                  _trigger.hidePopup();
               }
            });
      }
      return _contents;
   }

   public EObject getEObject() { return _ceo; }
   public boolean isMinimized() { return false; }
   public void propertyChange(PropertyChangeEvent evt) {}
   public void stateChanged(ChangeEvent evt) {}

   public boolean isEditable() { return true; }
   public void setEditable(boolean editable) {}
   public int transferValue() { return 0; }
   
   
   private void updateCeo(ComplexEObject item)
   {
      if (_view != null)
      {
         _view.detach();
      }
      _ceo = item;
      
      if (getComponentCount() > 0)
         remove(0);
      _view = _ceo.getListItemView();
      JComponent comp = ((JComponent) _view);
      add(comp, 0);
      comp.setTransferHandler(new DropTargetHandler());
      revalidate(); repaint();
   }
   
   public void detach()
   {
      _type = null;
      if (_view != null)
         _view.detach();
      _view = null;
      _ceo = null;
   }
   
}
