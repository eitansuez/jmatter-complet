/*
 * Created on Apr 12, 2004
 */
package com.u2d.view.swing.calendar;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import com.u2d.calendar.*;

/**
 * @author Eitan Suez
 */
public class EventsPnl extends JPanel implements AdjustmentListener, ListDataListener,
  TableColumnModelListener
{
   private TimeIntervalView _view;
   private Schedule _schedule;
   private java.awt.LayoutManager _layout;

   public EventsPnl(TimeIntervalView view, Schedule schedule)
   {
      _view = view;
      _schedule = schedule;

      _layout = new PositionedLayout(_view);
      setLayout(_layout);
      setOpaque(false);

      _view.addAdjustmentListener(this);
      _schedule.getCalEventList().addListDataListener(this);
      
      updateView();
   }

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

            CalEventList events = _schedule.getCalEventList();
            for (Iterator itr = events.iterator(); itr.hasNext(); )
            {
               CalEvent event = (CalEvent) itr.next();
               
               if (!_view.getSpan().containsOrIntersects(event.timeSpan()))
               {
                  continue;
               }
               
               JComponent comp = (JComponent) event.getCalEventView(_schedule);
               
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

   public Schedule getSchedule() { return _schedule; }

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
      _schedule.getCalEventList().removeListDataListener(this);
   }

}
