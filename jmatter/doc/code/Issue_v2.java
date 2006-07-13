package com.u2d.issuemgr;

public class Issue extends AbstractComplexEObject
{
   private final IssueState _status = new IssueState(NEW);
   private final StringEO _title = new StringEO();
   private final TextEO _description = new TextEO();
   
   private final RelationalList _notes = new RelationalList(Note.class);
   public static Class notesType = Note.class;

   private User _openedBy;
   private User _assignedTo;

   private final RelationalList _history = new RelationalList(LoggedEvent.class);
   public static Class historyType = LoggedEvent.class;

   // a loose definition for these -- just using a numeric 
   // instead of an enumeration.
   private final IntEO _priority = new IntEO();
   private final IntEO _severity = new IntEO();
   
   private IssueCategory _category;
   
   private Issue _dependsOn;
   
   public static String[] fieldOrder = {"status", "title", "description", "notes",
         "openedBy", "assignedTo", "history", "severity", "priority", "category",
         "dependsOn"};
   
   public static String[] readOnly = {"openedBy"};

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

   // *****************************************************************
   // state-specific logic; working on reducing the amount of rote-code
   
   public class NewState extends ReadState {}

   public class AssignedState extends ReadState
   {
      public void commandAccept(CommandInfo cmdInfo)
      {
         transition(_acceptedState, makeLog("Issue accepted by developer"));
      }
   }

   public class AcceptedState extends ReadState
   {
      public void commandFix(CommandInfo cmdInfo, StringEO fix, TextEO description)
      {
         transition(_fixedState, makeLog("Fix: "+fix.stringValue(), description));
      }
   }

   public class FixedState extends ReadState
   {
      public void commandRejectFix(CommandInfo cmdInfo, TextEO explanation)
      {
         transition(_acceptedState, makeLog("Fix rejected", explanation));
      }
      public void commandClose(CommandInfo cmdInfo, TextEO explanation)
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

      firePropertyChange("icon", null, null);

      persistor().updateAssociation(this, evt);
   }
   
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

   public static String commandFixParameter1Caption = "Fix";
   public static String commandFixParameter2Caption = "Description";
   public static String commandRejectFixParameter1Caption = "Explanation";
   public static String commandCloseParameter1Caption = "Explanation";

   // end of state-specific code
   // **************************


   private LoggedEvent makeLog(String msg)
   {
      LoggedEvent evt = (LoggedEvent) createInstance(LoggedEvent.class);
      evt.getMsg().setValue(msg);
      evt.getType().setValue(LoggedEvent.INFO);
      evt.setUser(currentUser());
      evt.setObject(this);
      return evt;
   }
   private LoggedEvent makeLog(String msg, TextEO longMsg)
   {
      LoggedEvent evt = (LoggedEvent) createInstance(LoggedEvent.class);
      evt.getMsg().setValue(msg);
      evt.getLongMsg().setValue(longMsg);
      evt.getType().setValue(LoggedEvent.INFO);
      evt.setUser(currentUser());
      evt.setObject(this);
      return evt;
   }

}
