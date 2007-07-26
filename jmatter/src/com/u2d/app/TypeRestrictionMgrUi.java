package com.u2d.app;

import com.u2d.view.ComplexEView;
import com.u2d.view.swing.CommandButton;
import com.u2d.view.swing.FormPane;
import com.u2d.model.Editor;
import com.u2d.model.EObject;
import com.u2d.element.Command;
import com.u2d.pattern.Onion;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.builder.PanelBuilder;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.util.*;

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
   private Map<Role, java.util.Map<Command, Boolean>> _backingModel;
   
   public TypeRestrictionMgrUi(TypeRestrictionMgr mgr)
   {
      _mgr = mgr;
      
      _backingModel = new HashMap<Role, java.util.Map<Command, Boolean>>();
      for (int i=0; i<_mgr.getRoles().getSize(); i++)
      {
         Role role = (Role) _mgr.getRoles().getElementAt(i);
    
         Map<Command, Boolean> map = new HashMap<Command, Boolean>();
         _backingModel.put(role, map);
              
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

      }
      
      setLayout(new BorderLayout());
      add(new JScrollPane(buildUI()), BorderLayout.CENTER);
      add(buttonPnl(), BorderLayout.SOUTH);
   }
   
   private JPanel buildUI()
   {
      Onion typeCommands = _mgr.getType().commands();
      Map<Class, Onion> instanceCmds = _mgr.getType().instanceCommands();
      
      FormLayout layout = new FormLayout("left:pref");
      PanelBuilder builder = new PanelBuilder(layout, new FormPane());
      builder.setDefaultDialogBorder();
      
      builder.appendRow("pref");
      builder.addSeparator("Type commands");
      builder.appendRelatedComponentsGapRow();
      builder.nextLine(2);

      builder.appendRow("pref");
      builder.add(new JScrollPane(table(typeCommands)));
      builder.appendRelatedComponentsGapRow();
      builder.nextLine(2);

      builder.appendRow("pref");
      builder.addSeparator("Instance commands");
      builder.appendRelatedComponentsGapRow();
      builder.nextLine(2);

      for (Iterator itr = instanceCmds.keySet().iterator(); itr.hasNext(); )
      {
         Class stateCls = (Class) itr.next();
         Onion stateCmds = instanceCmds.get(stateCls);

         builder.appendRow("pref");
         builder.addSeparator(stateName(stateCls));
         builder.appendRelatedComponentsGapRow();
         builder.nextLine(2);
         
         builder.appendRow("pref");
         builder.add(new JScrollPane(table(stateCmds)));
         builder.appendRelatedComponentsGapRow();
         builder.nextLine(2);
      }
      
      return builder.getPanel();
   }

   private JTable table(final Onion commands)
   {
      JTable table = new JTable();
      table.setModel(new AbstractTableModel()
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
            Map<Command, Boolean> map = _backingModel.get(role);
            return map.get(cmd);
         }

         public void setValueAt(Object aValue, int rowIndex, int columnIndex)
         {
            Command cmd = (Command) commands.get(rowIndex);
            Role role = (Role) _mgr.getRoles().get(columnIndex-1);
            Boolean value = ((Boolean) aValue);
            Map<Command, Boolean> map = _backingModel.get(role);
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
      
      Dimension preferredSize = table.getPreferredScrollableViewportSize();
      preferredSize.height = table.getRowHeight() * table.getRowCount();
      table.setPreferredScrollableViewportSize(preferredSize);
      
      return table;
   }
   
   private JPanel buttonPnl()
   {
      JPanel buttonPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      Command cmd = _mgr.command("ApplyChanges");
      CommandButton cmdBtn = new CommandButton(cmd, _mgr, this, true);
      buttonPnl.add(cmdBtn);
      return buttonPnl;
   }

   public EObject getEObject() { return _mgr; }
   public void detach() { }
   public void stateChanged(ChangeEvent e) { }
   public void propertyChange(PropertyChangeEvent evt) { }
   public boolean isMinimized() { return false; }
   public void setEditable(boolean editable) { }
   public boolean isEditable() { return false; }
   public int validateValue() { return 0; }

   public int transferValue()
   {
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
