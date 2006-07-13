/*
 * Created on Nov 21, 2003
 */
package com.u2d.ui;

import java.awt.*;
import javax.swing.*;

/**
 * @author Eitan Suez
 */
public class SmallButton extends JButton
{
	public SmallButton(String caption)
	{
		super(caption);
      setCharacteristics();
	}
   public SmallButton(javax.swing.Action action)
   {
      super(action);
      setCharacteristics();
   }
   public SmallButton(Icon icon)
   {
      super(icon);
      setCharacteristics();
   }
   
   private void setCharacteristics()
   {
      Font font = getFont().deriveFont(10.0f);
      setFont(font);
      Insets margin = new Insets(0,2,0,2);
      setMargin(margin);
   }
}
