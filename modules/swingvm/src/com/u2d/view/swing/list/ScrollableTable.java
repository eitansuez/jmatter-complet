package com.u2d.view.swing.list;

import com.u2d.ui.SeeThruTable;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.table.TableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Aug 7, 2008
 * Time: 5:01:59 PM
 */
public class ScrollableTable extends SeeThruTable
{
   int _tableWidth;

   public void adjustColumnWidths()
   {
      _tableWidth = 0;
      TableModel tableModel = getModel();
      for (int col=0; col<tableModel.getColumnCount(); col++)
      {
         int width = 50;  // a minimum

         TableColumn tc = getTableHeader().getColumnModel().getColumn(col);
         width = Math.max(tc.getPreferredWidth(), width);

         for (int row=0; row<tableModel.getRowCount(); row++)
         {
            Component comp = getTableComponentAt(row, col, tableModel);
            width = Math.max(comp.getPreferredSize().width, width);
            width = Math.min(width, 400); // a maximum column width
         }
         getColumnModel().getColumn(col).setPreferredWidth(width);
         _tableWidth += width;
      }
      _tableWidth += ( tableModel.getColumnCount() * getColumnModel().getColumnMargin() );
   }

   protected Component getTableComponentAt(int row, int col, TableModel tableModel)
   {
      TableCellRenderer cellRenderer = getCellRenderer(row, col);
      Object value = tableModel.getValueAt(row, col);
      return cellRenderer.getTableCellRendererComponent(this, value, false, false, row, col);
   }


   private static int MAXHEIGHT = 500;
   private static int MAXWIDTH = 800;

   public Dimension getPreferredScrollableViewportSize()
   {
      Dimension preferred = super.getPreferredScrollableViewportSize();
      preferred.height =  getModel().getRowCount() * getRowHeight() + 50;
      preferred.height = Math.min(preferred.height, MAXHEIGHT);

      preferred.width = Math.min(_tableWidth, MAXWIDTH);

      return preferred;
   }

   public boolean getScrollableTracksViewportWidth()
   {
      if (getParent() instanceof JViewport)
      {
         JViewport viewport = (JViewport) getParent();
         int vpwidth = viewport.getWidth();
         return (vpwidth > getPreferredSize().width || vpwidth == 0);
      }
      return false;
   }


   public Component prepareEditor(TableCellEditor editor, int row, int column)
   {
      Component editorComponent = super.prepareEditor(editor, row, column);
      if (editorComponent instanceof JTextComponent)
      {
         ((JTextComponent) editorComponent).selectAll();
      }
      return editorComponent;
   }

}
