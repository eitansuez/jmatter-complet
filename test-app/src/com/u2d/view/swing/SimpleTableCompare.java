package com.u2d.view.swing;

import com.u2d.type.atom.StringEO;
import com.u2d.type.atom.BooleanEO;
import com.u2d.type.atom.IntEO;
import com.u2d.view.swing.list.AtomicTableCellEditor;
import com.u2d.app.Application;
import com.u2d.model.EObject;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Setting up a simple table with raw types and raw components
 * to study the default behaviour of table cell editing,
 * with default cell editors, default cell renderers, etc..
 */
public class SimpleTableCompare extends JFrame
{
//   Model model = new Model();
   Application app = new Application();
   Model2 model2 = new Model2();
   JTable t;

   public SimpleTableCompare()
   {
      super();
      JPanel p = (JPanel) getContentPane();

      setupTable();
      p.add(new JScrollPane(t), BorderLayout.CENTER);
      
      addShowValuesButton(p);
      display();
   }

   private void setupTable()
   {
      t = new JTable(model2);

      for (int i=0; i<t.getColumnCount(); i++)
      {
         t.getColumnModel().getColumn(i).
               setCellEditor(model2.getCellEditor(i));
         TableCellRenderer renderer = model2.getCellRenderer(i);
         if (renderer != null)
         {
            t.getColumnModel().getColumn(i).
               setCellRenderer(renderer);
         }
      }
   }

   private void display()
   {
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setLocation(100,100);
      pack();
      setSize(getSize().width, 200);
      setVisible(true);
   }

   private void addShowValuesButton(JPanel p)
   {
      JButton btn = new JButton("Show Values");
      btn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e) { model2.print(); }
      });
      p.add(btn, BorderLayout.PAGE_END);
   }

   public static void main(String[] args) { new SimpleTableCompare(); }

}



class Toy
{
   boolean checked;
   String name;
   int age;
   Toy(boolean c, String n, int a)
   {
      checked = c; name = n; age = a;
   }

   public String toString()
   {
      return "" + checked + ":" + name + ":" + age;
   }
}


class Model extends AbstractTableModel
{
   Toy[] toys = new Toy[]
         {
               new Toy(true, "One", 3),
               new Toy(false, "Two", 1),
               new Toy(false, "Three", 1)
         };
   
   public int getRowCount() { return 3; }
   public int getColumnCount() { return 3; }

   public Object getValueAt(int rowIndex, int columnIndex)
   {
      if (columnIndex == 0)
      {
         return toys[rowIndex].checked;
      }
      else if (columnIndex == 1)
      {
         return toys[rowIndex].name;
      }
      else if (columnIndex == 2)
      {
         return "" + toys[rowIndex].age;
      }
      throw new RuntimeException("What happened?");
   }

   public boolean isCellEditable(int rowIndex, int columnIndex)
   {
      return true;
   }

   public void setValueAt(Object value, int rowIndex, int columnIndex)
   {
      if (columnIndex == 0)
      {
         toys[rowIndex].checked = ((Boolean) value).booleanValue();
      }
      else if (columnIndex == 1)
      {
         toys[rowIndex].name = (String) value;
      }
      else if (columnIndex == 2)
      {
         toys[rowIndex].age = Integer.parseInt((String) value);
      }
   }
   
   public TableCellEditor getCellEditor(int column)
   {
      if (column == 0)
      {
         return new CheckBoxTableCellEditor();
      }
      else if (column == 1)
      {
         return new TextAreaTableCellEditor();
      }
      else if (column == 2)
      {
         return new TextFieldTableCellEditor();
      }
      throw new RuntimeException("Invalid column index: "+column);
   }
   
   private TableCellRenderer getCellRenderer(int column)
   {
      if (column == 0)
      {
         return new TableCellRenderer()
         {
            JCheckBox b = new JCheckBox();
            {
               b.setOpaque(true);
               b.setHorizontalAlignment(JCheckBox.CENTER);
            }
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
            {
               b.setSelected(((Boolean) value).booleanValue());
               return b;
            }
         };
      }
      return null;
   }

   public void print()
   {
      for (Toy toy : toys)
      {
         System.out.println(toy);
      }
   }

}



class Toy2
{
   final BooleanEO checked = new BooleanEO();
   final StringEO name = new StringEO();
   final IntEO age = new IntEO();
   
   Toy2(boolean c, String n, int a)
   {
      checked.setValue(c);
      name.setValue(n);
      age.setValue(a);
   }
   public String toString()
   {
      return checked.toString() + ":" + name.toString() + ":" + age.toString();
   }
}
class Model2 extends AbstractTableModel
{
   Toy2[] toys2 = new Toy2[]
         {
               new Toy2(true, "one", 3),
               new Toy2(true, "two", 1),
               new Toy2(true, "three", 1)
         };

   public int getRowCount() { return 3; }
   public int getColumnCount() { return 3; }

   public Object getValueAt(int rowIndex, int columnIndex)
   {
      if (columnIndex == 0)
      {
         return toys2[rowIndex].checked;
      }
      else if (columnIndex == 1)
      {
         return toys2[rowIndex].name;
      }
      else if (columnIndex == 2)
      {
         return toys2[rowIndex].age;
      }
      throw new RuntimeException("What happened?");
   }
   public boolean isCellEditable(int rowIndex, int columnIndex)
   {
      return true;
   }

   public void setValueAt(Object value, int rowIndex, int columnIndex)
   {
      if (columnIndex == 0)
      {
         toys2[rowIndex].checked.setValue((EObject) value);
      }
      else if (columnIndex == 1)
      {
         toys2[rowIndex].name.setValue((EObject) value);
      }
      else if (columnIndex == 2)
      {
         toys2[rowIndex].age.setValue((EObject) value);
      }
   }
   
   public TableCellEditor getCellEditor(int column)
   {
      return new AtomicTableCellEditor();
   }
   public TableCellRenderer getCellRenderer(int column)
   {
      return new AtomicTableCellEditor();
   }
   
   public void print()
   {
      for (Toy2 toy2 : toys2)
      {
         System.out.println(toy2);
      }
   }

}

