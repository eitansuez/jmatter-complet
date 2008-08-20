/*
 * Created on Jan 26, 2004
 */
package com.u2d.view.swing.list;

import com.u2d.model.EObject;
import com.u2d.model.AbstractListEO;
import com.u2d.view.*;
import com.u2d.list.Paginable;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;

/**
 * @author Eitan Suez
 */
public class PaginableView extends JPanel implements ListEView, CompositeView
{
   protected Paginable _leo;
   private ListEView _listView;
   private PageScrollBar _pageScrollBar;
   
   public PaginableView(ListEView listView)
   {
      if (!(listView.getEObject() instanceof Paginable))
         throw new IllegalArgumentException("PaginableViews work only with "
             + " listeo types that implement the Paginable interface");
      
      _listView = listView;
      _leo = (Paginable) listView.getEObject();

      // TODO: Define an interface for ListEO's
      ((AbstractListEO) _leo).addListDataListener(this);
      
      setLayout(new BorderLayout());
      
      if (_listView instanceof CompositeView)
      {
         add((JComponent) _listView, BorderLayout.CENTER);
      }
      else
      {
         JScrollPane scrollPane = new JScrollPane((JComponent) _listView);
         add(scrollPane, BorderLayout.CENTER);
      }
      
      _pageScrollBar = new PageScrollBar(_leo);
      add(_pageScrollBar, BorderLayout.PAGE_END);
      
   }
   
   public void contentsChanged(ListDataEvent evt)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
//            _pagePnl.contentsChanged();
            _pageScrollBar.contentsChanged();
         }
      });
   }
   public void intervalAdded(ListDataEvent evt) {}
   public void intervalRemoved(ListDataEvent evt) {}
   
   public void stateChanged(javax.swing.event.ChangeEvent evt) {}
   
   public EObject getEObject() { return (EObject) _leo; }
   
   public EView getInnerView() { return _listView; }
   
   public void detach()
   {
      ((AbstractListEO) _leo).removeListDataListener(this);
      _listView.detach();
   }
   
   public boolean isMinimized() { return false; }

}
