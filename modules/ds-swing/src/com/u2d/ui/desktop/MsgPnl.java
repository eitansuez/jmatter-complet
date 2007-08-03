/*
 * Created on Dec 19, 2003
 */
package com.u2d.ui.desktop;

import org.jdesktop.swingx.JXPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.u2d.ui.UIUtils;
import com.u2d.css4swing.style.ComponentStyle;

/**
 * @author Eitan Suez
 */
public class MsgPnl extends JXPanel
{
   private JLabel _label;
   private static int _delay = 2000; // ms
   private Timer _timer;
   
   public MsgPnl()
   {
      this(2000);
   }
   
   public MsgPnl(int delay)
   {
      ComponentStyle.addClass(this, "feedback-pane");
      setVisible(false);
      
      _label = new JLabel();
      ComponentStyle.setIdent(_label, "msg-label");
      
      setLayout(new BorderLayout());
      add(_label, BorderLayout.CENTER);
      
      addMouseListener(new MouseAdapter() {
         public void mousePressed(MouseEvent evt) { dismiss(); }
      });
      addKeyListener(new KeyAdapter() {
         public void keyPressed(KeyEvent evt) { dismiss(); }
      });
      
      _delay = delay;
      _timer = new Timer(_delay, new ActionListener() {
         public void actionPerformed(ActionEvent e) { dismiss(); }
      });
      _timer.setRepeats(false);
      _timer.setCoalesce(false);
   }
   
   public void setDelay(int delayMs)
   {
      _delay = delayMs;
      _timer.setInitialDelay(_delay);
      _timer.setDelay(_delay);
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
}
