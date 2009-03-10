package com.u2d.ui.multipick;

import javax.swing.table.TableCellEditor;
import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Nov 20, 2008
 * Time: 11:14:10 AM
 */
public class MultiPickCellEditor extends AbstractCellEditor implements TableCellEditor
{
   MultiListPicker picker;
   public MultiPickCellEditor(MultiListPicker picker)
   {
      this.picker = picker;
   }

   public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
   {
      String valuesCommaSeparated = (String) value;
      picker.setValues(valuesCommaSeparated);
      return picker;
   }

   public Object getCellEditorValue()
   {
      return picker.getValues();
   }
}

