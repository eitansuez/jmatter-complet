package com.u2d.ui;

import org.jdesktop.swingx.painter.Painter;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Dec 5, 2008
 * Time: 12:40:06 PM
 */
public class BasePopupMenu extends JPopupMenu
{
   public BasePopupMenu()
   {
      super();
      setLightWeightPopupEnabled(true);
      setOpaque(false);
   }

   public JMenuItem add(JMenuItem menuItem)
   {
      menuItem.setOpaque(false);
      return super.add(menuItem);
   }

   public JMenuItem add(Action a)
   {
      JMenuItem item = super.add(a);
      item.setOpaque(false);
      return item;
   }

   Painter bgPainter;
   public void setBackgroundPainter(Painter p) { this.bgPainter = p; }
   protected void paintComponent(Graphics g)
   {
      setOpaque(bgPainter == null);
      if (bgPainter != null)
      {
         bgPainter.paint((Graphics2D) g, this, getWidth(), getHeight());
      }
      super.paintComponent(g);
   }
   
}
