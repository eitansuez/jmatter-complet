/*
 * Created on Apr 1, 2005
 */
package com.u2d.view.swing.list;

import com.u2d.model.AbstractListEO;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.ui.FlexibleListCellRenderer;
import com.u2d.ui.IconList;
import com.u2d.view.*;
import com.u2d.view.swing.SwingViewMechanism;
import com.u2d.app.Context;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.border.Border;
import java.awt.*;

/**
 * @author Eitan Suez
 */
public class IconListView extends IconList
                          implements ListEView, FlexibleListCellRenderer
{
   protected AbstractListEO _leo;
   private SwingViewMechanism vmech = Context.getInstance().swingvmech();
   
   public IconListView(AbstractListEO leo)
   {
      _leo = leo;
      setModel(_leo);
      setCellRenderer(this);
      setOpaque(true);
      setBackground(Color.white);
   }

   public java.awt.Component getListCellRendererComponent(JComponent list, Object value, int index, 
             boolean isSelected, boolean cellHasFocus)
   {
      ComplexEObject ceo = (ComplexEObject) value;

      JComponent comp = (JComponent) vmech.getIconView(ceo);
      
      comp.setBackground( isSelected ? 
            UIManager.getColor("List.selectionBackground") : list.getBackground() );
      comp.setForeground( isSelected ? 
            UIManager.getColor("List.selectionForeground") : list.getForeground() );
      
      comp.setBorder( cellHasFocus ? 
            UIManager.getBorder("List.focusCellHighlightBorder") : EMPTYBORDER );
      return comp;
   }
   private static Border EMPTYBORDER = BorderFactory.createEmptyBorder(1,1,1,1);
   
   public void stateChanged(javax.swing.event.ChangeEvent evt) {}

   // listdatalistener implementation:
   public synchronized void contentsChanged(ListDataEvent evt)
   {
      detachItems();
      super.contentsChanged(evt);
   }
   public void intervalAdded(ListDataEvent evt)
   {
      detachItems();
      super.intervalAdded(evt);
   }
   public void intervalRemoved(ListDataEvent evt)
   {
      detachItems();
      super.intervalRemoved(evt);
   }

   public EObject getEObject() { return _leo; }
   
   public void detach()
   {
      _leo.removeListDataListener(this);
      detachItems();
   }
   
   private void detachItems()
   {
      Component item = null;
      for (int i=0; i<getComponentCount(); i++)
      {
         item = getComponent(i);
         if (item instanceof EView)
            ((EView) item).detach();
      }
   }

   public Dimension getPreferredScrollableViewportSize()
   {
      Dimension p = super.getPreferredScrollableViewportSize();
      p.height = Math.min(MAXHEIGHT, p.height);
      p.height = Math.max(p.height, MINHEIGHT);
      return p;
   }

   public boolean isMinimized() { return false; }

}
