/*
 * Created on May 2, 2005
 */
package com.u2d.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import javax.swing.border.LineBorder;

/**
 * @author Eitan Suez
 */
public class LineBorderEnh extends LineBorder
{
   protected int _cornerRadius = 5;
   
   public LineBorderEnh(Color color, int thickness, int cornerRadius)
   {
      super(color, thickness, true);
      _cornerRadius = cornerRadius;
   }
   
   public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
   {
      if (!roundedCorners)
      {
         super.paintBorder(c, g, x, y, width, height);
         return;
      }
      
      Color oldColor = g.getColor();
      Graphics2D g2 = (Graphics2D) g;
      Stroke oldStroke = g2.getStroke();

      g2.setColor(lineColor);
      g2.setStroke(new BasicStroke(thickness));
      
      g2.drawRoundRect(x, y, width-thickness, height-thickness, _cornerRadius, _cornerRadius);
      
      g2.setStroke(oldStroke);
      g.setColor(oldColor);
      
  }
   
}
