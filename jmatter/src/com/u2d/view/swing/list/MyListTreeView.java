/*
 * Created on Jan 26, 2004
 */
package com.u2d.view.swing.list;

import com.u2d.model.AbstractListEO;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.ui.desktop.CloseableJInternalFrame;
import com.u2d.view.*;
import com.u2d.view.swing.SwingViewMechanism;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import com.l2fprod.common.swing.PercentLayout;

/**
 * @author Eitan Suez
 */
public class MyListTreeView extends JPanel implements ListEView
{
   private AbstractListEO _leo;

   private List _childViews = new ArrayList();

   public MyListTreeView(AbstractListEO leo)
   {
      _leo = leo;
      _leo.addListDataListener(this);
      setOpaque(false);

      setLayout(new PercentLayout(PercentLayout.VERTICAL, 1));

      Iterator itr = _leo.getItems().iterator();
      ComplexEObject ceo = null;
      EView view = null;
      while (itr.hasNext())
      {
         ceo = (ComplexEObject) itr.next();
         view = SwingViewMechanism.getInstance().
               getExpandableView(ceo, false /* collapsed */);
         _childViews.add(view);
         add((JComponent) view);
      }

   }

   public void contentsChanged(ListDataEvent evt) {}
   public void intervalAdded(final ListDataEvent evt)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            AbstractListEO source = (AbstractListEO) evt.getSource();
            ComplexEObject ceo = (ComplexEObject) source.getElementAt(evt.getIndex0());
            EView view = SwingViewMechanism.getInstance().
                  getExpandableView(ceo);
            _childViews.add(view);
            add((JComponent) view, evt.getIndex0());
            CloseableJInternalFrame.updateSize(MyListTreeView.this);
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
            _childViews.remove(index);
            remove(index);
            CloseableJInternalFrame.updateSize(MyListTreeView.this);
         }
      });
   }

   public void stateChanged(javax.swing.event.ChangeEvent evt) {}

   public EObject getEObject() { return _leo; }

   public void detach()
   {
      _leo.removeListDataListener(this);

      Iterator itr = _childViews.iterator();
      while (itr.hasNext())
         ((EView) itr.next()).detach();

   }

   public boolean isMinimized() { return false; }

}
