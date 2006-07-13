package com.u2d.ui.sorttable;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.net.URL;

/**
 * Date: May 18, 2005
 * Time: 11:06:04 AM
 *
 * @author Eitan Suez
 */
public class TableSortSupport
{
   private SortColumnHeader[] _columnHeaderComponents;
   private JTable _table;

   public TableSortSupport(JTable table)
   {
      _table = table;

      CustomTableHeaderRenderer customTableHeaderRenderer =
            new CustomTableHeaderRenderer();

      TableColumn col = null;
      int colCount = _table.getColumnModel().getColumnCount();
      _columnHeaderComponents = new SortColumnHeader[colCount];
      for (int i = 0; i < colCount; i++)
      {
         col = _table.getColumnModel().getColumn(i);
         col.setHeaderRenderer(customTableHeaderRenderer);
         _columnHeaderComponents[i] = new SortColumnHeader(i);
      }

      _table.getTableHeader().addMouseListener(new MouseAdapter()
      {
         public void mousePressed(MouseEvent e)
         {
            if (!SwingUtilities.isLeftMouseButton(e)) return;

            if (_table.getTableHeader().getResizingColumn() == null)
            {
               Point p = e.getPoint();
               TableColumnModel columnModel = _table.getTableHeader().getColumnModel();
               int colIdx = columnModel.getColumnIndexAtX(p.x);

               if (colIdx == -1) return;

               SortTableModel model = (SortTableModel) _table.getModel();
               if (!model.isColumnSortable(colIdx))
                  return;

               for (int i = 0; i < _columnHeaderComponents.length; i++)
               {
                  _columnHeaderComponents[i].setSelected(i == colIdx);
               }
            }
         }
      });
   }

   class CustomTableHeaderRenderer implements TableCellRenderer
   {
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                     boolean hasFocus, int rowIdx, int colIdx)
      {
         SortColumnHeader comp = _columnHeaderComponents[colIdx];
         comp.setText(value.toString());
         return comp;
      }
   }


   class SortColumnHeader extends JLabel
   {
      boolean _ascending = true;
      int _colIndex;
      boolean _selected = false;

      public SortColumnHeader(int colIndex)
      {
         _colIndex = colIndex;

         TableCellRenderer renderer = _table.getTableHeader().getDefaultRenderer();
         Object value = _table.getColumnModel().getColumn(colIndex).getHeaderValue();
         JLabel label = (JLabel)
               renderer.getTableCellRendererComponent(
                 _table, value, false, false, -1, colIndex);

         setForeground(label.getForeground());
         setBackground(label.getBackground());
         setFont(label.getFont());
         setBorder(label.getBorder());
         setHorizontalAlignment(label.getHorizontalAlignment());
      }

      public void setSelected(boolean selected)
      {
         if (selected)
         {
            if (_selected)
            {
               _ascending = !_ascending;
            }
            _selected = true;

            final SortTableModel model =
                  (SortTableModel) _table.getModel();

            Cursor wait = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
            _table.setCursor(wait);

            new Thread()
            {
               public void run()
               {
                  try
                  {
                     model.sort(_colIndex, _ascending);
                  }
                  finally
                  {
                     SwingUtilities.invokeLater(new Runnable()
                     {
                        public void run()
                        {
                           _table.setCursor(Cursor.getDefaultCursor());
                        }
                     });
                  }
               }
            }.start();


            setIcon(icon());
         }
         else
         {
            setIcon(null);
            _selected = false;
            _ascending = true;
         }
      }

      private Icon icon()
      {
         return (_ascending) ? ASC_ICON : DESC_ICON;
      }

   }

   // image resources:

   static String asc_resource = "images/sort_ascending.png";
   static String desc_resource = "images/sort_descending.png";
   static Icon ASC_ICON, DESC_ICON;
   static
   {
      URL url = TableSortSupport.class.getClassLoader().getResource(asc_resource);
      ASC_ICON = new ImageIcon(url);
      url = TableSortSupport.class.getClassLoader().getResource(desc_resource);
      DESC_ICON = new ImageIcon(url);
   }


}
