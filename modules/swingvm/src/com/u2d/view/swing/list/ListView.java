/*
 * Created on Jan 26, 2004
 */
package com.u2d.view.swing.list;

import com.u2d.model.AbstractListEO;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.ui.desktop.CloseableJInternalFrame;
import com.u2d.view.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import com.l2fprod.common.swing.PercentLayout;

import java.awt.*;

/**
 * @author Eitan Suez
 */
public class ListView extends JPanel implements ListEView, Scrollable
{
   protected AbstractListEO _leo;
   
   public ListView(AbstractListEO leo)
   {
      _leo = leo;
      setOpaque(false);
      setLayout(new PercentLayout(PercentLayout.VERTICAL, 1));
      addItems();
      _leo.addListDataListener(this);
   }
   
   protected void addItems()
   {
      Iterator itr = _leo.getItems().iterator();
      ComplexEObject ceo = null;
      while (itr.hasNext())
      {
         ceo = (ComplexEObject) itr.next();
         add((JComponent) ceo.getListItemView());
      }
   }
   
   public void contentsChanged(ListDataEvent evt)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            removeItems();
            addItems();
            CloseableJInternalFrame.updateSize(ListView.this);
         }
      });
   }
   public void intervalAdded(final ListDataEvent evt)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            AbstractListEO source = (AbstractListEO) evt.getSource();
            ComplexEObject ceo = (ComplexEObject) source.getElementAt(evt.getIndex0());
            EView view = ceo.getListItemView();
            add((JComponent) view, evt.getIndex0());
            CloseableJInternalFrame.updateSize(ListView.this);
         }
      });
   }
   public void intervalRemoved(final ListDataEvent evt)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            int index = evt.getIndex0();
            if (getComponent(index) instanceof EView)
               ((EView) getComponent(index)).detach();
            removeItemAtIndex(index);
            CloseableJInternalFrame.updateSize(ListView.this);
         }
      });
   }
   
   public void stateChanged(javax.swing.event.ChangeEvent evt) {}
   
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
   
   protected void removeItems()
   {
     detachItems();
     removeAll();
   }
   
   private void removeItemAtIndex(int index)
   {
      Component c = getComponent(index);
      if (c instanceof EView)
        ((EView) c).detach();
      remove(index);
   }


   //== implementation of scrollable interface..
   public boolean getScrollableTracksViewportHeight()
   {
      if (getParent() instanceof JViewport)
      {
         JViewport viewport = (JViewport) getParent();
         return (viewport.getHeight() > getPreferredSize().height);
      }
      return false;
   }

   public boolean getScrollableTracksViewportWidth()
   {
      if (getParent() instanceof JViewport)
      {
         JViewport viewport = (JViewport) getParent();
         return (viewport.getWidth() > getPreferredSize().width);
      }
      return false;
   }

   public Dimension getPreferredScrollableViewportSize()
   {
      Dimension p = getPreferredSize();
      p.height = Math.min(MAXHEIGHT, p.height);
      p.height = Math.max(p.height, MINHEIGHT);
      return p;
   }

   public int getScrollableUnitIncrement( Rectangle visibleRect, int orientation, int direction)
   {
      return (orientation == SwingConstants.HORIZONTAL) ? 80 : 30;
   }

   public int getScrollableBlockIncrement( Rectangle visibleRect, int orientation, int direction)
   {
      return (orientation == SwingConstants.HORIZONTAL) ? visibleRect.width : visibleRect.height;
   }

   public boolean isMinimized() { return false; }

}
