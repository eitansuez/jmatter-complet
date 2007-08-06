package com.u2d.view.swing;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 6, 2007
 * Time: 5:48:05 PM
 */
public class MovableSupport
{
   private int fromX, fromY;
   
   public MovableSupport(final Container target)
   {
      target.addMouseListener(new MouseAdapter()
      {
         public void mousePressed(MouseEvent
         evt)
         {
            fromX = evt.getX();
            fromY = evt.getY();
         }
      });

      target.addMouseMotionListener(new MouseMotionAdapter()
      {
         public void mouseDragged(MouseEvent e)
         {
            target.setLocation(target.getLocation().x + e.getX() - fromX,
                        target.getLocation().y + e.getY() - fromY);
         }
      });
   }
      
      
}
