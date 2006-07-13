/*
 * Created on Jan 27, 2004
 */
package com.u2d.ui;

import javax.swing.*;
import javax.swing.border.Border;

/**
 * @author Eitan Suez
 */
public class RenderHelper
{
   private static Border NO_BORDER = BorderFactory.createEmptyBorder(1,1,1,1);

   public static JComponent highlight(JTree tree, JComponent comp, boolean isSelected, boolean hasFocus)
   {
      comp.setBackground( isSelected ? UIManager.getColor("Tree.selectionBackground") : tree.getBackground() );
      comp.setForeground( isSelected ? UIManager.getColor("Tree.selectionForeground") : tree.getForeground() );
      comp.setBorder( hasFocus ? UIManager.getBorder("List.focusCellHighlightBorder") : NO_BORDER );
      return comp;
   }
   
   public static JComponent highlight(JList list, JComponent comp, boolean isSelected, boolean hasFocus)
   {
      comp.setBackground( isSelected ? list.getSelectionBackground() : list.getBackground() );
      comp.setForeground( isSelected ? list.getSelectionForeground() : list.getForeground() );
      comp.setBorder( hasFocus ? UIManager.getBorder("List.focusCellHighlightBorder") : NO_BORDER );
      return comp;
   }
}
