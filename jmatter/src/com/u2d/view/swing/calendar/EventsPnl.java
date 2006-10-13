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
import com.u2d.app.Context;

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
      _view.addChangeListener(this);
      _schedule.addChangeListener(this);

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

   public void update()
   {
      removeAll();

      Context.getInstance().swingvmech().setCursor(
              Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

      try
      {
         com.u2d.type.atom.TimeSpan newspan = _view.getSpan();
         final java.util.List events = _schedule.getEventsInTimeSpan(newspan);
         for (Iterator itr = events.iterator(); itr.hasNext(); )
         {
            CalEvent event = (CalEvent) itr.next();
            JComponent comp = (JComponent) event.getCalEventView(_schedule);
            add(comp, event);
         }

         revalidate(); repaint();
      }
      finally
      {
         Context.getInstance().swingvmech().setCursor(Cursor.getDefaultCursor());
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
