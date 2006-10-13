/*
 * Created on Apr 28, 2004
 */
package com.u2d.view.swing.list;

import com.u2d.model.AbstractListEO;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.view.*;
import com.u2d.view.swing.SwingViewMechanism;
import com.u2d.app.Context;

import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.awt.*;

/**
 * @author Eitan Suez
 */
public class ToolbarView extends JToolBar implements ListEView
{
   private AbstractListEO _leo;
   private JComponent _sampleChild;
   private ToolbarPanel _tbpanel;
   private SwingViewMechanism vmech = Context.getInstance().swingvmech();
   
   public ToolbarView(String name, AbstractListEO leo)
   {
      super(name);
      setOpaque(true);
      setBackground(Color.white);

      _leo = leo;
      _leo.addListDataListener(this);
      
      Iterator itr = _leo.iterator();
      ComplexEObject ceo = null;
      JComponent comp = null;
      _tbpanel = new ToolbarPanel();
      while (itr.hasNext())
      {
         ceo = (ComplexEObject) itr.next();
         comp = (JComponent) vmech.getIconView(ceo);
         _tbpanel.add(comp);
      }
      _sampleChild = comp;
      add(new JScrollPane(_tbpanel));

      setupBorder();
   }
   
   private void setupBorder()
   {
      javax.swing.border.Border emptyBorder = BorderFactory.createEmptyBorder();
      javax.swing.border.Border newBorder = 
         BorderFactory.createCompoundBorder(getBorder(), emptyBorder);
      setBorder(newBorder);
   }


   // TODO: implement this!
   public void contentsChanged(ListDataEvent evt) {}
   
   public void intervalAdded(final ListDataEvent evt)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            AbstractListEO source = (AbstractListEO) evt.getSource();
            ComplexEObject ceo = (ComplexEObject) source.getElementAt(evt.getIndex0());
            EView view = vmech.getIconView(ceo);
            _tbpanel.add((JComponent) view, evt.getIndex0());
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
            _tbpanel.remove(index);
         }
      });
   }
   
   public void stateChanged(javax.swing.event.ChangeEvent evt) {}
   
   public EObject getEObject() { return _leo; }
   
   public boolean isHorizontal() { return getOrientation() == SwingConstants.HORIZONTAL; }
   
   class ToolbarPanel extends JPanel implements Scrollable
   {
      ToolbarPanel()
      {
         super();
         setOpaque(false);
         setLayout(new FlowLayout(FlowLayout.CENTER));
      }
      
      public Dimension getPreferredSize()
      {
         if (isHorizontal() || _sampleChild==null) return super.getPreferredSize();
         
         int width = _sampleChild.getSize().width
               + _sampleChild.getInsets().left + _sampleChild.getInsets().right;
        
         int height = _sampleChild.getSize().height
               + _sampleChild.getInsets().top + _sampleChild.getInsets().bottom;
         height *= getComponentCount();
        
         return new Dimension(width, height);
      }
      
      public boolean getScrollableTracksViewportHeight()
      {
         if (isHorizontal())
         {
            return true;
         }
         else
         {
            if (getParent() instanceof JViewport)
            {
               JViewport viewport = (JViewport) getParent();
               int vpheight = viewport.getHeight();
               return (vpheight > getPreferredSize().height || vpheight == 0);
            }
            return false;
         }
      }

      public boolean getScrollableTracksViewportWidth()
      {
         if (isHorizontal())
         {
            if (getParent() instanceof JViewport)
            {
               JViewport viewport = (JViewport) getParent();
               int vpwidth = viewport.getWidth();
               return (vpwidth > getPreferredSize().width || vpwidth == 0);
            }
            return false;
         }
         else
         {
            return true;
         }
      }

      public Dimension getPreferredScrollableViewportSize()
      {
         if (isHorizontal())
         {
            Dimension p = getPreferredSize();
            if (!getScrollableTracksViewportWidth())
               return new Dimension(p.width, p.height + 15);
            return p;
         }
         else
         {
            Dimension p = getPreferredSize();
            if (!getScrollableTracksViewportHeight())
               return new Dimension(p.width + 15, p.height);
            return p;
         }
      }

      public int getScrollableUnitIncrement( Rectangle visibleRect, int orientation, int direction)
      {
         return (orientation == SwingConstants.HORIZONTAL) ? 80 : 30;
      }

      public int getScrollableBlockIncrement( Rectangle visibleRect, int orientation, int direction)
      {
         return (orientation == SwingConstants.HORIZONTAL) ? visibleRect.width : visibleRect.height;
      }
   }
   
   
   public void detach()
   {
      _leo.removeListDataListener(this);
      Component comp = null;
      for (int i=0; i<_tbpanel.getComponentCount(); i++)
      {
         comp = _tbpanel.getComponent(i);
         if (comp instanceof EView)
            ((EView) comp).detach();
      }
   }
   
   public boolean isMinimized() { return false; }

}
