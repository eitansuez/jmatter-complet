/*
 * Created on Jan 26, 2004
 */
package com.u2d.view.swing.list;

import com.u2d.model.AbstractListEO;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.ui.*;
import com.u2d.view.*;
import com.u2d.view.swing.SwingViewMechanism;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.util.*;

/**
 * @author Eitan Suez
 */
public class PickView extends JList implements ListEView, ListCellRenderer
{
   protected AbstractListEO _leo;
   private Map _views = new HashMap();
   
   public PickView(AbstractListEO leo)
   {
      _leo = leo;
      _leo.addListDataListener(this);
      
      setModel(_leo);
      setCellRenderer(this);
      setOpaque(true);
      setBackground(Color.white);
      setDragEnabled(false);
   }
   
   public java.awt.Component getListCellRendererComponent(JList list, Object value, int index, 
         boolean selected, boolean hasFocus)
   {
      if (_views.get(value) == null)
      {
         ComplexEObject ceo = (ComplexEObject) value;
         ComplexEView view = SwingViewMechanism.getInstance().getListItemViewAdapter(ceo);
         JComponent comp = (JComponent) view;
         comp.setOpaque(true);
         _views.put(value, view);
      }
      JComponent comp = (JComponent) _views.get(value);
      return RenderHelper.highlight(this, comp, selected, hasFocus);
   }
   
   // model automatically updates list
   public void contentsChanged(ListDataEvent evt) {}
   public void intervalAdded(ListDataEvent evt) {}
   public void intervalRemoved(ListDataEvent evt) {}
   
   public void stateChanged(javax.swing.event.ChangeEvent evt)
   {
      // noop
   }
   
   public EObject getEObject() { return _leo; }
   
   public void detach()
   {
      _leo.removeListDataListener(this);
      Iterator itr = _views.values().iterator();
      while (itr.hasNext())
      {
         ((EView) itr.next()).detach();
      }
      _leo = null;
   }
   
   public boolean isMinimized() { return false; }

}
