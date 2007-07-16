package com.u2d.view.swing.calendar.fancy;

import com.u2d.calendar.Schedule;
import com.u2d.calendar.CalEvent;
import com.u2d.view.swing.calendar.TimeIntervalView;
import com.u2d.view.swing.calendar.BaseEventsSheet;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Mar 21, 2007
 * Time: 1:50:03 PM
 */
public class EventsSheet
      extends BaseEventsSheet
      implements Sheet
{
   protected static final int LAYER_START = 50;

   protected int _layer = LAYER_START;
   protected java.util.List<EventsPnl> _eventsPnls = new ArrayList<EventsPnl>();
   
   public EventsSheet(TimeIntervalView view)
   {
      super(view);
   }
   
   public synchronized void addSchedule(Schedule schedule)
   {
      EventsPnl eventsPnl = new EventsPnl(_view, schedule);
      _eventsPnls.add(eventsPnl);
      _substrate.add(eventsPnl);
      _substrate.setLayer(eventsPnl, _layer);
      schedule.setLayer(_layer);
      _layer++;
   }

   public void removeSchedule(Schedule schedule)
   {
      Component[] components = _substrate.getComponentsInLayer(schedule.getLayer());
      for (int i=0; i<components.length; i++)
      {
         if (components[i] instanceof EventsPnl)
         {
            EventsPnl eventsPnl = ((EventsPnl) components[i]);
            eventsPnl.detach();
            _substrate.remove(eventsPnl);
         }
      }
   }

   public void clearSchedules()
   {
      for (Iterator itr = _eventsPnls.iterator(); itr.hasNext(); )
      {
         EventsPnl eventsPnl = (EventsPnl) itr.next();
         eventsPnl.detach();
         _substrate.remove(eventsPnl);
      }
      _layer = LAYER_START;
   }


   public void setScheduleVisible(Schedule schedule, boolean visible)
   {
      Component[] comps = _substrate.getComponentsInLayer(schedule.getLayer());
      for (int i=0; i<comps.length; i++)
      {
         comps[i].setVisible(visible);
      }
   }
   
   public synchronized void bringScheduleToFront(CalEvent calEvt)
   {
      Schedule schedule = calEvt.schedulable().schedule();

      int newLayer = _layer;
      
      for (Iterator itr = _eventsPnls.iterator(); itr.hasNext(); )
      {
         EventsPnl pnl = (EventsPnl) itr.next();
         if (pnl.getSchedule() == schedule)
         {
            _substrate.setLayer(pnl, newLayer);
            break;
         }
      }
      schedule.setLayer(newLayer);
      
      _layer++;
   }
   
   public void detach()
   {
      for (Iterator itr = _eventsPnls.iterator(); itr.hasNext(); )
         ((EventsPnl) itr.next()).detach();
   }

}
