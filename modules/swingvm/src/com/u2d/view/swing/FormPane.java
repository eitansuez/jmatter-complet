/*
 * Created on Apr 29, 2005
 */
package com.u2d.view.swing;

import java.awt.*;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

/**
 * @author Eitan Suez
 */
public class FormPane extends JPanel implements Scrollable
{
   public FormPane()
   {
      setOpaque(true);
      setBackground(new Color(0xfffaf0));
   }
   
   private static Dimension MAXSIZE = new Dimension(700,450);
   
   public Dimension getPreferredScrollableViewportSize()
   {
      Dimension p = getPreferredSize();
      p.height = Math.min(p.height, MAXSIZE.height);
      p.width = Math.min(p.width, MAXSIZE.width);
      return p;
   }
   public boolean getScrollableTracksViewportHeight()
   {
      if (getParent() instanceof JViewport)
      {
         JViewport viewport = (JViewport) getParent();
         int vpheight = viewport.getHeight();
         return (vpheight > getPreferredSize().height || vpheight == 0);
      }
      return false;
   }
   public boolean getScrollableTracksViewportWidth()
   {
      if (getParent() instanceof JViewport)
      {
         JViewport viewport = (JViewport) getParent();
         int vpwidth = viewport.getWidth();
         return (vpwidth > getPreferredSize().width || vpwidth == 0);
      }
      return false;
   }
   
   public int getScrollableUnitIncrement(Rectangle visibleRect,
         int orientation, int direction)
   {
      return (orientation == SwingConstants.HORIZONTAL) ? 80 : 30;
   }
   public int getScrollableBlockIncrement(Rectangle visibleRect,
         int orientation, int direction)
   {
      return (orientation == SwingConstants.HORIZONTAL) ? visibleRect.width : visibleRect.height;
   }
}