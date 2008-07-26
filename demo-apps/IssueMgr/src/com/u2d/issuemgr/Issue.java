package com.u2d.issuemgr;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.model.ComplexType;
import com.u2d.type.atom.StringEO;
import com.u2d.type.atom.TextEO;
import com.u2d.type.atom.IntEO;
import com.u2d.type.composite.Note;
import com.u2d.type.composite.LoggedEvent;
import com.u2d.list.RelationalList;
import com.u2d.app.User;
import com.u2d.element.CommandInfo;
import com.u2d.pattern.State;
import com.u2d.pubsub.AppEventListener;
import com.u2d.pubsub.AppEvent;
import static com.u2d.pubsub.AppEventType.*;
import com.u2d.reflection.Cmd;
import com.u2d.reflection.Arg;
import javax.persistence.Entity;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Nov 22, 2005
 * Time: 8:54:01 PM
 */
@Entity
public class Issue extends AbstractComplexEObject
{
   private final IssueState _status = new IssueState(NEW);
   private final StringEO _title = new StringEO();
   private final TextEO _description = new TextEO();

   // a loose definition for these -- just using a numeric 
   // instead of an enumeration.
   private final IntEO _priority = new IntEO();
   private final IntEO _severity = new IntEO();
   private IssueCategory _category;

   private Issue _dependsOn;

   private final RelationalList _notes = new RelationalList(Note.class);
   public static Class notesType = Note.class;

   private User _openedBy;
   private User _assignedTo;

   private final RelationalList _history = new RelationalList(LoggedEvent.class);
   public static Class historyType = LoggedEvent.class;

   public static String[] fieldOrder = {"status", "title", "description", "notes",
         "openedBy", "assignedTo", "history", "severity", "priority", "category"};
   public static String[] readOnly = {"openedBy"};
   public static String defaultSearchPath = "title";
   
   
   public Issue() {}

   public void onBeforeCreate()
   {
      super.onBeforeCreate();
      setOpenedBy(currentUser());
   }

   public IssueState getStatus() { return _status; }
   public StringEO getTitle() { return _title; }
   public TextEO getDescription() { return _description; }
   public RelationalList getNotes() { return _notes; }

   public User getOpenedBy() { return _openedBy; }
   public void setOpenedBy(User user)
   {
      User oldValue = _openedBy;
      _openedBy = user;
      firePropertyChange("openedBy", oldValue, _openedBy);
   }

   public User getAssignedTo() { return _assignedTo; }
   public void setAssignedTo(User user)
   {
      User oldValue = _assignedTo;
      _assignedTo = user;
      firePropertyChange("assignedTo", oldValue, _assignedTo);
   }
   public void associateAssignedTo(User user)
   {
      setAssignedTo(user);

      if (_assignedTo != null && !_assignedTo.isEmpty())
      {
         if (isEditableState())
         {
            addAppEventListener(CREATE, new AppEventListener()
            {
               public void onEvent(AppEvent appEvent)
               {
                  transition(_assignedState, makeLog("Assigned to "+_assignedTo));
               }
            });
         }
         else
         {
            transition(_assignedState, makeLog("Assigned to "+_assignedTo));
         }
      }
   }

   public RelationalList getHistory() { return _history; }

   public IntEO getPriority() { return _priority; }
   public IntEO getSeverity() { return _severity; }

   public IssueCategory getCategory() { return _category; }
   public void setCategory(IssueCategory category)
   {
      IssueCategory oldValue = _category;
      _category = category;
      firePropertyChange("category", oldValue, _category);
   }

   public Issue getDependsOn() { return _dependsOn; }
   public void setDependsOn(Issue issue)
   {
      Issue oldValue = _dependsOn;
      _dependsOn = issue;
      firePropertyChange("dependsOn", oldValue, _dependsOn);
   }

   public Title title()
   {
      return _title.title().appendParens(""+getID());
   }

   static
   {
      ComplexType type = ComplexType.forClass(Issue.class);
      type.command("Accept", AssignedState.class).setOwner(type.field("assignedTo"));
      type.command("Fix", AcceptedState.class).setOwner(type.field("assignedTo"));
      type.command("RejectFix", FixedState.class).setOwner(type.field("openedBy"));
      type.command("Close", FixedState.class).setOwner(type.field("openedBy"));
   }


   // *****************************************************************
   // state-specific logic; working on reducing the amount of rote-code

   static final String NEW = "New";
   static final String ASSIGNED = "Assigned";
   static final String ACCEPTED = "Accepted";
   static final String FIXED = "Fixed";
   static final String CLOSED = "Closed";

   private transient final State _newState, _assignedState, _acceptedState,
         _fixedState, _closedState;
   {
      _newState = new NewState();
      _assignedState = new AssignedState();
      _acceptedState = new AcceptedState();
      _fixedState = new FixedState();
      _closedState = new ClosedState();
      _stateMap.put(_newState.getName(), _newState);
      _stateMap.put(_assignedState.getName(), _assignedState);
      _stateMap.put(_acceptedState.getName(), _acceptedState);
      _stateMap.put(_fixedState.getName(), _fixedState);
      _stateMap.put(_closedState.getName(), _closedState);
   }

   public class NewState extends ReadState {}

   public class AssignedState extends ReadState
   {
      @Cmd(mnemonic='a')
      public void Accept(CommandInfo cmdInfo)
      {
         transition(_acceptedState, makeLog("Issue accepted by developer"));
      }
   }

   public class AcceptedState extends ReadState
   {
      @Cmd
      public void Fix(CommandInfo cmdInfo,
                      @Arg("Fix") StringEO fix, 
                      @Arg("Description") TextEO description)
      {
         transition(_fixedState, makeLog("Fix: "+fix.stringValue(), description));
      }
   }
   public class FixedState extends ReadState
   {
      @Cmd
      public void RejectFix(CommandInfo cmdInfo, 
                            @Arg("Explanation") TextEO explanation)
      {
         transition(_acceptedState, makeLog("Fix rejected", explanation));
      }
      @Cmd
      public void Close(CommandInfo cmdInfo, 
                        @Arg("Explanation") TextEO explanation)
      {
         transition(_closedState, makeLog("Issue Closed", explanation));
      }
   }

   public class ClosedState extends ReadState {}


   public State startState() { return _newState; }
   public State restoredState() { return (State) _stateMap.get(getStatus().code()); }


   private void transition(State state, LoggedEvent evt)
   {
      _history.add(evt);
      setState(state, true);
      _status.setValue(state.getName());
      persistor().updateAssociation(this, evt);
   }

   // end of state-specific code
   // **************************


   private LoggedEvent makeLog(String msg)
   {
      LoggedEvent evt = (LoggedEvent) createInstance(LoggedEvent.class);
      evt.getMsg().setValue(msg);
      evt.getEventType().setValue(LoggedEvent.INFO);
      evt.setUser(currentUser());
      evt.setObject(this);
      return evt;
   }
   private LoggedEvent makeLog(String msg, TextEO longMsg)
   {
      LoggedEvent evt = makeLog(msg);
      evt.getLongMsg().setValue(longMsg);
      return evt;
   }

}
