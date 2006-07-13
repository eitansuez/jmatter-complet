/*
 * Created on Apr 2, 2004
 */
package com.u2d.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author Eitan Suez
 */
public class PopupButton extends JButton implements ActionListener
{
   private JComponent _owner, _popupFor;
   private PopupClient _client;
   private Popup _popup;
   private boolean _isShowing = false;  // i wish i didn't have to do this but swing popup buggy
     // (i.e. won't tell you its state and if show/hide called in improper state then behaves
     //    irresponsibly)
   
   public PopupButton(Icon icon, JComponent owner, PopupClient client)
   {
      super(icon);
      
      setFocusPainted(false);
      setMargin(new Insets(0, 1, 0, 1));
      setupEscapeKeyDismiss();
      setFocusable(false);  // this makes WHEN_FOCUSED never be true which nullifies above line
      
      _owner = owner;
      _client = client;
      
      addActionListener(this);
   }
   
   public PopupButton(Icon icon, JComponent owner, 
                      PopupClient client, JComponent popupFor)
   {
      this(icon, owner, client);
      _popupFor = popupFor;
   }
   
   private static String MAP_KEY = "HIDE_POPUP";
   private static KeyStroke ESCAPE = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0 /* no modifiers */);
   private void setupEscapeKeyDismiss()
   {
      getInputMap(WHEN_FOCUSED).put(ESCAPE, MAP_KEY);
      getActionMap().put(MAP_KEY, new AbstractAction()
         {
            public void actionPerformed(ActionEvent evt)
            {
               hidePopup();
            }
         });
   }
   
   private Point computeLocation()
   {
      if (_popupFor == null)
      {
         Point p = getLocationOnScreen();
         p.y += getHeight();
         return p;
      }
      
      Point p = _popupFor.getLocationOnScreen();
      p.y += _popupFor.getHeight();
      return p;
   }

   
   private void showPopup()
   {
      if (_popup == null) return;
      if (!_isShowing)
      {
         _popup.show();
         _isShowing = true;
      }
   }
   
   public void hidePopup()
   {
      if (_popup == null) return;
      if (_isShowing)
      {
         _popup.hide();
         _isShowing = false;
      }
   }
   
   public void actionPerformed(ActionEvent evt)
   {
      if (_isShowing)
      {
         hidePopup();
         return;
      }
      
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      
      new Thread()
         {
            public void run()
            {
               try
               {
                  // 1. don't clog ui thread to fetch contents..
                  final JComponent contents = _client.getContents();
                  
                  // 2.  one have the contents, use ui thread to display it..
                  SwingUtilities.invokeLater(new Runnable()
                     {
                        public void run()
                        {
                           Point p = computeLocation();
                           _popup = PopupFactory.getSharedInstance().getPopup(_owner, contents, p.x, p.y);
                           showPopup();
                        }
                     });
               }
               finally
               {
                  setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
               }
            }
         }.start();
   }

}
