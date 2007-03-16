/*
 * Created on Nov 24, 2004
 */
package com.u2d.view.swing.calendar;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import com.u2d.calendar.Schedule;
import com.u2d.calendar.DateTimeBounds;

/**
 * @author Eitan Suez
 */
public class WeekSheet extends JPanel implements Sheet
{
   private JLayeredPane _substrate;
   private WeekView _weekView;
   private static final int LAYER_START = 50;
   private int _layer = LAYER_START;
   private java.util.List<EventsPnl> _eventsPnls = new ArrayList<EventsPnl>();
   
   public WeekSheet(DateTimeBounds bounds)
   {
      _substrate = new JLayeredPane();
      OverlayLayout overlay = new OverlayLayout(_substrate);
      _substrate.setLayout(overlay);
      
      _weekView = new WeekView(bounds);
      
      _substrate.add(_weekView);
      _substrate.setLayer(_weekView, JLayeredPane.DEFAULT_LAYER.intValue());
      
      setLayout(new BorderLayout());
      add(_substrate, BorderLayout.CENTER);
   }
   
   public synchronized void addSchedule(Schedule schedule)
   {
      EventsPnl eventsPnl = new EventsPnl(_weekView, schedule);
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
      EventsPnl eventsPnl = null;
      for (Iterator itr = _eventsPnls.iterator(); itr.hasNext(); )
      {
         eventsPnl = (EventsPnl) itr.next();
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

   public Dimension getMinimumSize()
   {
      Dimension size = getPreferredSize();
      return (new Dimension((int) (size.width*0.7), 
                            (int) (size.height * 0.7)));
   }

   public TimeIntervalView getIntervalView() { return _weekView; }

   
   public void detach()
   {
      for (Iterator itr = _eventsPnls.iterator(); itr.hasNext(); )
         ((EventsPnl) itr.next()).detach();
   }
   
}
