/*
 * Created on Apr 14, 2004
 */
package com.u2d.view.swing.calendar;

import com.u2d.calendar.*;
import com.u2d.model.EObject;
import com.u2d.model.Editor;
import com.u2d.type.atom.*;
import javax.swing.*;
import java.beans.*;
import java.awt.*;
import java.awt.event.*;
import com.u2d.view.*;
import com.u2d.view.swing.SwingViewMechanism;
import com.u2d.view.swing.find.FindPanel;

import java.util.*;

/**
 * @author Eitan Suez
 */
public class ScheduleView extends JPanel implements ComplexEView
{
   private Schedule _schedule;
   private TimeSheet _timeSheet;

   public ScheduleView(Schedule schedule)
   {
      _schedule = schedule;
      FindPanel findPanel = new FindPanel(_schedule.getCalEventList());
      _timeSheet = new TimeSheet(schedule.bounds(), findPanel);
      _timeSheet.addSchedule(_schedule);
      
      _timeSheet.getDayView().getSpan().addChangeListener(this);
      _timeSheet.getWeekView().getSpan().addChangeListener(this);
      
      setLayout(new BorderLayout());
      add(_timeSheet, BorderLayout.CENTER);
      
      // double clicking on a cell should initiate the creation of an event..
      _timeSheet.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent evt)
            {
               CalActionEvent tevt = ((CalActionEvent) evt);
               Date startDate = tevt.getTime();
               
               TimeSpan span = new TimeSpan(startDate, CalEvent.DEFAULT_DURATION);
               CalEvent calEvt = _schedule.newEvent(span);
               
               EView calView = calEvt.getMainView();

               if (calEvt.isEditableState() && calView instanceof Editor)
                  calEvt.setEditor((Editor) calView);
               
               SwingViewMechanism.getInstance().displayView(calView, null);
            }
         });

      CalendarDropHandler cdh = new CalendarDropHandler(_schedule);
      _timeSheet.getDayView().addDropListener(cdh);
      _timeSheet.getWeekView().addDropListener(cdh);
    
      new Thread() { public void run() {
         _schedule.fetchEvents(_timeSheet.selectedView().getSpan());
         } }.start();
   }
   
   
   public void propertyChange(final PropertyChangeEvent evt) {}

   public void stateChanged(javax.swing.event.ChangeEvent evt)
   {
      TimeSpan span = (TimeSpan) evt.getSource();
      if ( (_timeSheet.getDayView().getSpan() == span && _timeSheet.getDayView().isVisible()) ||
           (_timeSheet.getWeekView().getSpan() == span && _timeSheet.getWeekView().isVisible()) )
      {
         _schedule.fetchEvents(span);
      }
   }

   public EObject getEObject() { return _schedule; }
   public boolean isMinimized() { return false; }
   
   public void detach()
   {
      _timeSheet.getDayView().getSpan().removeChangeListener(this);
      _timeSheet.getWeekView().getSpan().removeChangeListener(this);
      _timeSheet.detach();
   }

}
