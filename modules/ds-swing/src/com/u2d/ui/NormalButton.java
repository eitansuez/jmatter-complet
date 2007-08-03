/*
 * Created on Oct 9, 2003
 */
package com.u2d.ui;

import org.jdesktop.swingx.JXButton;

import javax.swing.*;
import java.awt.*;

/**
 * @author Eitan Suez
 */
public class NormalButton extends JXButton
{
	public NormalButton()
	{
		super();
      setCharacteristics();
   }
	public NormalButton(String caption)
	{
		super(caption);
      setCharacteristics();
   }
   public NormalButton(javax.swing.Action action)
   {
      super(action);
      setCharacteristics();
   }
   
   private void setCharacteristics()
   {
      setOpaque(false);
      Insets margin = new Insets(0,6,0,6);
      setMargin(margin);
   }
}
