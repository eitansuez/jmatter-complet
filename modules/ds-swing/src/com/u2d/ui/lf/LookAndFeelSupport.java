/*
 * Created on Sep 6, 2003
 */
package com.u2d.ui.lf;

import javax.swing.JMenu;

/**
 * @author Eitan Suez
 */
public interface LookAndFeelSupport extends LFChangeNotifier
{
	public JMenu getMenu();
	public String getCurrentLFName();
	public void setLF(String lfname);
   public boolean hasLF(String lfname);
}
