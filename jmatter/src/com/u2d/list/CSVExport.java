/*
 * Created on Mar 16, 2005
 */
package com.u2d.list;

import java.io.*;
import javax.swing.table.TableModel;
import com.u2d.model.*;
import com.u2d.field.Association;

/**
 * @author Eitan Suez
 */
public class CSVExport
{
   public static void export(AbstractListEO leo, File targetFile)
   {
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
      StringBuffer line;
      String item;
      
      for (int i=0; i<listTableModel.getRowCount(); i++)
      {
         line = new StringBuffer("");
         for (int j=0; j<listTableModel.getColumnCount(); j++)
         {
            Object value = listTableModel.getValueAt(i, j);
            if (value instanceof Association)
            {
               item = ((Association) value).get().toString();
            }
            else
            {
               item = value.toString();
            }
            line.append('"').append(item).append('"');
               
            if (j < listTableModel.getColumnCount() - 1)
               line.append(',');
         }
         pw.println(line.toString());
      }
   }
   
}
