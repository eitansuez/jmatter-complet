/*
 * Created on Sep 8, 2003
 */
package com.u2d.ui;

import java.awt.*;
import javax.swing.*;

/**
 * @author Eitan Suez
 */
public class Palette extends JInternalFrame
{
   private JPanel _contentPane;
   
	public Palette(JDesktopPane container)
	{
		super("Palette");
		container.add(this, JLayeredPane.PALETTE_LAYER);
		init(container);
	}
	
	private void init(JDesktopPane container)
	{
		setIconifiable(true);
		setResizable(true);
		putClientProperty("JInternalFrame.isPalette", Boolean.TRUE);
		setLocation(10,10);
		
		_contentPane = (JPanel) getContentPane();
		
		JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(container);
		setFrameIcon(new ImageIcon(topFrame.getIconImage()));
	}

	public void setBody(JComponent item)
	{
		_contentPane.add(item, BorderLayout.CENTER);
	}
	
	public Dimension getMinimumSize()
	{
		//return new Dimension(60, getPreferredSize().height);
		return new Dimension(32, 32);  // needs work
	}
	
}
