package com.u2d.ui;

import org.jdesktop.swingx.painter.Painter;
import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Dec 8, 2008
 * Time: 10:50:06 AM
 */
public class BaseToggleButton extends JToggleButton
{
   public BaseToggleButton()
   {
      setOpaque(false);
   }

   Painter bgPainter;
   public void setBackgroundPainter(Painter p) { this.bgPainter = p; }
   protected void paintComponent(Graphics g)
   {
      if (bgPainter != null)
      {
         bgPainter.paint((Graphics2D) g, this, getWidth(), getHeight());
      }
      super.paintComponent(g);
   }
}
