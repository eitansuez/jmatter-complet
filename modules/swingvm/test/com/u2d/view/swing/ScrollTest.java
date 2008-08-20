package com.u2d.view.swing;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Enumeration;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Aug 11, 2008
 * Time: 12:06:51 PM
 */
public class ScrollTest extends JPanel
{
   public ScrollTest()
   {
      setLayout(new BorderLayout());

      JPanel contentPanel = new ContentPanel();
      contentPanel.add(new MyLabel("Hello World 1"));
      contentPanel.add(new MyLabel("Hello World 2"));
      contentPanel.add(new MyLabel("Hello World 3"));
      contentPanel.add(new MyLabel("Hello World 4"));
      contentPanel.add(new MyLabel("Hello World 5"));
      contentPanel.add(new MyLabel("Hello World 6"));
      
      add(new HorizScrollpane(contentPanel), BorderLayout.CENTER);
   }

   class MyLabel extends JLabel
   {
      MyLabel(String text)
      {
         super(text);
         setBorder(new LineBorder(Color.black, 1));
      }
   }

   class ContentPanel extends JPanel implements Scrollable
   {
      public ContentPanel()
      {
         super(new FlowLayout(FlowLayout.LEADING));
         setBorder(new LineBorder(Color.red, 1));
      }
      public Dimension getPreferredScrollableViewportSize()
      {
         return getPreferredSize();
      }

      public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
      {
         return 75;
      }

      public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
      {
         return 75;
      }

      public boolean getScrollableTracksViewportWidth()
      {
         if (getParent() instanceof JViewport)
         {
            return (getParent().getWidth() > getPreferredSize().width);
         }
         return false;
      }

      public boolean getScrollableTracksViewportHeight()
      {
         return false;
      }
   }
   
   public static void main(String[] args)
   {
      JFrame f = new JFrame("Scroll Test");
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.setContentPane(new ScrollTest());
      f.setLocation(200,200);
      f.pack();
      f.setSize(f.getWidth()-100, f.getHeight());
      f.setVisible(true);
   }
}

