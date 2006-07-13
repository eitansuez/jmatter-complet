package com.u2d.view.swing;

import com.u2d.list.CompositeList;
import com.u2d.view.swing.list.TableView;
import com.u2d.app.ViewMechanism;
import com.u2d.app.Application;
import com.u2d.model.AtomicEObject;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Mar 17, 2006
 * Time: 1:31:46 PM
 */
public class TableViewTester
{
   public TableViewTester()
   {
      Application app = new Application();
      ViewMechanism vmech = app.getViewMechanism();
      
      JFrame f = new JFrame();
      JPanel p = (JPanel) f.getContentPane();
      p.setLayout(new BorderLayout());

      CompositeList items = new CompositeList(Falafel.class);
      Date date = new Date();
      addFalafel(items, "One", 1, 1.3, date);
      addFalafel(items, "Two", 2, 5.4, date);
      addFalafel(items, "Three", 3, 6.5, date);
      addFalafel(items, "Four", 4, 7.8, date);

      final TableView view = new TableView(items);
      
      CellEditor editor = view.getDefaultEditor(AtomicEObject.class);
      editor.addCellEditorListener(new CellEditorListener()
      {
         public void editingStopped(ChangeEvent e)
         {
            int row = view.getSelectedRow();
            int col = view.getSelectedColumn();
            System.out.println("editing stopped; row: "+row+"; col: "+col);
         }

         public void editingCanceled(ChangeEvent e)
         {
            System.out.println("editing canceled");
         }
      });

      p.add(new JScrollPane(view), BorderLayout.CENTER);
      p.add(new JButton("Dummy"), BorderLayout.SOUTH);

      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.setLocation(100,100);
      f.pack();
      f.setVisible(true);
   }

   private void addFalafel(CompositeList items, String s, int i, double v, Date date)
   {
      Falafel falafel = new Falafel(s, i, v, date);
      falafel.setEditState();
      items.add(falafel);
      date.setTime(date.getTime()+10000);
   }


   public static void main(String[] args)
   {
      new TableViewTester();
   }

}
