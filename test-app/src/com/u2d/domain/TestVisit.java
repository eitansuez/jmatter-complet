/*
 * Created on Apr 26, 2004
 */
package com.u2d.domain;

import com.u2d.calendar.*;
import com.u2d.element.CommandInfo;
import com.u2d.model.Title;
import com.u2d.pattern.*;
import com.u2d.type.atom.*;
import javax.persistence.Entity;

/**
 * @author Eitan Suez
 */
@Entity
public class TestVisit extends CalEvent
{
   private transient State _scheduledState;
   private transient State _confirmedState;
   private transient State _inProgressState;
   private transient State _billedState;
   {
      _scheduledState = new ScheduledState();
      _confirmedState = new ConfirmedState();
      _inProgressState = new InProgressState();
      _billedState = new BilledState();
      State _paidState = new PaidState();
      State _archivedState = new ArchivedState();

      _stateMap.put(_scheduledState.getName(), _scheduledState);
      _stateMap.put(_confirmedState.getName(), _confirmedState);
      _stateMap.put(_inProgressState.getName(), _inProgressState);
      _stateMap.put(_billedState.getName(), _billedState);
      _stateMap.put(_paidState.getName(), _paidState);
      _stateMap.put(_archivedState.getName(), _archivedState);
   }

   private final StringEO _patientName = new StringEO();
   private final TimeSpan _timeSpan = new TimeSpan();
   private final StringEO _status = new StringEO("Scheduled");
   
   public static final String[] fieldOrder = {"patientName", "timeSpan", "status"};
   public static final String[] readOnly = {"status"};
   
   public TestVisit()
   {
      super();
   }
   
   public StringEO getPatientName() { return _patientName; }
   public TimeSpan getTimeSpan() { return _timeSpan; }
   public StringEO getStatus() { return _status; }

   public String timeSpanFieldName() { return "timeSpan"; }
   
   public void restoreState()
   {
      String statusName = getStatus().stringValue();
      setState((State) _stateMap.get(statusName), true);
   }
   
   public Title title()
   {
      return _patientName.title().appendParens(_status);
   }
   public Title calTitle() { return title(); }
   
   public void setStartState() { setState(_scheduledState); }

   public class ScheduledState extends ReadState
   {
      public String getName() { return "Scheduled"; }
      public void commandConfirm(CommandInfo cmdInfo)
      {
         setState(_confirmedState);
         updateStatus("Confirmed");
      }
   }
   public class ConfirmedState extends ReadState
   {
      public String getName() { return "Confirmed"; }
      public void commandAdmit(CommandInfo cmdInfo)
      {
         setState(_inProgressState);
         updateStatus("InProgress");
      }
   }
   public class InProgressState extends ReadState
   {
      public String getName() { return "InProgress"; }
      public void commandBill(CommandInfo cmdInfo)
      {
         setState(_billedState);
         updateStatus("Billed");
      }
   }
   public class BilledState extends ReadState
   {
      public String getName() { return "Billed"; }
//      public Payment commandAcceptPayment(CommandInfo cmdInfo)
//      {
//         return new Payment(getBill());
//      }
   }
   public class PaidState extends ReadState
   {
      public String getName() { return "Paid"; }
   }
   public class ArchivedState extends ReadState
   {
      public String getName() { return "Archived"; }
   }

   
   private void updateStatus(String value)
   {
      _status.setValue(value);
      save();
   }
   
   public Schedulable schedulable() { return null; }
   public void schedulable(Schedulable s) {}
}
