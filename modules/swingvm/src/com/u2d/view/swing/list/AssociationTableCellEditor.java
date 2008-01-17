package com.u2d.view.swing.list;

import com.u2d.view.swing.AssociationEditor;
import com.u2d.field.Association;
import com.u2d.element.Field;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Mar 17, 2006
 * Time: 10:10:33 PM
 */
public class AssociationTableCellEditor extends AbstractCellEditor
                       implements TableCellRenderer, TableCellEditor
{
   private Map<Field, AssociationEditor> _editorMap = new HashMap<Field, AssociationEditor>();
   private AssociationEditor _currentEditor;

   public AssociationTableCellEditor() { }

   public Component getTableCellRendererComponent(JTable table, Object value,
                                                  boolean isSelected, boolean hasFocus,
                                                  int row, int column)
   {
      return getTableCellEditorComponent(table, value, isSelected, row, column);
   }

   public Component getTableCellEditorComponent(JTable table, Object value,
                                                boolean isSelected,
                                                int row, int column)
   {
      Association assoc = (Association) value;
      AssociationEditor editor = editor(assoc);
      if (assoc.isEmpty())
      {
         editor.clearValue();
      }
      else
      {
         editor.renderValue(assoc.get());
      }
      return editor;
   }
   
   private AssociationEditor editor(Association assoc)
   {
      Field field = assoc.field();
      if (_editorMap.get(field) == null)
      {
         AssociationEditor editor = new AssociationEditor(assoc);
         editor.setOpaque(true);
         editor.setBackground(UIManager.getColor("Table.background"));
         editor.setForeground(UIManager.getColor("Table.foreground"));
         
         ActionListener stopEditorListener = new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               stopCellEditing();
            }
         };
         editor.addActionListener(stopEditorListener);
         
         _editorMap.put(field, editor);
      }
      _currentEditor = _editorMap.get(field);
      return _currentEditor;
   }

   public Object getCellEditorValue() { return _currentEditor.bind(); }

}
