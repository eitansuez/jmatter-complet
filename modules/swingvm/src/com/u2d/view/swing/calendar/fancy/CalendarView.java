/*
 * Created on Apr 14, 2004
 */
package com.u2d.view.swing.calendar.fancy;

import com.u2d.calendar.*;
import com.u2d.model.EObject;
import com.u2d.model.AbstractListEO;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListDataEvent;
import java.awt.*;
import java.awt.event.*;
import com.u2d.view.*;
import net.miginfocom.swing.MigLayout;

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

      _schedules = _calendar.schedules();
      _timeSheet = new TimeSheet(_calendar, _calendar.bounds(), _checkboxPanel);

      _scheduleListener = new ScheduleListener();
      _schedules.addListDataListener(_scheduleListener);
      _scheduleListener.contentsChanged(null);

      setLayout(new BorderLayout());
      add(_timeSheet, BorderLayout.CENTER);
   }
   

   class ScheduleListener implements ListDataListener
   {
      public void intervalAdded(ListDataEvent e)
      {
         Schedule schedule;
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
         Schedule schedule;
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

         Schedule schedule;
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
      CBPanel()
      {
         MigLayout layout = new MigLayout("insets 0, flowy, gapy 0, fillx");
         setLayout(layout);
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
         add(cb, "growx");
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
