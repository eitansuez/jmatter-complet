/*
 * Created on Sep 3, 2003
 */
package com.u2d.ui;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

/**
 * @author Eitan Suez
 */
public class KeycapsDetector extends JLabel
{
	String capsOnMsg = "Key Caps On";
	ImageIcon icon = null;
	{
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		java.net.URL imgurl = loader.getResource("images/lock.png");
		icon = new ImageIcon(imgurl);
	}

	public KeycapsDetector(JComponent targetComponent)
	{
      setText(capsOnMsg);
      setIcon(icon);
		targetComponent.addKeyListener(new MyKeyAdapter());
		setBorder(BorderFactory.createEmptyBorder(2,8,2,8));
      setVisible(false);
	}
	
	public Dimension getPreferredSize()
	{
		return new Dimension(icon.getIconWidth(), icon.getIconHeight());
	}
	
	
	class MyKeyAdapter extends KeyAdapter
	{
		public void keyTyped(KeyEvent evt)
		{
			boolean capsOn = false;
			try
			{
				capsOn = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
			}
			catch (UnsupportedOperationException ex)
			{
				// return don't know!
				// apple does not support this!
				char keychar = evt.getKeyChar();
				capsOn = (Character.isUpperCase(keychar) && !evt.isShiftDown());
			}
         setVisible(capsOn);
		}
	}
}
