/*
 * Created on Apr 21, 2004
 */
package com.u2d.ui;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import javax.swing.*;
import javax.swing.event.*;

/**
 * Taken from forums.java.sun.com thread:
 *   http://forum.java.sun.com/thread.jsp?thread=261308&forum=57&message=1277552
 * 
 * Superior from PopupButton from the point of view that "..if you use a 
 *   JPopupMenu as your popup you get the closing behaviour for free.."
 * 
 * Does not exhibit the hiding of the popup upon initial attempt to show.
 *   On the other hand, does not work with nested JComboBoxes
 * 
 * @author Eitan Suez
 */
public class PopupButton2 extends JToggleButton implements PopupMenuListener, ItemListener
{
//   private JComponent _owner, _popupFor;
   private PopupClient _client;
   private JPopupMenu _menu = new JPopupMenu();
   private boolean _buttonWasPressed = false;

   public PopupButton2(Icon icon, JComponent owner, PopupClient client)
   {
      super(PICK_ICON);  // was: icon
      
      setMargin(new Insets(0, 1, 0, 1));
      setupEscapeKeyDismiss();
      setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      setContentAreaFilled(false);
      setRolloverEnabled(true);
      setRolloverIcon(PICK_ROLLOVER_ICON);
      setFocusPainted(false);
      setBorder(BorderFactory.createEmptyBorder(1,2,1,2));
      setFocusable(false);
      
//      _owner = owner;
      _client = client;
      
      addItemListener(this);
      _menu.addPopupMenuListener(this);

      // We need to keep track of whether the button was pressed or not
      // so the menu can determine if it should deselect the button.
      addMouseListener(
            new MouseAdapter()
              {
                 public void mousePressed(MouseEvent e) { _buttonWasPressed = true; }
                 public void mouseReleased(MouseEvent e) {_buttonWasPressed = false; }
              }
            );
   }
   
   public PopupButton2(Icon icon, JComponent owner, 
                      PopupClient client, JComponent popupFor)
   {
      this(icon, owner, client);
//      _popupFor = popupFor;
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
   
   public void hidePopup()
   {
      _menu.setVisible(false);
   }
   
   public void itemStateChanged(ItemEvent e)
   {
      if (!isSelected()) return;
      
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
                           //Component invoker = (_popupFor == null) ? PopupButton2.this : _popupFor;
                           Component invoker = PopupButton2.this;
                           _menu.removeAll();
                           _menu.add(contents);
                           _menu.show(invoker, 0, invoker.getHeight());
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

   public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
   {
      // only set the state of the button to non selected if
      // the close was triggered by something other that
      // clicking the button..
      if (!_buttonWasPressed)
         setSelected(false);
   }

   public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
   public void popupMenuCanceled(PopupMenuEvent e) {}
   
   
   static Icon PICK_ICON, PICK_ROLLOVER_ICON;
   static
   {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      URL imgURL = loader.getResource("images/open.png");
      PICK_ICON = new ImageIcon(imgURL);
      imgURL = loader.getResource("images/open_rollover.png");
      PICK_ROLLOVER_ICON = new ImageIcon(imgURL);
   }
   
}


 