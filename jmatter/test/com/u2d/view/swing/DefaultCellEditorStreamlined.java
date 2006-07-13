package com.u2d.view.swing;

import com.lowagie.text.Table;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.util.EventObject;
import java.awt.event.*;
import java.awt.*;
import java.io.Serializable;

public class DefaultCellEditorStreamlined
      extends AbstractCellEditor
      implements TableCellEditor
{
   protected JComponent editorComponent;
   protected EditorDelegate delegate;

   public DefaultCellEditorStreamlined(final JTextField textField)
   {
      editorComponent = textField;
      delegate = new EditorDelegate()
      {
         public void setValue(Object value)
         {
            textField.setText(value.toString());
         }

         public Object getCellEditorValue()
         {
            return textField.getText();
         }
      };
      textField.addActionListener(delegate);
   }

   public DefaultCellEditorStreamlined(final JCheckBox checkBox)
   {
      editorComponent = checkBox;
      delegate = new EditorDelegate()
      {
         public void setValue(Object value)
         {
            boolean selected = false;
            if (value instanceof Boolean)
            {
               selected = ((Boolean) value).booleanValue();
            }
            else if (value instanceof String)
            {
               selected = value.equals("true");
            }
            checkBox.setSelected(selected);
         }

         public Object getCellEditorValue()
         {
            return Boolean.valueOf(checkBox.isSelected());
         }
      };
      checkBox.addActionListener(delegate);
   }

   public Object getCellEditorValue() { return delegate.getCellEditorValue(); }

   public boolean isCellEditable(EventObject anEvent)
   {
      return delegate.isCellEditable(anEvent);
   }

   public Component getTableCellEditorComponent(JTable table, Object value,
                                                boolean isSelected,
                                                int row, int column)
   {
      delegate.setValue(value);
      return editorComponent;
   }

   protected class EditorDelegate
         implements ActionListener, Serializable
   {

      protected Object value;

      public Object getCellEditorValue() { return value; }
      public void setValue(Object value) { this.value = value; }

      public boolean isCellEditable(EventObject anEvent)
      {
         if (anEvent instanceof MouseEvent)
         {
            return ((MouseEvent) anEvent).getClickCount() >= 1;
         }
         return true;
      }

      public void actionPerformed(ActionEvent e)
      {
         DefaultCellEditorStreamlined.this.stopCellEditing();
      }

   }

}

class TextFieldTableCellEditor extends AbstractCellEditor
         implements ActionListener, TableCellEditor
{
   JTextField tf;
   
   public TextFieldTableCellEditor()
   {
      tf = new JTextField();
      tf.addActionListener(this);
   }

   public Component getTableCellEditorComponent(JTable table, Object value,
                                                boolean isSelected,
                                                int row, int column)
   {
      tf.setText(value.toString());
      return tf;
   }
   public Object getCellEditorValue() { return tf.getText(); }
   public void actionPerformed(ActionEvent e) { stopCellEditing(); }
}
class TextAreaTableCellEditor extends AbstractCellEditor
   implements TableCellEditor
{
   JPanel p;
   JTextArea ta;
   JScrollPane sp;
   public TextAreaTableCellEditor()
   {
      p = new JPanel(new BorderLayout());
      ta = new JTextArea(2, 30);
      sp = new JScrollPane(ta);
      p.add(sp, BorderLayout.CENTER);
   }

   public Object getCellEditorValue() { return ta.getText(); }

   public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
   {
      ta.setText(value.toString());
      return p;
   }

   public int rowHeight()
   {
      return ta.getPreferredSize().height;
   }
}

class CheckBoxTableCellEditor extends AbstractCellEditor
   implements TableCellEditor
{
   JCheckBox cb;
   
   public CheckBoxTableCellEditor()
   {
      cb = new JCheckBox();
      cb.setBackground(UIManager.getColor("Table.background"));
      cb.setForeground(UIManager.getColor("Table.foreground"));
      cb.setHorizontalAlignment(JCheckBox.CENTER);
      cb.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            stopCellEditing();
         }
      });
   }

   public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
   {
      cb.setSelected(((Boolean) value).booleanValue());
      return cb;
   }

   public Object getCellEditorValue()
   {
      return Boolean.valueOf(cb.isSelected());
   }
}
