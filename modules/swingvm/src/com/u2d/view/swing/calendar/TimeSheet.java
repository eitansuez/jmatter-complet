/*
 * Created on Nov 22, 2004
 */
package com.u2d.view.swing.calendar;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.u2d.calendar.*;
import com.u2d.type.atom.*;
import com.u2d.view.swing.atom.DateView2;
import com.u2d.view.swing.find.FindPanel;
import com.u2d.ui.CustomTabbedPane;

/**
 * @author Eitan Suez
 */
public class TimeSheet extends JPanel
{
   private Sheet _daySheet, _weekSheet;
   private JTabbedPane _tabPane;
   private CardLayout _cardLayout;
   private JPanel _lblPnl, _eastPanel, _northPanel;
   private CellResPanel _cellResPanel;
   private final DateEO _position;

   public TimeSheet(DateTimeBounds bounds)
   {
      _position = bounds.position();
      _daySheet = new DaySheet(bounds);
      _weekSheet = new WeekSheet(bounds);
      
      _weekSheet.getIntervalView().setCellResolution(bounds.resolution());
      _daySheet.getIntervalView().setCellResolution(bounds.resolution());
      
      setLayout(new BorderLayout());
      JPanel pnl = new JPanel(new BorderLayout());
      pnl.add(northPanel(), BorderLayout.NORTH);
      pnl.add(body(), BorderLayout.CENTER);
      add(pnl, BorderLayout.CENTER);
      
      _eastPanel = new JPanel(new BorderLayout());
      _eastPanel.add(new DateView2(_position), BorderLayout.NORTH);
      add(_eastPanel, BorderLayout.EAST);
   }
   
   public TimeSheet(DateTimeBounds bounds, Component c)
   {
      this(bounds);
      _eastPanel.add(new JScrollPane(c), BorderLayout.SOUTH);
   }
   public TimeSheet(DateTimeBounds bounds, FindPanel findPanel)
   {
      this(bounds);
      _northPanel.add(findPanel, BorderLayout.SOUTH);
   }

   public DateEO currentPosition() { return _position; }

   
   private JPanel northPanel()
   {
      _northPanel = new JPanel(new BorderLayout());
      _northPanel.add(heading(), BorderLayout.CENTER);
      return _northPanel;
   }
   private JPanel heading()
   {
      JPanel heading = new JPanel();
      heading.setLayout(new BorderLayout());
      _cellResPanel = new CellResPanel(TimeSheet.this);
      heading.add(_cellResPanel, BorderLayout.WEST);
      heading.add(label(), BorderLayout.CENTER);
      heading.add(new NavPanel(TimeSheet.this), BorderLayout.EAST);
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
               TimeIntervalView selectedView = selectedView();
               _cellResPanel.bindTo(selectedView);
               if (selectedView == _weekSheet.getIntervalView())
                 selectedView.getSpan().fireStateChanged();
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
      TimeInterval interval = selectedView().getTimeInterval();
      if (forward)
         _position.add(interval);
      else
         _position.subtract(interval);
   }
   
   public CellResChoice getCellResolution() { return selectedView().getCellResolution(); }
   public void setCellResolution(CellResChoice res) { selectedView().setCellResolution(res); }
   public void addActionListener(ActionListener l)
   {
      getDayView().addActionListener(l);
      getWeekView().addActionListener(l);
   }
   
   public DayView getDayView() { return (DayView) ((DaySheet) _daySheet).getIntervalView(); }
   public WeekView getWeekView() { return (WeekView) ((WeekSheet) _weekSheet).getIntervalView(); }

   public TimeIntervalView selectedView() { return selectedSheet().getIntervalView(); }
   private Sheet selectedSheet() { return (Sheet) _tabPane.getSelectedComponent(); }

   public void detach()
   {
      _daySheet.detach();
      _weekSheet.detach();
   }


   public Dimension getPreferredSize() { return new Dimension(800,400); }
   public Dimension getMinimumSize() { return new Dimension(500,250); }
   
   
}
