package com.u2d.view.wings.list;

import com.u2d.view.ListEView;
import com.u2d.view.EView;
import com.u2d.model.AbstractListEO;
import com.u2d.model.EObject;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ChangeEvent;
import org.wings.SPanel;
import org.wings.SBorderLayout;

/**
 * @author Eitan Suez
 */
public class TitleBarView extends SPanel implements ListEView
{
   private ListTitleView _titleView;
//   private AlternateListView _altView;
   private AbstractListEO _leo;
//   private FindPanel _findPnl;

   public TitleBarView(AbstractListEO leo, EView parentView, EView innerView)
   {
      _leo = leo;

      _titleView = new ListTitleView(_leo, parentView);
      setBackground(leo.type().colorCode());
      setLayout(new SBorderLayout());
      add(_titleView, SBorderLayout.WEST);

//      FormLayout layout = new FormLayout(
//            "left:pref:grow, 3dlu, right:pref",
//            "bottom:pref, pref");
//      setLayout(layout);
//      CellConstraints cc = new CellConstraints();
//
//      _titleView = new ListTitleView(_leo, parentView);
//      add(_titleView, cc.xy(1, 1));
//
//      _altView = getAlternateListView(innerView);
//      if (_altView != null)
//         add(_altView.getControlPane(), cc.xy(3, 1));
//
//      if (!(_leo instanceof RelationalList))
//      {
//         _findPnl = new FindPanel(_leo);
//         add(_findPnl, cc.xyw(1, 2, 3));
//      }
   }


   public EObject getEObject() { return _leo; }
   public void detach()
   {
      _titleView.detach();
//      if (_altView != null)
//         _altView.detach();
//      if (_findPnl != null)
//         _findPnl.detach();
   }


//   private AlternateListView getAlternateListView(EView innerView)
//   {
//      while (innerView != null &&
//             (innerView instanceof CompositeView) &&
//             !(innerView instanceof AlternateListView) )
//      {
//         innerView = ((CompositeView) innerView).getInnerView();
//      }
//
//      if (innerView instanceof AlternateListView)
//         return (AlternateListView) innerView;
//
//      return null;
//   }

   public void contentsChanged(ListDataEvent e) {}
   public void intervalAdded(ListDataEvent e) {}
   public void intervalRemoved(ListDataEvent e) {}
   public void stateChanged(ChangeEvent e) {}

   public boolean isMinimized() { return false; }

}
