package com.u2d.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 29, 2005
 * Time: 2:17:55 PM
 */
public class DescriptionText extends JTextArea
{
   public DescriptionText()
   {
      super(3, 40);
      setOpaque(false);
      setEditable(false);
      setText("");
      setLineWrap(true);
      setWrapStyleWord(true);
      Font larger = getFont().deriveFont(12.0f); // 12 pt font
      setFont(larger);
   }
}
