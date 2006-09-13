package com.u2d.view.swing.atom;

import com.u2d.model.AtomicEObject;
import com.u2d.model.AtomicRenderer;
import com.u2d.utils.BrowserLauncher;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Date: Jun 8, 2005
 * Time: 2:47:28 PM
 *
 * A Hyperlink View
 *
 * @author Eitan Suez
 */
public class URIRenderer extends JLabel implements AtomicRenderer
{
   public URIRenderer()
   {
      setForeground(Color.blue);
      setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

      addMouseListener(new MouseAdapter()
      {
         public void mouseClicked(MouseEvent evt)
         {
            BrowserLauncher.openInBrowser(getText());
         }
      });
   }

   protected void paintComponent(Graphics g)
   {
      super.paintComponent(g);
      g.setColor(Color.blue);
      FontMetrics fm = g.getFontMetrics();
      int x = getInsets().left;
      int y = fm.getAscent() + getInsets().top + 4;
      int width = fm.stringWidth(getText());
      g.drawLine(x, y, x+width, y);
   }

   public void render(AtomicEObject value)
   {
      setText(value.toString());
   }

   public void passivate() { }
}
