/*
 * Created on Apr 14, 2004
 */
package com.u2d.view.swing.calendar;

import com.u2d.calendar.*;
import com.u2d.model.EObject;
import com.u2d.model.Editor;
import com.u2d.model.AbstractListEO;
import com.u2d.type.atom.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListDataEvent;
import java.awt.*;
import java.awt.event.*;
import com.u2d.view.*;
import com.u2d.app.Context;
import com.jgoodies.forms.builder.ButtonStackBuilder;
import java.util.*;

/**
 * @author Eitan Suez
 */
public class CalendarView extends JPanel implements ComplexEView
{
   private Calendrier _calendar;
   private TimeSheet _timeSheet;
   private CBPanel _checkboxPanel;
   private AbstractListEO _schedules;
   private ScheduleListener _scheduleListener;

   public CalendarView(Calendrier calendar)
   {
      _calendar = calendar;
      _checkboxPanel = new CBPanel();
      _timeSheet = new TimeSheet(calendar.position(), _checkboxPanel);

      _schedules = _calendar.schedules();
      _scheduleListener = new ScheduleListener();
      _schedules.addListDataListener(_scheduleListener);
      _scheduleListener.contentsChanged(null);

      setLayout(new BorderLayout());
      add(_timeSheet, BorderLayout.CENTER);

      // double clicking on a cell should initiate the creation of an event..
      _timeSheet.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent evt)
            {
               CalActionEvent tevt = (CalActionEvent) evt;
               Date startDate = tevt.getTime();
               TimeSpan span = new TimeSpan(startDate, CalEvent.DEFAULT_DURATION);
               CalEvent calEvt = _calendar.newDefaultCalEvent(span);
               calEvt.schedulable(tevt.getSchedulable());

               EView calView = calEvt.getMainView();

               if (calEvt.isEditableState() && calView instanceof Editor)
                  calEvt.setEditor((Editor) calView);

               Context.getInstance().swingvmech().displayView(calView, null);
            }
         });
      
      CalendarDropHandler cdh = new CalendarDropHandler(_calendar);
      _timeSheet.getDayView().addDropListener(cdh);
      _timeSheet.getWeekView().addDropListener(cdh);
   }
   

   class ScheduleListener implements ListDataListener
   {
      public void intervalAdded(ListDataEvent e)
      {
         Schedule schedule = null;
         for (int i=e.getIndex0(); i<=e.getIndex1(); i++)
         {
            schedule = (Schedule) _schedules.getElementAt(i);
            _timeSheet.addSchedule(schedule);
            _checkboxPanel.addCheckbox(schedule);
         }
         revalidate();
         repaint();
      }

      public void intervalRemoved(ListDataEvent e)
      {
         Schedule schedule = null;
         for (int i=e.getIndex0(); i<=e.getIndex1(); i++)
         {
            schedule = (Schedule) _schedules.getElementAt(i);
            _timeSheet.removeSchedule(schedule);
            _checkboxPanel.removeCheckbox(i);
         }
         revalidate();
         repaint();
      }

      public void contentsChanged(ListDataEvent e)
      {
         _timeSheet.clearSchedules();

         Schedule schedule = null;
         for (int i=0; i<_schedules.getSize(); i++)
         {
            schedule = (Schedule) _schedules.getElementAt(i);
            _timeSheet.addSchedule(schedule);
            _checkboxPanel.addCheckbox(schedule);
         }
         revalidate();
         repaint();
      }
   }
   
   public void propertyChange(final PropertyChangeEvent evt) {}
   public void stateChanged(javax.swing.event.ChangeEvent evt) {}
   
   public EObject getEObject() { return _calendar; }
   public boolean isMinimized() { return false; }
   
   class CBPanel extends JPanel
   {
      ButtonStackBuilder builder;
      
      CBPanel()
      {
         builder = new ButtonStackBuilder(this);
      }

      void addCheckbox(Schedule schedule)
      {
         addCheckbox( new ScheduleCheckBox(schedule) );
      }
      void addCheckbox(ScheduleCheckBox cb)
      {
         cb.addActionListener(new ActionListener()
               {
                  public void actionPerformed(ActionEvent evt)
                  {
                     ScheduleCheckBox cb = (ScheduleCheckBox) evt.getSource();
                     _timeSheet.setScheduleVisible(cb.getSchedule(),
                           cb.isSelected());
                  }
               });
         builder.addGridded(cb);
      }
      void removeCheckbox(int index)
      {
         remove(index);
      }
      void clearCheckboxes()
      {
         removeAll();
      }
   }
   
   class ScheduleCheckBox extends JCheckBox
   {
      Schedule _schedule;
      ScheduleCheckBox(Schedule schedule)
      {
         super(schedule.title().toString());
         _schedule = schedule;
         
         setBackground(schedule.getColor());
         setSelected(true);
      }
      public Schedule getSchedule() { return _schedule; }
   }
   
   public void detach()
   {
      _timeSheet.detach();
      _schedules.removeListDataListener(_scheduleListener);
   }

}