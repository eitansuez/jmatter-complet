package com.u2d.ui;

import javax.swing.*;
import java.net.URL;
import java.awt.*;
import java.awt.event.*;

/**
 * Date: May 25, 2005
 * Time: 9:16:00 PM
 *
 * Window a little more 'lightweight' (memory-wise) compared to jwindow
 *
 * @author Eitan Suez
 */
public class Splash extends JWindow
{
   private static String WAIT_MSG = "Launching Application";

   private int fromX, fromY;
   private Timer timer;
   private StringBuffer dots = new StringBuffer(".    ");
   private boolean forward = true;
   private int position = 0;

   private JPanel _p = new JPanel(new BorderLayout())
   {
      {
         setBackground(Color.white);
         setOpaque(true);
         setBorder(BorderFactory.createLineBorder(Color.BLACK));
      }
   };

   private JLabel _messageLabel = new JLabel(WAIT_MSG)
   {
      {
         setFont(new Font("SansSerif", Font.ITALIC, 12));
         setOpaque(false);
      }

      public Insets getInsets(Insets insets)
      {
         return new Insets(15, 15, 15, 15);
      }
   };

   public Splash()
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run() { begin(); }
      });
   }
   
   private void begin()
   {
      URL imgURL = resolveSplashURL();
      if (imgURL == null)
      {
         System.err.println("hint: if you place a file called 'splash.[png|gif|jpg] "
           + "in resources/images then it will automatically be used as a splash image");
      }
      else
      {
         JLabel imgLabel = new JLabel(new ImageIcon(imgURL));
         imgLabel.setOpaque(false);
         _p.add(imgLabel, BorderLayout.CENTER);
      }
      _p.add(_messageLabel, BorderLayout.SOUTH);
      getContentPane().add(_p, BorderLayout.CENTER);

      timer = new Timer(500, new ActionListener()
      {
         public void actionPerformed(ActionEvent evt)
         {
            if (_msg != null && _msg.length() > 0) return;

            position = (forward) ? position + 1 : position - 1;
            char c = (forward) ? '.' : ' ';
            dots.setCharAt(position, c);

            if ((position >= 4 && forward) || (position <= 0 && !forward))
            {
               position = (forward) ? position + 1 : position - 1;
               forward = !forward;
            }

            _messageLabel.setText(WAIT_MSG + dots);
         }
      });
      timer.setRepeats(true);

      addMouseListener(new MouseAdapter()
      {
         public void mousePressed(MouseEvent evt)
         {
            fromX = evt.getX();
            fromY = evt.getY();
         }
      });

      addMouseMotionListener(new MouseMotionAdapter()
      {
         public void mouseDragged(MouseEvent e)
         {
            setLocation(getLocation().x + e.getX() - fromX,
                        getLocation().y + e.getY() - fromY);
         }
      });

      pack();
      setSize(getWidth()+100, getHeight());
      UIUtils.centerOnScreen(this);
      timer.start();
      setVisible(true);
   }

   String _msg = "";
   public void message(final String text)
   {
      if (SwingUtilities.isEventDispatchThread())
      {
         _msg = text;
         _messageLabel.setText(_msg);
      }
      else
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               _msg = text;
               _messageLabel.setText(_msg);
            }
         });
      }
   }
   
   private URL resolveSplashURL()
   {
      String[] suffixes = {"png", "jpg", "gif"};
      int i=0;
      ClassLoader loader = getClass().getClassLoader();
      URL url = null;
      while ( (i < suffixes.length) &&
              ( (url = loader.getResource("images/splash."+suffixes[i])) == null )
            ) { i++; }
      return url;
   }

   public void dispose()
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            timer.stop();
            Splash.super.dispose();
         }
      });
   }
}
