/*
 * Created on Mar 16, 2005
 */
package com.u2d.list;

import java.io.*;
import java.awt.Component;
import javax.swing.*;
import javax.swing.table.TableModel;
import com.u2d.element.*;
import com.u2d.model.*;

/**
 * @author Eitan Suez
 */
public class CSVExport
{
   private static File START_PATH = new File(System.getProperty("user.home"));

   public static void export(CommandInfo cmdInfo, AbstractListEO leo)
   {
      JFileChooser chooser = new JFileChooser(START_PATH);
      chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      
      String fileName = leo.type().getPluralName() + ".csv";
      String targetFileName = START_PATH + File.separator + fileName;
      
      chooser.setSelectedFile(new File(targetFileName));
      int result = chooser.showSaveDialog((Component) cmdInfo.getSource());
      if (result != JFileChooser.APPROVE_OPTION)
         return;

      File targetFile = chooser.getSelectedFile();
      START_PATH = targetFile.getParentFile();

      PrintWriter pw = null;
      try
      {
         pw = new PrintWriter(new FileWriter(targetFile));

         TableModel listTableModel = leo.tableModel();
         StringBuffer line = new StringBuffer();
         String item;
         
         for (int i=0; i<listTableModel.getColumnCount(); i++)
         {
            item = listTableModel.getColumnName(i);
            line.append('"').append(item).append('"');
            
            if ( i < listTableModel.getColumnCount() - 1 )
               line.append(',');
         }
         pw.println(line.toString());
         
         if (leo instanceof Paginable)
         {
            // must write all the pages..
            Paginable paginable = (Paginable) leo;
            paginable.firstPage();
            writeList(leo.tableModel(), pw);
            while (paginable.hasNextPage())
            {
               paginable.nextPage();
               writeList(leo.tableModel(), pw);
            }
         }
         else
         {
            writeList(listTableModel, pw);
         }
         
      }
      catch (IOException ex)
      {
         System.err.println(ex.getMessage());
         ex.printStackTrace();
      }
      finally
      {
         if (pw != null)
            pw.close();
      }
   }
   
   private static void writeList(TableModel listTableModel, PrintWriter pw)
   {
      StringBuffer line = null;
      String item = null;
      
      for (int i=0; i<listTableModel.getRowCount(); i++)
      {
         line = new StringBuffer("");
         for (int j=0; j<listTableModel.getColumnCount(); j++)
         {
            item = listTableModel.getValueAt(i, j).toString();
            line.append('"').append(item).append('"');
               
            if (j < listTableModel.getColumnCount() - 1)
               line.append(',');
         }
         pw.println(line.toString());
      }
   }
   
}
