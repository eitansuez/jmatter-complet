package com.u2d.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Mar 27, 2008
 * Time: 4:58:35 PM
 */
public class StripedRowListCellRenderer extends JLabel implements ListCellRenderer
{
   private static Color DEFAULT_COLOR = new Color(0x90DB90);
   private Color _color;

   {
      setOpaque(true);
   }
   
   public StripedRowListCellRenderer()
   {
      _color = DEFAULT_COLOR;
   }

   public StripedRowListCellRenderer(Color color)
   {
      _color = color;
   }

   public java.awt.Component getListCellRendererComponent(JList list, Object value,
                                                          int index, boolean selected, boolean hasFocus)
   {
      setText(value.toString());
      
      RenderHelper.highlight(list, this, selected, hasFocus);
      boolean odd = (index % 2) == 1;
      if (odd && !selected)
      {
         setBackground(_color);
      }
      return this;
   }

}
