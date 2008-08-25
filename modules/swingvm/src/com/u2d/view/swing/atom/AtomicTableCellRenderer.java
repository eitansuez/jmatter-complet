/*
 * Created on Aug 31, 2004
 */
package com.u2d.view.swing.atom;

import com.u2d.type.atom.NumericEO;

/**
 * @author Eitan Suez
 */
public class AtomicTableCellRenderer extends javax.swing.table.DefaultTableCellRenderer
{
   public AtomicTableCellRenderer()
   {
      super();
   }
   
   public void setValue(Object value)
   {
      if (value != null && value instanceof NumericEO)
         setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
      super.setValue(value);
   }
}
