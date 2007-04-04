/*
 * Created on Dec 19, 2003
 */
package com.u2d.ui.desktop;

import org.jdesktop.swingx.painter.AbstractPainter;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.JXPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.event.*;
import com.u2d.ui.UIUtils;
import com.u2d.css4swing.style.ComponentStyle;

/**
 * @author Eitan Suez
 */
public class MsgPnl extends JXPanel
{
   private static Painter backgroundPainter = new MsgPanelPainter();
   
   private JLabel _label;
   private static int _delay = 2000; // ms
   private Timer _timer;
   
   public MsgPnl()
   {
      setBackgroundPainter(backgroundPainter);
      ComponentStyle.addClass(this, "feedback-pane");
      setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
      setVisible(false);
      
      _label = new JLabel();
      
      setLayout(new BorderLayout());
      add(_label, BorderLayout.CENTER);
      
      addMouseListener(new MouseAdapter() {
         public void mousePressed(MouseEvent evt) { dismiss(); }
      });
      addKeyListener(new KeyAdapter() {
         public void keyPressed(KeyEvent evt) { dismiss(); }
      });
      
      _timer = new Timer(_delay, new ActionListener() {
         public void actionPerformed(ActionEvent e) { dismiss(); }
      });
      _timer.setRepeats(false);
      _timer.setCoalesce(false);
   }
   
   public void message(String msg, JComponent parent)
   {
      _label.setText(msg);
      revalidate(); repaint();
      setSize(getPreferredSize());
      setLocation(UIUtils.computeCenter(parent, this));
      setVisible(true);
      _timer.restart();
   }

   private void dismiss()
   {
      _label.setText("");
      setVisible(false);
   }

   static class MsgPanelPainter extends AbstractPainter
   {
      public MsgPanelPainter()
      {
         super();
         setUseCache(false);
         setAntialiasing(RenderingHints.VALUE_ANTIALIAS_ON);
      }

      public void paintBackground(Graphics2D g2, JComponent c)
      {
         Composite composite = g2.getComposite();
         Color color = g2.getColor();

         
         float alpha = 0.7f;
         if (composite instanceof AlphaComposite)
         {
            alpha *= ((AlphaComposite) composite).getAlpha();
         }
         
         g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
         g2.setColor(c.getBackground());
         RoundRectangle2D rect = new RoundRectangle2D.Double(0, 0, c.getWidth() - 1, c.getHeight() - 1, 24, 24);
         g2.fill(rect);

         
         g2.setColor(color);
         g2.setComposite(composite);
      }
   }
   
}
