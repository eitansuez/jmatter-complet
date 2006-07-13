package com.u2d.issuemgr;

/* Initial Version */
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

}
