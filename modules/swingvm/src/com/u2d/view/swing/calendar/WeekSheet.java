/*
 * Created on Nov 24, 2004
 */
package com.u2d.view.swing.calendar;

import java.util.*;
import java.awt.*;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;
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
   private java.util.List _eventsPnls = new ArrayList();
   private Map _map = new HashMap();
   
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
   
   public void addSchedule(Schedule schedule)
   {
      EventsPnl eventsPnl = new EventsPnl(_weekView, schedule);
      _eventsPnls.add(eventsPnl);
      _substrate.add(eventsPnl);
      _substrate.setLayer(eventsPnl, _layer);
      _map.put(schedule, new Integer(_layer));
      _layer++;
   }
   public void removeSchedule(Schedule schedule)
   {
      int layer = ((Integer) _map.get(schedule)).intValue();
      _map.remove(schedule);
      Component[] components = _substrate.getComponentsInLayer(layer);
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
      Iterator itr = _eventsPnls.iterator();
      EventsPnl eventsPnl = null;
      while (itr.hasNext())
      {
         eventsPnl = (EventsPnl) itr.next();
         eventsPnl.detach();
         _substrate.remove(eventsPnl);
         _map.remove(eventsPnl.getSchedule());
      }
      _layer = LAYER_START;
   }
   public void setScheduleVisible(Schedule schedule, boolean visible)
   {
      int layer = ((Integer) _map.get(schedule)).intValue();
      Component[] comps = _substrate.getComponentsInLayer(layer);
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
      _map.clear();
      for (Iterator itr = _eventsPnls.iterator(); itr.hasNext(); )
         ((EventsPnl) itr.next()).detach();
   }
   
}
