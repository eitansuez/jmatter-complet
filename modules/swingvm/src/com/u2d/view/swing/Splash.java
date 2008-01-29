package com.u2d.view.swing;

import com.u2d.model.ComplexType;
import com.u2d.pubsub.AppEventListener;
import com.u2d.pubsub.AppEvent;
import com.u2d.ui.UIUtils;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;
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
public class Splash extends JWindow implements AppEventListener
{
   private static final String WAIT_MSG =  ComplexType.localeLookupStatic("launching_application");

   private Timer timer;
   private StringBuffer dots = new StringBuffer(".    ");
   private boolean forward = true;
   private int position = 0;

   private JLabel _messageLabel;
   private URL _imgURL = resolveSplashURL();

   public Splash()
   {
      super();
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run() { layMeOut(); }
      });
   }
   
   private void layMeOut()
   {
      FormLayout layout = new FormLayout("max(300px;pref)", "pref, pref");
      CellConstraints cc = new CellConstraints();
      JPanel contentPane = new ContentPane(layout);
      _messageLabel = new MessageLabel();
      
      if (_imgURL == null)
      {
         System.err.println("hint: if you place a file called 'splash.[png|gif|jpg] "
           + "in resources/images then it will automatically be used as a splash image");
      }
      else
      {
         JLabel imgLabel = new JLabel(new ImageIcon(_imgURL));
         imgLabel.setOpaque(false);
         contentPane.add(imgLabel, cc.rc(1, 1, "center, center"));
      }
      contentPane.add(_messageLabel, cc.rc(2, 1, "center, left"));
      setContentPane(contentPane);

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

      new MovableSupport(this);
      
      pack();
      if (getWidth() < 200)
      {
         setSize(getWidth()+100, getHeight());
      }
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


   public void onEvent(AppEvent evt)
   {
      String text = (String) evt.getEventInfo();
      message(text);
   }

   private URL resolveSplashURL()
   {
      String[] suffixes = {"png", "jpg", "gif"};
      int i=0;
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
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
   
   
   static class ContentPane extends JPanel
   {
      public ContentPane(LayoutManager layout)
      {
         super(layout);
         setBackground(Color.white);
         setOpaque(true);
         setBorder(BorderFactory.createLineBorder(Color.BLACK));
      }
   }
   static class MessageLabel extends JLabel
   {
      Insets _insets = new Insets(15, 15, 15, 15);
      public MessageLabel()
      {
         super(WAIT_MSG);
         setFont(new Font("SansSerif", Font.ITALIC, 12));
         setOpaque(false);
      }
      public Insets getInsets(Insets insets) { return _insets; }
   }
   
}
