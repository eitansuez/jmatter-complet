/*
 * Created on Mar 4, 2004
 */
package com.u2d.ui;

import java.awt.*;
import javax.swing.*;

/**
 * @author Eitan Suez
 */
public class MyTextArea extends JTextArea
{
   private static Color inactiveBackground = UIManager.getColor("TextField.inactiveBackground");
   private static Color activeBackground = UIManager.getColor("TextField.background");
   static
   {
   	UIDefaults defaults = UIManager.getDefaults();
      // make uidefaults for text areas match those of textfield:
      defaults.put("TextArea.background", activeBackground);
      defaults.put("TextArea.inactiveBackground", inactiveBackground);
      
      // (i've verified that (at least on my platform (macosx, java1.4.2)) uidefaults
      //  does not even have an entry for TextArea.inactiveBackground)
      // i've also verified that setting that uidefault really does nothing since
      //  the source code does not even reference it
   }

   public MyTextArea()
   {
      super();
   }
   
   public MyTextArea(String text)
   {
   	super(text);
   }
   
   public MyTextArea(int rows, int cols)
   {
      super(rows, cols);
   }
   
   public MyTextArea(String text, int rows, int cols)
   {
   	super(text, rows, cols);
   }
   
   // initialization code
   {
      // the intent here is to value the ability to tab between fields more highly
      // than the need to encode a tab when entering large text
      java.util.Set forwardTab = new java.util.HashSet();
      forwardTab.add(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_TAB, 0));
      setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardTab);
      
      java.util.Set backTab = new java.util.HashSet();
      backTab.add(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_TAB, java.awt.event.InputEvent.SHIFT_MASK));
      setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backTab);
   }
   
   public void setEditable(boolean editable)
   {
      super.setEditable(editable);
      // correction of something that i believe should happen in the TextAreaUI code..
      setBackground(editable ? activeBackground : inactiveBackground);
   }

}
