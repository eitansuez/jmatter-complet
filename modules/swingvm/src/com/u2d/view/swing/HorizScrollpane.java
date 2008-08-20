package com.u2d.view.swing;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Aug 11, 2008
 * Time: 2:41:08 PM
 */
public class HorizScrollpane extends JPanel implements ChangeListener
{
   JViewport viewport = new JViewport();
   private JButton lbtn;
   private JButton rbtn;
   private JPanel btnPnl;

   public HorizScrollpane(Component view)
   {
      setLayout(new BorderLayout());

      viewport.setView(view);
      viewport.addChangeListener(this);
      add(viewport, BorderLayout.CENTER);
      btnPnl = new JPanel(new FlowLayout(FlowLayout.LEADING, 2, 0));
      btnPnl.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
      lbtn = new NavButton(ARROW_ICON_LEFT);
      lbtn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            Point viewportPos = viewport.getViewPosition();
            int x = viewportPos.x;
            x -= unitIncrement(-1);
            x = Math.max(0, x);
            viewport.setViewPosition(new Point(x, viewportPos.y));
         }
      });
      rbtn = new NavButton(ARROW_ICON_RIGHT);
      rbtn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            Point viewportPos = viewport.getViewPosition();
            int x = viewportPos.x;
            x += unitIncrement(+1);
            x = Math.min(x, viewport.getView().getWidth() - viewport.getWidth());
            viewport.setViewPosition(new Point(x, viewportPos.y));
         }
      });
      btnPnl.add(lbtn);
      btnPnl.add(rbtn);
      add(btnPnl, BorderLayout.LINE_END);
   }

   private int unitIncrement(int direction)
   {
      Component view = viewport.getView();
      if (view instanceof Scrollable)
      {
         return ((Scrollable) view).getScrollableUnitIncrement(viewport.getViewRect(), SwingConstants.HORIZONTAL, direction);
      }
      return 10;
   }

   public void stateChanged(ChangeEvent e)
   {
      boolean clipped = ( viewport.getWidth() < viewport.getView().getWidth() );
      btnPnl.setVisible(clipped);
      rbtn.setEnabled(clipped);
      lbtn.setEnabled(clipped);
   }

   public static ImageIcon ARROW_ICON_LEFT, ARROW_ICON_RIGHT;
   static
   {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      URL url = loader.getResource("images/arrow_lt.gif");
      ARROW_ICON_LEFT = new ImageIcon(url);
      url = loader.getResource("images/arrow_rt.gif");
      ARROW_ICON_RIGHT = new ImageIcon(url);
   }

}

class NavButton extends JButton
{
   public NavButton(Icon icon)
   {
      super(icon);
      setVerticalAlignment(SwingConstants.CENTER);
      setHorizontalAlignment(SwingConstants.CENTER);
      setContentAreaFilled(false);
      setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
   }
   public NavButton(Icon icon, Icon rollover)
   {
      this(icon);
      setRolloverEnabled(true);
      setRolloverIcon(rollover);
   }

   public Dimension getPreferredSize()
   {
      int width = 11;
      Insets insets = getParent().getInsets();
      int height = getParent().getHeight() - (insets.top + insets.bottom);
      return new Dimension(width, height);
   }
}
