/*
 * Created on Nov 22, 2004
 */
package com.u2d.view.swing.calendar;

import java.awt.*;
import java.awt.event.*;
import java.util.Calendar;
import javax.swing.*;
import com.u2d.calendar.*;
import com.u2d.type.atom.*;
import com.u2d.view.swing.atom.DateView2;

/**
 * @author Eitan Suez
 */
public class TimeSheet extends JPanel implements ICalView
{
   private Sheet _daySheet, _weekSheet;
   private final DateEO _eo;
   private JTabbedPane _tabPane;
   private CardLayout _cl;
   private JPanel _lblPnl, _eastPanel;
   private CellResChoice _resolution;
   
   public TimeSheet(DateTimeBounds bounds)
   {
      _eo = bounds.position();
      _daySheet = new DaySheet(bounds);
      _weekSheet = new WeekSheet(bounds);
      _resolution = bounds.resolution();
      setCellResolution(_resolution.timeInterval());
      
      setLayout(new BorderLayout());
      JPanel pnl = new JPanel(new BorderLayout());
      pnl.add(new Heading(), BorderLayout.NORTH);
      pnl.add(body(), BorderLayout.CENTER);
      add(pnl, BorderLayout.CENTER);
      
      _eastPanel = new JPanel(new BorderLayout());
      _eastPanel.add(new DateView2(_eo), BorderLayout.NORTH);
      add(_eastPanel, BorderLayout.EAST);
   }

   public TimeSheet(DateTimeBounds bounds, Component c)
   {
      this(bounds);
      _eastPanel.add(new JScrollPane(c), BorderLayout.SOUTH);
   }

   private Sheet selectedSheet()
   {
      return (Sheet) _tabPane.getSelectedComponent();
   }

   
   class Heading extends JPanel
   {
      public Heading()
      {
         setLayout(new BorderLayout());
         add(new CellResPanel(TimeSheet.this, _resolution), BorderLayout.WEST);
         add(label(), BorderLayout.CENTER);
         add(new NavPanel(TimeSheet.this), BorderLayout.EAST);
      }

      public Dimension getMinimumSize() { return getPreferredSize(); }
   }
   
   private JPanel label()
   {
      JLabel dayLbl = _daySheet.getIntervalView().getLabel();
      JLabel weekLbl = _weekSheet.getIntervalView().getLabel();
      _cl = new CardLayout();
      _lblPnl = new JPanel(_cl);
      _lblPnl.add(dayLbl, "day");
      _lblPnl.add(weekLbl, "week");
      _cl.show(_lblPnl, "week");
      return _lblPnl;
   }
   
   private JComponent body()
   {
      _tabPane = new JTabbedPane();
      _tabPane.add("Week View", (JComponent) _weekSheet);
      _tabPane.add("Day View", (JComponent) _daySheet);
      
      _tabPane.addChangeListener(new javax.swing.event.ChangeListener()
         {
            public void stateChanged(javax.swing.event.ChangeEvent evt)
            {
               Component selected = _tabPane.getSelectedComponent();
               String key = (selected == _weekSheet) ? "week" : "day";
               _cl.show(_lblPnl, key);
            }
         });
      
      return _tabPane;
   }
   
   public void addSchedule(Schedule schedule)
   {
      _daySheet.addSchedule(schedule);
      _weekSheet.addSchedule(schedule);
   }
   public void removeSchedule(Schedule schedule)
   {
      _daySheet.removeSchedule(schedule);
      _weekSheet.removeSchedule(schedule);
   }
   public void clearSchedules()
   {
      _daySheet.clearSchedules();
      _weekSheet.clearSchedules();
   }
   public void setScheduleVisible(Schedule schedule, boolean visible)
   {
      _daySheet.setScheduleVisible(schedule, visible);
      _weekSheet.setScheduleVisible(schedule, visible);
   }
   
   public void shift(boolean forward)
   {
      TimeInterval interval = selectedSheet().getIntervalView().getTimeInterval();
      if (forward)
         _eo.add(interval);
      else
         _eo.subtract(interval);
   }
   public void setCellResolution(TimeInterval interval)
   {
      _weekSheet.getIntervalView().setCellResolution(interval);
      _daySheet.getIntervalView().setCellResolution(interval);
   }
   public void addActionListener(ActionListener l)
   {
      _weekSheet.getIntervalView().addActionListener(l);
      _daySheet.getIntervalView().addActionListener(l);
   }
   
   public void detach()
   {
      _daySheet.detach();
      _weekSheet.detach();
   }


   public Dimension getPreferredSize() { return new Dimension(800,400); }
   public Dimension getMinimumSize() { return new Dimension(500,250); }
   
   public DayView getDayView() { return (DayView) ((DaySheet) _daySheet).getIntervalView(); }
   public WeekView getWeekView() { return (WeekView) ((WeekSheet) _weekSheet).getIntervalView(); }
   
}
