/*
 * Created on Oct 11, 2004
 */
package com.u2d.model;

import java.util.*;
import javax.swing.ListModel;
import javax.swing.Icon;
import javax.swing.event.*;
import com.u2d.element.CommandInfo;
import com.u2d.element.Field;
import com.u2d.element.Command;
import com.u2d.field.Association;
import com.u2d.field.AssociationField;
import com.u2d.field.CompositeField;
import com.u2d.list.CSVExport;
import com.u2d.pattern.ListChangeNotifier;
import com.u2d.pattern.Onion;
import com.u2d.pubsub.*;
import com.u2d.reporting.Reportable;
import com.u2d.reporting.ReportFormat;
import com.u2d.view.ListEView;
import com.u2d.reflection.CommandAt;
import javax.swing.table.*;


/**
 * @author Eitan Suez
 */
public abstract class AbstractListEO extends AbstractEObject
   implements ListChangeNotifier, ListModel, AppEventListener, Typed
{
   protected List _items = new ArrayList();
   
   // EObject interface:
   // ============================================
   
   public Title title()
   {
      return type().title().appendParens(""+getTotal());
   }

   public abstract boolean isEmpty();

   public int validate()
   {
      int count = 0;
      Iterator itr = _items.iterator();
      EObject item;
      while (itr.hasNext())
      {
         item = (EObject) itr.next();
         count += item.validate();
      }
      return count;
   }

   public Icon iconSm() { return type().iconsSm(); }
   public Icon iconLg() { return type().iconsLg(); }

   public static String[] commandOrder = {"Open", "ExportToCSV", "Print"};
   protected static Onion _commands;
   static
   {
      _commands = Harvester.simpleHarvestCommands(AbstractListEO.class, new Onion(),
            false, null);  // TODO: FLAG: what parameter to pass in for parent?
   }
   public Onion commands() { return _commands; }
   public Command command(String commandName)
   {
      return (Command) _commands.find(Command.finder(commandName));
   }

   public void setValue(EObject eo)
   {
      if (!(eo instanceof AbstractListEO))
         throw new IllegalArgumentException("Invalid type on set;  must be AbstractListEO");
      
      AbstractListEO leo = (AbstractListEO) eo;
      setItems(leo.getItems());
   }

   
   public abstract ComplexType type();

   public String toString() { return title().toString(); }

   public List getItems() { return _items; }
   public void setItems(List items)
   {
      if (_items == items) return;

      removeDeleteListeners();
      _items = items;
      addDeleteListeners();
      fireContentsChanged(this, 0, _items.size());
   }
   public void restoreItems(List items)
   {
      if (_items == items) return;
      _items = items;
   }
   
   protected void removeDeleteListeners() { updateListeners(true); }
   protected void addDeleteListeners() { updateListeners(false); }

   protected void updateListeners(boolean clear)
   {
      ComplexEObject ceo = null;
      for (Iterator itr = _items.iterator(); itr.hasNext(); )
      {
         ceo = (ComplexEObject) itr.next();
         if (clear)
            ceo.removeAppEventListener("ONDELETE", this);
         else
            ceo.addAppEventListener("ONDELETE", this);
      }
   }
   
   public Iterator iterator() { return _items.iterator(); }
   
   public void add(ComplexEObject item)
   {
      if (contains(item)) return;
      
      _items.add(item);
      item.addAppEventListener("ONDELETE", this);
      fireIntervalAdded(this, _items.size() - 1, _items.size() - 1 );
   }
   
   public void onEvent(AppEvent evt)
   {
      remove((ComplexEObject) evt.getTarget());
   }

   public void remove(ComplexEObject item)
   {
      int index = _items.indexOf(item);
      if (index >= 0)
      {
         item.removeAppEventListener("ONDELETE", this);
         _items.remove(item);
         fireIntervalRemoved(this, index, index);
      }
   }
   
   public boolean contains(Object item)
   {
      return _items.contains(item);
   }
   
   /* ** ===== ListModel implementation ===== ** */
   
   public Object getElementAt(int index) { return _items.get(index); }
   public Object first() { return _items.get(0); }
   public Object last() { return _items.get(_items.size()-1); }
   
   public abstract int getSize();
   public abstract int getTotal();
   
   /* ** ===== TableModel implementation ===== ** */
   
   // must use an inner class because i want to extend from AbstractTableModel
   //  because it "provides default implementations for most of the methods 
   //  in the TableModel interface."
   
   protected AbstractTableModel _tableModel = null;
   public TableModel tableModel()
   {
      if (_tableModel == null)
         _tableModel = new LEOTableModel();
      return _tableModel;
   }
   
   public TableModel tableModel(final String[] fieldNames)
   {
      List fields = new ArrayList();
      for (int i=0; i<fieldNames.length; i++)
      {
         fields.add(type().field(fieldNames[i]));
      }
      return tableModel(fields);
   }
   
   public TableModel tableModel(final List fields)
   {
      return new AbstractTableModel()
      {
         public int getRowCount() { return getSize(); }
         public int getColumnCount() { return fields.size(); }
      
         public String getColumnName(int column)
         {
            Field field = (Field) fields.get(column);
            return field.label();
         }
      
         public Class getColumnClass(int column)
         {
            Field field = (Field) fields.get(column);
            if (field.isAssociation())
            {
               return Association.class;
            }
            else
            {
               return field.getJavaClass();
            }
         }
      
         public Object getValueAt(int row, int column)
         {
            ComplexEObject ceo = (ComplexEObject) _items.get(row);
            Field field = (Field) fields.get(column);
            if (field.isAssociation())
            {
               return ((AssociationField) field).association(ceo);
            }
            else
            {
               return field.get(ceo);
            }
         }
      };
   }
   
   public class LEOTableModel extends AbstractTableModel
   {
      /**
       * slight customization here.  fields containing long text (TextEO)
       * are excluded from the tablemodel.
       */
      protected List _tableFields = new ArrayList();
      
      public LEOTableModel()
      {
         Field field = null;
         for (Iterator itr = type().fields().iterator(); itr.hasNext(); )
         {
            field = (Field) itr.next();
            if ( AbstractListEO.class.isAssignableFrom(field.getJavaClass()) ||
                  field.isHidden() ||
                  "createdOn".equals(field.name()) ||
                  field.getJavaClass().equals(com.u2d.type.atom.Photo.class) )
               continue;
            _tableFields.add(field);
         }
      }
      public int getRowCount() { return getSize(); }
      public int getColumnCount() { return _tableFields.size() + 1; }
      
      public String getColumnName(int column)
      {
         if (column == 0)
         {
            return type().title().toString();
         }
         Field field = (Field) _tableFields.get(column - 1);
         return field.label();
      }
      
      public Class getColumnClass(int column)
      {
         if (column == 0)
         {
            return type().getJavaClass();
         }
         Field field = (Field) _tableFields.get(column - 1);
         if (field.isAssociation())
         {
            return Association.class;
         }
         else
         {
            return field.getJavaClass();
         }
      }
      
      public Object getValueAt(int row, int column)
      {
         ComplexEObject ceo = (ComplexEObject) _items.get(row);
         if (column == 0)
         {
            return ceo;
         }
         Field field = (Field) _tableFields.get(column - 1);
         if (field.isAssociation())
         {
            return ((AssociationField) field).association(ceo);
         }
         else
         {
            return field.get(ceo);
         }
      }

      // used for editable tables.

      public boolean isCellEditable(int row, int column)
      {
         Object o = getValueAt(row, column);
         if (o instanceof Association)
         {
            Association a = (Association) o;
            ComplexEObject parentObject = a.parent();
            return (parentObject != null && parentObject.isEditableState());
         }
         
         EObject value = (EObject) getValueAt(row, column);
         EObject parentObject = value.parentObject();
         return ( parentObject != null && 
                  parentObject instanceof ComplexEObject &&
                  ((ComplexEObject) parentObject).isEditableState() &&
                  value instanceof AtomicEObject &&
                  !((CompositeField) value.field()).isReadOnly()
         );
      }

      public void setValueAt(Object value, int row, int column)
      {
         if (column == 0) return; // first column of this model not editable
         Field field = (Field) _tableFields.get(column - 1);
         ComplexEObject parent = (ComplexEObject) _items.get(row);
         field.set(parent, value);
      }

   }

   /* ** ===== List Change Support code ===== ** */
   
   protected transient EventListenerList _listDataListenerList = new EventListenerList();

   public void addListDataListener(ListDataListener l)
   {
      _listDataListenerList.add(ListDataListener.class, l);
   }

   public void removeListDataListener(ListDataListener l)
   {
      _listDataListenerList.remove(ListDataListener.class, l);
   }
   
   

   public void fireContentsChanged(Object source, int index0, int index1)
   {
      Object[] listeners = _listDataListenerList.getListenerList();
      ListDataEvent e = null;
      
      for (int i = listeners.length - 2; i >= 0; i -= 2) {
         if (listeners[i] == ListDataListener.class) {
            if (e == null) {
               e = new ListDataEvent(source, ListDataEvent.CONTENTS_CHANGED, index0, index1);
            }
            ((ListDataListener)listeners[i+1]).contentsChanged(e);
         }
      }

      if (_tableModel != null)
         _tableModel.fireTableChanged(new TableModelEvent(tableModel()));
   }

   public void fireIntervalAdded(Object source, int index0, int index1)
   {
      Object[] listeners = _listDataListenerList.getListenerList();
      ListDataEvent e = null;

      for (int i = listeners.length - 2; i >= 0; i -= 2) {
         if (listeners[i] == ListDataListener.class) {
            if (e == null) {
               e = new ListDataEvent(source, ListDataEvent.INTERVAL_ADDED, index0, index1);
            }
            ((ListDataListener)listeners[i+1]).intervalAdded(e);
         }         
      }

      if (_tableModel != null)
         _tableModel.fireTableRowsInserted(index0, index1);
   }

   public void fireIntervalRemoved(Object source, int index0, int index1)
   {
      Object[] listeners = _listDataListenerList.getListenerList();
      ListDataEvent e = null;

      for (int i = listeners.length - 2; i >= 0; i -= 2) {
         if (listeners[i] == ListDataListener.class) {
            if (e == null) {
               e = new ListDataEvent(source, ListDataEvent.INTERVAL_REMOVED, index0, index1);
            }
            ((ListDataListener)listeners[i+1]).intervalRemoved(e);
         }
      }

      if (_tableModel != null)
         _tableModel.fireTableRowsDeleted(index0, index1);
   }
   
   public void jibxAdd(Object obj)
   {
      if (!(obj instanceof ComplexEObject))
         throw new IllegalArgumentException("Cannot add object that is not a ComplexEObject");
      
      ComplexEObject ceo = (ComplexEObject) obj;
      
      if (ceo.isNullState())
         ceo.setReadState();
      
      add(ceo);
   }
   
   public ListEView getPickView() { return vmech().getPickView(this); }
   
   
   @CommandAt
   public void ExportToCSV(CommandInfo cmdInfo)
   {
      CSVExport.export(cmdInfo, this);
   }
   @CommandAt
   public AbstractListEO Open(CommandInfo cmdInfo)
   {
      return this;
   }

   
   
   // basic print capability .. needs a little work.
   
   private PrintForm _printForm = null;
   public Reportable commandPrint(CommandInfo cmdInfo)
   {
      if (_printForm == null)
      {
         _printForm = new PrintForm();
      }
      return _printForm;
   }
   
   // technically could just make AbstractListEO itself reportable.  might
   // be simpler
   class PrintForm implements Reportable
   {
      Properties p = new Properties();
      public Properties properties() { return p; }
      public String reportName() { return toString(); }
      public TableModel tableModel() { return AbstractListEO.this.tableModel(); }
      public ReportFormat reportFormat() { return ReportFormat.PDF; }
   }

   
   // pick support
   private Association _association;
   public void setPickState(Association association)
   {
      _association = association;
      fireStateChanged();
   }
   public boolean isPickState() { return (_association != null); }

   private boolean _inContext = false;
   public void setPickState(Association association, boolean inContext)
   {
      setPickState(association);
      _inContext = inContext;
   }

   /**
    * @return whether the list is intended for use in a greater
    * context.  right now this is an evil hack that lets me conditionally
    * control whether list views should automatically close after
    * a 'pick' (in pick state) or not (so when i use this in a wizard
    * picking will not actually terminate my wizard!
    */
   public boolean isInContext() { return _inContext; }

   public void pick(ComplexEObject value)
   {
      _association.associate(value);
      if (!_inContext)
         setPickState(null);
   }

   public int hashCode()
   {
      int hash = 0;
      Object item;
      for (int i=0; i<_items.size(); i++)
      {
         item = _items.get(i);
         hash += 31 * item.hashCode();
      }
      return hash;
   }

   public boolean equals(Object obj)
   {
      if (this == obj) return true;
      if (! (obj instanceof AbstractListEO)) return false;

      AbstractListEO list = (AbstractListEO) obj;
      if (list.type() != type()) return false;

      return _items.equals(list.getItems());
   }
   
   public String concat(String delimiter)
   {
      StringBuffer text = new StringBuffer("");
      EObject item;
      for (int i=0; i<_items.size()-1; i++)
      {
         item = (EObject) _items.get(i);
         text.append(item.toString()).append(delimiter).append(" ");
      }
      text.append(last().toString());
      return text.toString();
   }
   
}
