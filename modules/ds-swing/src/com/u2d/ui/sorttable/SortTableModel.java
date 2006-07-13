package com.u2d.ui.sorttable;

import javax.swing.table.TableModel;

/**
 * Date: May 18, 2005
 * Time: 5:42:18 PM
 *
 * @author Eitan Suez
 */
public interface SortTableModel extends TableModel
{
   public void sort(int colIndex, boolean ascending);
   public boolean isColumnSortable(int colIndex);
}
