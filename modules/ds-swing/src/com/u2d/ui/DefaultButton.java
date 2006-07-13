/*
 * Created on Oct 9, 2003
 */
package com.u2d.ui;

import javax.swing.*;
import java.awt.*;

/**
 * @author Eitan Suez
 */
public class DefaultButton extends NormalButton
{
   private static Font BOLD = UIManager.getFont("Button.font").deriveFont(Font.BOLD);
   
	public DefaultButton()
	{
		super();
      setCharacteristics();
	}
	public DefaultButton(String caption)
	{
      super(caption);
      setCharacteristics();
   }
   public DefaultButton(javax.swing.Action action)
   {
      super(action);
      setCharacteristics();
   }
   
   private void setCharacteristics()
   {
      setOpaque(false);
      setFont(BOLD);
   }
	
	public void addNotify()
	{
		super.addNotify();
		JRootPane rootpane = SwingUtilities.getRootPane(this);
		if (rootpane != null)
			rootpane.setDefaultButton(this);
	}
}
