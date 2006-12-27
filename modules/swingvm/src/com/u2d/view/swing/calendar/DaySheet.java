/*
 * Created on Nov 24, 2004
 */
package com.u2d.view.swing.calendar;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import com.u2d.calendar.Schedule;
import com.u2d.type.atom.DateEO;

/**
 * @author Eitan Suez
 */
public class DaySheet extends JPanel implements Sheet
{
   private JLayeredPane _substrate;
   private DayView _dayView;
   private static final int LAYER_START = 50;
   private int _layer = LAYER_START;
   private java.util.List _eventsPnls = new ArrayList();
   private Map _map = new HashMap();
   
   public DaySheet(DateEO eo)
   {
      _substrate = new JLayeredPane();
      OverlayLayout overlay = new OverlayLayout(_substrate);
      _substrate.setLayout(overlay);
      
      _dayView = new DayView(eo);
      
      _substrate.add(_dayView);
      _substrate.setLayer(_dayView, JLayeredPane.DEFAULT_LAYER.intValue());

      setLayout(new BorderLayout());
      add(_substrate, BorderLayout.CENTER);
   }
   
   public void addSchedule(Schedule schedule)
   {
      _dayView.addSchedule(schedule);
      EventsPnl eventsPnl = new EventsPnl(_dayView, schedule);
      _eventsPnls.add(eventsPnl);
      _substrate.add(eventsPnl);
      _substrate.setLayer(eventsPnl, _layer);
      _map.put(schedule, new Integer(_layer));
      _layer++;
   }

   public void removeSchedule(Schedule schedule)
   {
      _dayView.removeSchedule(schedule);
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
      _dayView.removeSchedules();
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
      _dayView.setScheduleVisible(schedule, visible);
   }
   
   public TimeIntervalView getIntervalView() { return _dayView; }
   
   public void detach()
   {
      _map.clear();
      Iterator itr = _eventsPnls.iterator();
      while (itr.hasNext())
         ((EventsPnl) itr.next()).detach();
   }

   public Dimension getMinimumSize()
   {
      Dimension size = getPreferredSize();
      return (new Dimension((int) (size.width*0.7), 
                            (int) (size.height * 0.7)));
   }

}