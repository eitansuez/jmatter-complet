/*
 * Created on Apr 12, 2004
 */
package com.u2d.view.swing.calendar;

import com.u2d.view.swing.SwingViewMechanism;
import java.awt.Cursor;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import com.u2d.calendar.*;

/**
 * @author Eitan Suez
 */
public class EventsPnl extends JPanel implements AdjustmentListener, ChangeListener,
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
      
      // these two will go away in favor of being a listdatalistener on the list
      _view.addChangeListener(this);
      _schedule.addChangeListener(this);
      // somewhere else keep track of navigation sending message to list to update
      // itself (geteventsintimespan -> setitems -> firelistdataevent )
      
      // also, notion of multiple schedules is flawed.
      // do one query and fetch all matching events.
      // want to color code them?  that's fine.
      // want to put them in a separate layer?  that's fine.
      // however, i don't think that there should be > 1 eventspanels.
      
      update();
   }

   public void stateChanged(ChangeEvent evt)
   {
      SwingUtilities.invokeLater(new Runnable() {
         public void run()
         {
            update();
         }
      });
   }

   public Schedule getSchedule() { return _schedule; }

   // lots of work to do here.
   // this is backwards.  eventspnl will be made a listdatalistener on
   // schedule.  when schedule changes, this guy will simply redraw.  no
   // fetching from the db on the edt (!).
   // schedule should somehow mirror a paged list (see criterialisteo)
   // where you pass it a timespan and it fetches the events for that
   // span and sets its items, which fires the contentschanged event.
   // also:  dissociate weekview from day view.  when scroll one, don't
   // scroll the other.
   public void update()
   {
      removeAll();

      SwingViewMechanism.getInstance().setCursor(
              Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

      try
      {
         com.u2d.type.atom.TimeSpan newspan = _view.getSpan();
         final java.util.List events = _schedule.getEventsInTimeSpan(newspan);
         for (Iterator itr = events.iterator(); itr.hasNext(); )
         {
            CalEvent event = (CalEvent) itr.next();
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
      finally
      {
         SwingViewMechanism.getInstance().setCursor(Cursor.getDefaultCursor());
      }
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
      _schedule.removeChangeListener(this);
   }

}
