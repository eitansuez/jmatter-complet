/*
 * Created on Apr 29, 2005
 */
package com.u2d.view.swing;

import org.jdesktop.swingx.JXPanel;
import javax.swing.*;
import java.awt.*;

/**
 * @author Eitan Suez
 */
public class FormPane extends JXPanel implements Scrollable
{
   public FormPane()
   {
      super();
      setOpaque(true);
   }
   public FormPane(LayoutManager layout)
   {
      super(layout);
      setOpaque(true);
   }
   
   private static Dimension MAXSIZE = new Dimension(700,500);
   @Override
   public Dimension getMaximumSize() { return MAXSIZE; }

   public Dimension getPreferredScrollableViewportSize()
   {
      Dimension p = getPreferredSize();
      
      if (getScrollableTracksViewportWidth())
      {
         p.width += 20;
      }
      if (getScrollableTracksViewportHeight())
      {
         p.height += 20;
      }
      
      p.height = Math.min(p.height, getMaximumSize().height);
      p.width = Math.min(p.width, getMaximumSize().width);
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
