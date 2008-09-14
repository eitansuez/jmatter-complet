package com.u2d.view.swing;

import com.u2d.model.ComplexType;
import com.u2d.pubsub.AppEventListener;
import com.u2d.pubsub.AppEvent;
import com.u2d.ui.UIUtils;
import com.u2d.css4swing.style.ComponentStyle;
import javax.swing.*;
import java.net.URL;
import java.awt.event.*;
import java.awt.*;
import java.util.Locale;
import net.miginfocom.swing.MigLayout;

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
   private static String WAIT_MSG = ComplexType.localeLookupStatic("launching_application");

   private Timer timer;
   private String[] dotsarray = {".", "..", "...", "....", "...", ".."};
   private int arrayindex = 0;

   private JLabel _messageLabel;
   private URL _imgURL = resolveSplashURL();

   public Splash()
   {
      layMeOut();
      applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
   }
   
   private void layMeOut()
   {
      MigLayout layout = new MigLayout("flowy, fill", "[max(300,pref)]", "");
      JPanel contentPane = new JPanel(layout);
      ComponentStyle.addClass(contentPane, "splash-pane");
      _messageLabel = new JLabel();
      ComponentStyle.addClass(_messageLabel, "message-label");
      if (WAIT_MSG == null) WAIT_MSG = "Launching Application";
      _messageLabel.setText(WAIT_MSG);
      
      
      if (_imgURL == null)
      {
         System.err.println("hint: if you place a file called 'splash.[png|gif|jpg] "
           + "in resources/images then it will automatically be used as a splash image");
      }
      else
      {
         JLabel imgLabel = new JLabel(new ImageIcon(_imgURL));
         imgLabel.setOpaque(false);
         contentPane.add(imgLabel, "alignx center, aligny bottom");
      }
      contentPane.add(_messageLabel, "alignx leading, aligny center");
      setContentPane(contentPane);

      timer = new Timer(500, new ActionListener()
      {
         public void actionPerformed(ActionEvent evt)
         {
            if (_msg != null && _msg.length() > 0) return;
            _messageLabel.setText(WAIT_MSG + dotsarray[arrayindex++ % dotsarray.length]);
         }
      });
      timer.setRepeats(true);

      new MovableSupport(this);
      
      pack();
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
   
}
