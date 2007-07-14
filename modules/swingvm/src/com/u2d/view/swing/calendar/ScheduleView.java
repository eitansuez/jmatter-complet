/*
 * Created on Apr 14, 2004
 */
package com.u2d.view.swing.calendar;

import com.u2d.calendar.*;
import com.u2d.model.EObject;
import javax.swing.*;
import java.beans.*;
import java.awt.*;
import com.u2d.view.*;

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
      _timeSheet = new TimeSheet(_schedule, _schedule.bounds());
      _timeSheet.addSchedule(_schedule);
      
      setLayout(new BorderLayout());
      add(_timeSheet, BorderLayout.CENTER);
   }
   
   
   public void propertyChange(final PropertyChangeEvent evt) {}
   public void stateChanged(javax.swing.event.ChangeEvent evt) {}

   public EObject getEObject() { return _schedule; }
   public boolean isMinimized() { return false; }
   public void detach() { _timeSheet.detach(); }

}
