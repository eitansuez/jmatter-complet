/*
 * Created on Nov 22, 2004
 */
package com.u2d.view.swing.calendar.fancy;

import java.awt.*;
import java.awt.event.*;
import java.util.Date;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import com.u2d.calendar.*;
import com.u2d.type.atom.*;
import com.u2d.view.swing.atom.DateView2;
import com.u2d.view.swing.CommandAdapter;
import com.u2d.view.swing.AppLoader;
import com.u2d.view.swing.calendar.*;
import com.u2d.view.EView;
import com.u2d.ui.CustomTabbedPane;
import com.u2d.model.ComplexType;
import com.u2d.element.Command;
import com.u2d.pattern.Callback;

/**
 * @author Eitan Suez
 */
public class TimeSheet extends JPanel implements ChangeListener, ITimeSheet
{
   private Sheet _daySheet, _weekSheet;
   private JTabbedPane _tabPane;
   private CardLayout _cardLayout;
   private JPanel _lblPnl;
   private JPanel _eastPanel;
   private final DateEO _position;
   private EventManager _eventMgr;

   public TimeSheet(EventManager mgr, DateTimeBounds bounds)
   {
      _eventMgr = mgr;
      _position = bounds.position();
      _daySheet = new DayEventsSheet(this, bounds);
      _weekSheet = new WeekEventsSheet(this, bounds);

      // keep day and week view scroll bars in sync (share scroll model)
      getWeekView().getScrollPane().getVerticalScrollBar().setModel(getDayView().getScrollPane().getVerticalScrollBar().getModel());
      
      setCellResolution(bounds.resolution());
      
      setLayout(new BorderLayout());
      JPanel pnl = new JPanel(new BorderLayout());
      pnl.add(heading(), BorderLayout.PAGE_START);
      pnl.add(body(), BorderLayout.CENTER);
      add(pnl, BorderLayout.CENTER);
      
      _eastPanel = new JPanel(new BorderLayout());
      _eastPanel.setOpaque(false);
      _eastPanel.add(new DateView2(_position), BorderLayout.PAGE_START);
      add(_eastPanel, BorderLayout.LINE_END);
      
      // double clicking on a cell should initiate the creation of an event..
      addActionListener(new ActionListener()
      {
         public void actionPerformed(final ActionEvent evt)
         {
            ComplexType eventType = _eventMgr.eventType();
            Command cmd = eventType.command("New");
            
            cmd.setCallback(new Callback()
            {
               public void call(Object obj)
               {
                  CalEvent calEvt = (CalEvent) obj;
                  
                  CalActionEvent tevt = (CalActionEvent) evt;
                  Date startDate = tevt.getTime();
                  TimeSpan span = new TimeSpan(startDate, CalEvent.DEFAULT_DURATION);
                  Schedulable sched = tevt.getSchedulable();
                  
                  calEvt.timeSpan(span);
                  if (sched != null)
                  {
                     calEvt.schedulable(sched);
                  }
               }
            });
            
            CommandAdapter ca = new CommandAdapter(cmd, eventType, (EView) TimeSheet.this.getParent());
            ca.actionPerformed(evt);
         }
      });
      
      CalendarDropHandler cdh = new CalendarDropHandler(_eventMgr);
      getDayView().addDropListener(cdh);
      getWeekView().addDropListener(cdh);
      
      getWeekView().getSpan().addChangeListener(this);
      
      AppLoader.getInstance().newThread(new Runnable()
      {
         public void run() {
            stateChanged(null);
         }
      }).start();
   }
   
   public TimeSheet(EventManager mgr, DateTimeBounds bounds, Component c)
   {
      this(mgr, bounds);
      _eastPanel.add(new JScrollPane(c), BorderLayout.PAGE_END);
   }

   public DateEO currentPosition() { return _position; }
   
   private JPanel heading()
   {
      JPanel heading = new JPanel();
      heading.setLayout(new BorderLayout());
      heading.add(new CellResPanel(TimeSheet.this), BorderLayout.LINE_START);
      heading.add(label(), BorderLayout.CENTER);
      heading.add(new NavPanel(TimeSheet.this), BorderLayout.LINE_END);
      return heading;
   }
   
   private JPanel label()
   {
      JLabel dayLbl = _daySheet.getIntervalView().getLabel();
      JLabel weekLbl = _weekSheet.getIntervalView().getLabel();
      _cardLayout = new CardLayout();
      _lblPnl = new JPanel(_cardLayout);
      _lblPnl.add(dayLbl, "day");
      _lblPnl.add(weekLbl, "week");
      _cardLayout.show(_lblPnl, "week");
      return _lblPnl;
   }
   
   private JComponent body()
   {
      _tabPane = new CustomTabbedPane();
      _tabPane.add("Week View", (JComponent) _weekSheet);
      _tabPane.add("Day View", (JComponent) _daySheet);
      
      _tabPane.addChangeListener(new javax.swing.event.ChangeListener()
         {
            public void stateChanged(javax.swing.event.ChangeEvent evt)
            {
               Component selected = _tabPane.getSelectedComponent();
               String key = (selected == _weekSheet) ? "week" : "day";
               _cardLayout.show(_lblPnl, key);
            }
         });
      
      return _tabPane;
   }


   public void stateChanged(ChangeEvent evt)
   {
      _eventMgr.fetchEvents(getWeekView().getSpan());
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
      TimeInterval interval = selectedView().getTimeInterval();
      if (forward)
         _position.add(interval);
      else
         _position.subtract(interval);
   }
   
   public void addActionListener(ActionListener l)
   {
      getDayView().addActionListener(l);
      getWeekView().addActionListener(l);
   }
   
   protected CellResChoice _cellRes = CellResChoice.THIRTY_MINUTES;
   public CellResChoice getCellResolution() { return _cellRes; }
   public void setCellResolution(CellResChoice choice)
   {
      CellResChoice oldValue = _cellRes;
      _cellRes = choice;
      firePropertyChange("cellResolution", oldValue, _cellRes);
   }

   public DayView getDayView() { return (DayView) _daySheet.getIntervalView(); }
   public WeekView getWeekView() { return (WeekView) _weekSheet.getIntervalView(); }

   public TimeIntervalView selectedView() { return selectedSheet().getIntervalView(); }
   private Sheet selectedSheet() { return (Sheet) _tabPane.getSelectedComponent(); }

   public void detach()
   {
      getDayView().getSpan().removeChangeListener(this);
      getWeekView().getSpan().removeChangeListener(this);
      _daySheet.detach();
      _weekSheet.detach();
   }


   public Dimension getPreferredSize() { return new Dimension(800,400); }
   public Dimension getMinimumSize() { return new Dimension(500,250); }
   
}
