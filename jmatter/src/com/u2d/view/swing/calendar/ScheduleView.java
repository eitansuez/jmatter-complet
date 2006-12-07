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
import com.u2d.app.Context;

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
      _timeSheet = new TimeSheet(schedule.position());
      _timeSheet.addSchedule(_schedule);
      
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
               
               Context.getInstance().swingvmech().displayView(calView, null);
            }
         });

      CalendarDropHandler cdh = new CalendarDropHandler(_schedule);
      _timeSheet.getDayView().addDropListener(cdh);
      _timeSheet.getWeekView().addDropListener(cdh);
   }
   
   public void propertyChange(final PropertyChangeEvent evt) {}
   public void stateChanged(javax.swing.event.ChangeEvent evt) {}
   
   public EObject getEObject() { return _schedule; }
   public boolean isMinimized() { return false; }
   
   public void detach()
   {
      _timeSheet.detach();
   }
   

}
