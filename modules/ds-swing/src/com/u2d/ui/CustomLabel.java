/*
 * Created on Apr 12, 2004
 */
package com.u2d.ui;

import javax.swing.*;
import java.awt.*;

/**
 * @author Eitan Suez
 */
public class CustomLabel extends JLabel
{
   public CustomLabel(float fontSize, int horizAlignment)
   {
      setFont(getFont().deriveFont(fontSize));
      setHorizontalAlignment(horizAlignment);
   }
   
   public CustomLabel(float fontSize, int horizAlignment, Color bgColor, boolean isOpaque)
   {
      this(fontSize, horizAlignment);
      setBackground(bgColor);
      setOpaque(isOpaque);
   }
}
