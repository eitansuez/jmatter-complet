/*
 * Created on Apr 6, 2005
 */
package com.u2d.view.swing.list;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListDataEvent;
import com.u2d.model.AbstractListEO;
import com.u2d.model.EObject;
import com.u2d.ui.GradientPanel;
import com.u2d.view.*;
import com.u2d.view.swing.find.FindPanel;
import com.u2d.find.QueryReceiver;
import net.miginfocom.swing.MigLayout;

/**
 * @author Eitan Suez
 */
public class TitleBarView extends GradientPanel implements ListEView
{
   private ListTitleView _titleView;
   private AlternateListView _altView;
   private AbstractListEO _leo;
   private FindPanel _findPnl;
   
   public TitleBarView(AbstractListEO leo, EView parentView, EView innerView)
   {
      super(leo.type().colorCode(), false);
      _leo = leo;
      
      MigLayout layout = new MigLayout("fill, insets 0", "[left][right]", "[bottom][center]");
      setLayout(layout);

      _titleView = new ListTitleView(_leo, parentView);
      add(_titleView, "growx");
      
      _altView = getAlternateListView(innerView);
      if (_altView != null)
         add(_altView.getControlPane());

      if (_leo instanceof QueryReceiver)
      {
         _findPnl = new FindPanel((QueryReceiver) _leo);
         add(_findPnl, "newline, span, growx");
      }
   }
   

   public EObject getEObject() { return _leo; }
   public void detach()
   {
      _titleView.detach();
      if (_altView != null)
         _altView.detach();
      if (_findPnl != null)
         _findPnl.detach();
   }
   
   
   private AlternateListView getAlternateListView(EView innerView)
   {
      while (innerView != null &&
             (innerView instanceof CompositeView) &&
             !(innerView instanceof AlternateListView) )
      {
         innerView = ((CompositeView) innerView).getInnerView();
      }
      
      if (innerView instanceof AlternateListView)
         return (AlternateListView) innerView;
      
      return null;
   }

   public void contentsChanged(ListDataEvent e) {}
   public void intervalAdded(ListDataEvent e) {}
   public void intervalRemoved(ListDataEvent e) {}
   public void stateChanged(ChangeEvent e) {}
   
   public boolean isMinimized() { return false; }

}
