package com.u2d.view.swing.restrict;

import com.u2d.view.ComplexEView;
import com.u2d.view.swing.CommandButton;
import com.u2d.view.swing.FormPane;
import com.u2d.model.Editor;
import com.u2d.model.EObject;
import com.u2d.model.AbstractComplexEObject;
import com.u2d.element.Command;
import com.u2d.element.Field;
import com.u2d.pattern.Onion;
import com.u2d.restrict.CommandRestriction;
import com.u2d.restrict.FieldRestrictionType;
import com.u2d.restrict.FieldRestriction;
import com.u2d.app.Role;
import com.u2d.app.TypeRestrictionMgr;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListDataEvent;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.util.*;
import java.util.List;
import net.miginfocom.swing.MigLayout;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jul 25, 2007
 * Time: 3:41:51 PM
 */
public class TypeRestrictionMgrUi extends JPanel
      implements ComplexEView, Editor
{
   private TypeRestrictionMgr _mgr;
   private Map<Role, java.util.Map<Command, Boolean>> _cmdBackingModel;
   private Map<Role, java.util.Map<Field, FieldRestrictionType>> _fldBackingModel;
   
   public TypeRestrictionMgrUi(TypeRestrictionMgr mgr)
   {
      _mgr = mgr;

      initBackingModel();

      setLayout(new BorderLayout());
      add(new JScrollPane(buildUI()), BorderLayout.CENTER);
      add(buttonPnl(), BorderLayout.PAGE_END);
   }

   private void initBackingModel()
   {
      _cmdBackingModel = new HashMap<Role, Map<Command, Boolean>>();
      _fldBackingModel = new HashMap<Role, Map<Field, FieldRestrictionType>>();
      
      for (int i=0; i<_mgr.getRoles().getSize(); i++)
      {
         Role role = (Role) _mgr.getRoles().getElementAt(i);
         initBackingModelForRole(role);
      }
   }

   private void initBackingModelForRole(Role role)
   {
      Map<Command, Boolean> map = new HashMap<Command, Boolean>();
      _cmdBackingModel.put(role, map);

      Onion typeCommands = _mgr.getType().commands();
      for (Iterator itr = typeCommands.deepIterator(); itr.hasNext(); )
      {
         Command cmd = (Command) itr.next();
         map.put(cmd, role.hasRestrictionOnCmd(cmd));
      }

      Map<Class, Onion> instanceCmds = _mgr.getType().instanceCommands();
      for (Iterator itr = instanceCmds.keySet().iterator(); itr.hasNext(); )
      {
         Class stateCls = (Class) itr.next();
         Onion stateCmds = instanceCmds.get(stateCls);

         for (Iterator itr2 = stateCmds.deepIterator(); itr2.hasNext(); )
         {
            Command cmd = (Command) itr2.next();
            map.put(cmd, role.hasRestrictionOnCmd(cmd));
         }
      }

      Map<Field, FieldRestrictionType> fldmap = new HashMap<Field, FieldRestrictionType>();
      _fldBackingModel.put(role, fldmap);

      List fields = _mgr.getType().fields();
      for (Iterator itr = fields.iterator(); itr.hasNext(); )
      {
         Field fld = (Field) itr.next();
         FieldRestriction fr = role.restrictionOnFld(fld);
         if (fr == null)
            fldmap.put(fld, new FieldRestrictionType(FieldRestriction.NONE));
         else
            fldmap.put(fld, fr.getRestrictionType());
      }
   }

   private JTable typeCommandsTable, instanceCommandsTable, fieldTable;
   private ListDataListener rolesListDataListener = new ListDataListener()
   {
      public void intervalAdded(ListDataEvent e)
      {
         updateTables(e);
      }

      public void intervalRemoved(ListDataEvent e)
      {
      }

      public void contentsChanged(ListDataEvent e)
      {
      }
   };

   private void addSeparator(JPanel panel, String text)
   {
      panel.add(new JLabel(text), "gapbottom 1, span, split 2, aligny center");
      panel.add(new JSeparator(), "gapleft rel, growx");
   }

   private JPanel buildUI()
   {
      Onion typeCommands = _mgr.getType().commands();
      Map<Class, Onion> instanceCmds = _mgr.getType().instanceCommands();

      MigLayout layout = new MigLayout("wrap 1");
      JPanel formPane = new FormPane(layout);
      
      addSeparator(formPane, "Type commands");

      typeCommandsTable = commandtable(typeCommands);
      formPane.add(new JScrollPane(typeCommandsTable), "gapbottom unrel");

      addSeparator(formPane, "Instance commands");

      for (Iterator itr = instanceCmds.keySet().iterator(); itr.hasNext(); )
      {
         Class stateCls = (Class) itr.next();
         if ((stateCls == AbstractComplexEObject.NullState.class) ||
             (stateCls == AbstractComplexEObject.EditState.class) ||
             (stateCls == AbstractComplexEObject.TransientState.class) ||
             (stateCls == AbstractComplexEObject.ReadState.class && _mgr.getType().hasSubstates())
            )
         {
            continue;
         }

         Onion stateCmds = instanceCmds.get(stateCls);
         
         if (stateCls != AbstractComplexEObject.ReadState.class)
         {
            addSeparator(formPane, stateName(stateCls));
         }
         
         instanceCommandsTable = commandtable(stateCmds);
         formPane.add(new JScrollPane(instanceCommandsTable), "gapbottom unrel");
      }
      
      
      List fields = _mgr.getType().fields();
      
      addSeparator(formPane, "Fields");

      fieldTable = fieldtable(fields);
      formPane.add(new JScrollPane(fieldTable));

      _mgr.getRoles().addListDataListener(rolesListDataListener);

      return formPane;
   }

   private void updateTables(ListDataEvent e)
   {
      Role role = (Role) _mgr.getRoles().getElementAt(e.getIndex0());
      initBackingModelForRole(role);
      ((AbstractTableModel) fieldTable.getModel()).fireTableStructureChanged();
      ((AbstractTableModel) typeCommandsTable.getModel()).fireTableStructureChanged();
      ((AbstractTableModel) instanceCommandsTable.getModel()).fireTableStructureChanged();
   }
   
   private JTable table(TableModel model)
   {
      JTable table = new JTable();
      table.setModel(model);
      Dimension preferredScrollSize = table.getPreferredScrollableViewportSize();
      preferredScrollSize.height = table.getRowHeight() * table.getRowCount();
      table.setPreferredScrollableViewportSize(preferredScrollSize);
      return table;
   }
   private JTable fieldtable(final List fields)
   {
      JTable table = table(new AbstractTableModel()
      {
         public int getRowCount() { return fields.size(); }
         public int getColumnCount() { return _mgr.getRoles().getSize() + 1; }

         public Object getValueAt(int rowIndex, int columnIndex)
         {
            Field fld = (Field) fields.get(rowIndex);
            if (columnIndex == 0)
            {
               return fld.label();
            }
            Role role = (Role) _mgr.getRoles().get(columnIndex-1);
            Map<Field, FieldRestrictionType> map = _fldBackingModel.get(role);
            return map.get(fld);
         }

         public void setValueAt(Object aValue, int rowIndex, int columnIndex)
         {
            Field fld = (Field) fields.get(rowIndex);
            Role role = (Role) _mgr.getRoles().get(columnIndex-1);
            String value = (String) aValue;
            Map<Field, FieldRestrictionType> map = _fldBackingModel.get(role);
            map.put(fld, new FieldRestrictionType(value));
            fireTableCellUpdated(rowIndex, columnIndex);
         }
         
         public boolean isCellEditable(int rowIndex, int columnIndex)
         {
            if (columnIndex == 0) return false;
            return true;
         }

         public Class<?> getColumnClass(int columnIndex)
         {
            if (columnIndex == 0) return String.class;
            return FieldRestrictionType.class;
         }
         
         public String getColumnName(int column)
         {
            if (column == 0) return "Field";
            Role role = (Role) _mgr.getRoles().get(column-1);
            return role.getName() + " role";
         }
      });
      
      for (int i=1; i<table.getColumnModel().getColumnCount(); i++)
      {
         TableColumn roleColumn = table.getColumnModel().getColumn(i);
         JComboBox comboBox = new JComboBox();
         comboBox.addItem(FieldRestriction.NONE);
         comboBox.addItem(FieldRestriction.READ_ONLY);
         comboBox.addItem(FieldRestriction.HIDDEN);
         roleColumn.setCellEditor(new DefaultCellEditor(comboBox));
      }

      return table;
   }
   private JTable commandtable(final Onion commands)
   {
      return table(new AbstractTableModel()
      {
         public int getRowCount() { return commands.size(); }
         public int getColumnCount() { return _mgr.getRoles().getSize() + 1; }

         public Object getValueAt(int rowIndex, int columnIndex)
         {
            Command cmd = (Command) commands.get(rowIndex);
            if (columnIndex == 0)
            {
               return cmd.label();
            }
            Role role = (Role) _mgr.getRoles().get(columnIndex-1);
            Map<Command, Boolean> map = _cmdBackingModel.get(role);
            return map.get(cmd);
         }

         public void setValueAt(Object aValue, int rowIndex, int columnIndex)
         {
            Command cmd = (Command) commands.get(rowIndex);
            Role role = (Role) _mgr.getRoles().get(columnIndex-1);
            Boolean value = ((Boolean) aValue);
            Map<Command, Boolean> map = _cmdBackingModel.get(role);
            map.put(cmd, value);
            fireTableCellUpdated(rowIndex, columnIndex);
         }
         
         public boolean isCellEditable(int rowIndex, int columnIndex)
         {
            if (columnIndex == 0) return false;
            return true;
         }

         public Class<?> getColumnClass(int columnIndex)
         {
            if (columnIndex == 0) return String.class;
            return Boolean.class;
         }
         
         public String getColumnName(int column)
         {
            if (column == 0) return "Command";
            Role role = (Role) _mgr.getRoles().get(column-1);
            return role.getName() + " role";
         }
      });
   }
   
   private JPanel buttonPnl()
   {
      JPanel buttonPnl = new JPanel(new FlowLayout(FlowLayout.TRAILING));
      Command cmd = _mgr.command("ApplyChanges");
      CommandButton cmdBtn = new CommandButton(cmd, _mgr, this, true);
      buttonPnl.add(cmdBtn);
      return buttonPnl;
   }

   public EObject getEObject() { return _mgr; }

   public void detach()
   {
      _mgr.getRoles().removeListDataListener(rolesListDataListener);
   }

   public void stateChanged(ChangeEvent e) { }
   public void propertyChange(PropertyChangeEvent evt) { }
   public boolean isMinimized() { return false; }
   public void setEditable(boolean editable) { }
   public boolean isEditable() { return false; }
   public int validateValue() { return 0; }

   public int transferValue()
   {
      for (Iterator itr= _mgr.getRoles().iterator(); itr.hasNext(); )
      {
         Role role = (Role) itr.next();
         Map<Command, Boolean> map = _cmdBackingModel.get(role);
         Map<Field, FieldRestrictionType> fieldmap = _fldBackingModel.get(role);
         
         List addedRestrictions, removedRestrictions, dirtyRestrictions;
         addedRestrictions = new ArrayList();
         removedRestrictions = new ArrayList();
         dirtyRestrictions = new ArrayList();
         
         for (Iterator itr2 = map.keySet().iterator(); itr2.hasNext(); )
         {
            Command cmd = (Command) itr2.next();
            boolean checked = map.get(cmd);
            CommandRestriction restriction = role.restrictionOnCmd(cmd);
            if (restriction==null && checked)
            {
               addedRestrictions.add(new CommandRestriction(role, cmd));
            }
            else if (restriction!=null && !checked)
            {
               removedRestrictions.add(restriction);
            }
         }
         
         for (Iterator itr2 = fieldmap.keySet().iterator(); itr2.hasNext(); )
         {
            Field fld = (Field) itr2.next();
            FieldRestrictionType frt = fieldmap.get(fld);
            FieldRestriction restriction = role.restrictionOnFld(fld);
            if (restriction == null && !frt.is(FieldRestriction.NONE))
            {
               addedRestrictions.add(new FieldRestriction(role, fld, frt));
            }
            else if (restriction != null && !restriction.getRestrictionType().equals(frt))
            {
               restriction.getRestrictionType().setValue(frt);
               dirtyRestrictions.add(restriction);
            }
            else if (restriction != null && frt.code().equals(FieldRestriction.NONE))
            {
               removedRestrictions.add(restriction);
            }
         }
         
         _mgr.setAddedRestrictionsForRole(role, addedRestrictions);
         _mgr.setRemovedRestrictionsForRole(role, removedRestrictions);
         _mgr.setDirtyRestrictionsForRole(role, dirtyRestrictions);
      }
      
      
      return 0;
   }

   private String stateName(Class stateCls)
   {
      String clsName = stateCls.getName();
      int idx = clsName.lastIndexOf(".");
      String stateName = clsName.substring(idx+1);
      if (stateName.contains("$"))
      {
         idx = stateName.lastIndexOf('$');
         stateName = stateName.substring(idx + 1);
      }
      return stateName.substring(0, stateName.length() - 5)+ " State";
   }

}
