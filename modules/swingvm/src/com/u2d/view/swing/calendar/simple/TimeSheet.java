/*
 * Created on Nov 22, 2004
 */
package com.u2d.view.swing.calendar.simple;

import java.awt.*;
import java.awt.event.*;
import java.util.Date;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import com.u2d.calendar.*;
import com.u2d.type.atom.*;
import com.u2d.view.swing.atom.DateView2;
import com.u2d.view.swing.atom.DateView3;
import com.u2d.view.swing.CommandAdapter;
import com.u2d.view.swing.AppLoader;
import com.u2d.view.swing.calendar.*;
import com.u2d.view.EView;
import com.u2d.ui.CustomTabbedPane;
import com.u2d.element.Command;
import com.u2d.pattern.Callback;
import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.gui.form.FormAccessor;

/**
 * @author Eitan Suez
 */
public class TimeSheet extends JPanel implements ChangeListener, ITimeSheet
{
   private EventsSheet _daySheet, _weekSheet;
   private JTabbedPane _tabPane;
   private CardLayout _cardLayout;
   private JPanel _lblPnl;
   private final DateEO _position;
   private CalEventList _list;

   public TimeSheet(CalEventList list, DateTimeBounds bounds)
   {
      _list = list;
      
      _position = bounds.position();
      _daySheet = new EventsSheet(new DayView(this, bounds), _list);
      _weekSheet = new EventsSheet(new WeekView(this, bounds), _list);
      
      // keep day and week view scroll bars in sync (share scroll model)
      getWeekView().getScrollPane().getVerticalScrollBar().setModel(
            getDayView().getScrollPane().getVerticalScrollBar().getModel());

      setCellResolution(bounds.resolution());
      
      layMeOut();
      
      // double clicking on a cell should initiate the creation of an event..
      addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent evt)
         {
            Command cmd = _list.command("New");
            // .. unless command is forbidden / unauthorized
            if (cmd.isForbidden(_list))
            {
               return;
            }

            CalActionEvent tevt = (CalActionEvent) evt;
            Date startDate = tevt.getTime();
            final TimeSpan span = new TimeSpan(startDate, CalEvent.DEFAULT_DURATION);

            cmd.setCallback(new Callback()
            {
               public void call(Object obj)
               {
                  CalEvent calEvt = (CalEvent) obj;
                  calEvt.timeSpan(span);
               }
            });
            
            CommandAdapter ca = new CommandAdapter(cmd, _list, (EView) TimeSheet.this.getParent());
            ca.actionPerformed(evt);
         }
      });
      
      CalendarDropHandler cdh = new CalendarDropHandler(_list);
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

   private void layMeOut()
   {
      FormPanel formPanel = new FormPanel("com/u2d/view/swing/calendar/simple/TimeSheet.jfrm");
      FormAccessor accessor = formPanel.getFormAccessor();
      accessor.replaceBean("title", label());
      accessor.replaceBean("navPanel", new NavPanel(TimeSheet.this));
      accessor.replaceBean("body", body());
      
      FormAccessor nestedForm = formPanel.getFormAccessor("sidePnl");
      nestedForm.replaceBean("dateChooser", new DateView3(_position));
      nestedForm.replaceBean("cellResPnl", new CellResPanel(TimeSheet.this));
      
      setLayout(new BorderLayout());
      add(formPanel, BorderLayout.CENTER);
   }


   public DateEO currentPosition() { return _position; }
   
   private JPanel label()
   {
      JLabel dayLbl = _daySheet.getIntervalView().getLabel();
      JLabel weekLbl = _weekSheet.getIntervalView().getLabel();
      _cardLayout = new CardLayout();
      _lblPnl = new JPanel(_cardLayout);
      _lblPnl.setOpaque(false);
      _lblPnl.add(dayLbl, "day");
      _lblPnl.add(weekLbl, "week");
      _cardLayout.show(_lblPnl, "week");
      return _lblPnl;
   }
   
   private JComponent body()
   {
      _tabPane = new CustomTabbedPane();
      _tabPane.add("Week View", _weekSheet);
      _tabPane.add("Day View", _daySheet);
      
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
      _list.fetchEvents(getWeekView().getSpan());
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
   private EventsSheet selectedSheet() { return (EventsSheet) _tabPane.getSelectedComponent(); }

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
