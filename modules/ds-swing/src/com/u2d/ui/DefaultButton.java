/*
 * Created on Oct 9, 2003
 */
package com.u2d.ui;

import com.u2d.css4swing.style.ComponentStyle;
import javax.swing.*;

/**
 * @author Eitan Suez
 */
public class DefaultButton extends NormalButton
{
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
      ComponentStyle.addClass(this, "default-button");
   }
	
	public void addNotify()
	{
		super.addNotify();
		JRootPane rootpane = SwingUtilities.getRootPane(this);
		if (rootpane != null)
			rootpane.setDefaultButton(this);
	}
}
