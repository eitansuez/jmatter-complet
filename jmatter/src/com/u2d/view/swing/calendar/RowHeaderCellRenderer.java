/*
 * Created on Nov 22, 2004
 */
package com.u2d.view.swing.calendar;

import java.awt.*;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

/**
 * @author Eitan Suez
 */
public class RowHeaderCellRenderer implements TableCellRenderer
{
   JLabel _cell;
   
   RowHeaderCellRenderer()
   {
      _cell = new JLabel();
      _cell.setOpaque(true);
      Color color = UIManager.getColor("TableHeader.background");
      _cell.setBackground(color);
      
      Font font = UIManager.getFont("TableHeader.font");
      _cell.setFont(font.deriveFont(10.0f));
      _cell.setVerticalAlignment(SwingConstants.TOP);
      _cell.setHorizontalAlignment(SwingConstants.RIGHT);
      _cell.setBorder(BorderFactory.createEmptyBorder(0,2,2,2));
   }
   
   public Component getTableCellRendererComponent(JTable table, Object value, 
                              boolean isSelected, boolean hasFocus, int row, int column)
   {
      _cell.setText(value.toString());
      return _cell;
   }
}

