/*
 * Created on Apr 12, 2004
 */
package com.u2d.view.swing.calendar.simple;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import com.u2d.calendar.*;
import com.u2d.pubsub.AppEventType;
import com.u2d.pubsub.AppEventListener;
import com.u2d.pubsub.AppEvent;
import com.u2d.view.swing.calendar.TimeIntervalView;
import com.u2d.view.swing.calendar.PositionedLayout;

/**
 * @author Eitan Suez
 */
public class EventsPnl extends JPanel implements AdjustmentListener, ListDataListener,
  TableColumnModelListener, ChangeListener
{
   private TimeIntervalView _view;
   private CalEventList _calEventList;
   private java.awt.LayoutManager _layout;
   
   public EventsPnl(TimeIntervalView view, CalEventList calEventList)
   {
      _view = view;
      _calEventList = calEventList;

      _layout = new PositionedLayout(_view);
      setLayout(_layout);
      setOpaque(false);

      _view.addAdjustmentListener(this);

      _view.getSpan().addChangeListener(this);

      _calEventList.addListDataListener(this);
      _calEventList.type().addAppEventListener(AppEventType.SAVE, new AppEventListener() {
         public void onEvent(AppEvent evt)
         {
            updateView();  // times may have changed..
         }
      });
      
      updateView();
   }

   public void stateChanged(ChangeEvent e) { updateView(); }

   public void intervalAdded(ListDataEvent e) { updateView(); }
   public void intervalRemoved(ListDataEvent e) { updateView(); }
   public void contentsChanged(ListDataEvent e) { updateView(); }

   public void updateView()
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            removeAll();

            for (Iterator itr = _calEventList.iterator(); itr.hasNext(); )
            {
               CalEvent event = (CalEvent) itr.next();
               
               if (!_view.getSpan().containsOrIntersects(event.timeSpan()))
               {
                  continue;
               }
               
               JComponent comp = (JComponent) event.getCalEventView();
               
               // workaround for mousewheelsupport when hovering over a calevent..
               MouseWheelListener[] listeners = _view.getScrollPane().getMouseWheelListeners();
               for (int i=0; i<listeners.length; i++)
               {
                  comp.addMouseWheelListener(listeners[i]);
               }
               add(comp, event);
            }

            revalidate(); repaint();
         }
      });
   }

   // since the scrollbar is tied to the weekview, need to do some work
   // to ensure that this panel is also driven by it
   public void adjustmentValueChanged(AdjustmentEvent evt)
   {
      _layout.layoutContainer(this);
   }

   // implementation of tablecolumnmodellistener
   public void columnMoved(TableColumnModelEvent evt)
   {
      _layout.layoutContainer(this);
   }
   public void columnMarginChanged(ChangeEvent evt)
   {
      _layout.layoutContainer(this);
   }
   public void columnAdded(TableColumnModelEvent evt) { }
   public void columnRemoved(TableColumnModelEvent evt) { }
   public void columnSelectionChanged(ListSelectionEvent evt) { }

   public void detach()
   {
      _calEventList.removeListDataListener(this);
   }

}
