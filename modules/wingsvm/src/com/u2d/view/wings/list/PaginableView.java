package com.u2d.view.wings.list;

import com.u2d.view.ListEView;
import com.u2d.view.CompositeView;
import com.u2d.view.EView;
import com.u2d.list.Paginable;
import com.u2d.model.AbstractListEO;
import com.u2d.model.EObject;
import javax.swing.event.ListDataEvent;
import org.wings.SPanel;
import org.wings.SBorderLayout;
import org.wings.SComponent;

/**
 * @author Eitan Suez
 */
public class PaginableView extends SPanel implements ListEView, CompositeView
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

      setLayout(new SBorderLayout());

      add((SComponent) _listView, SBorderLayout.CENTER);

      _pageScrollBar = new PageScrollBar(_leo);
      add(_pageScrollBar, SBorderLayout.SOUTH);

   }

   public void contentsChanged(ListDataEvent evt)
   {
//      _pagePnl.contentsChanged();
      _pageScrollBar.contentsChanged();
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
