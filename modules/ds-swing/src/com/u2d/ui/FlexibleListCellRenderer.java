/*
 * Created on Oct 13, 2003
 */
package com.u2d.ui;

import java.awt.*;
import javax.swing.*;

/**
 * Not tied to JList but a more generic lister component that allows different layout managers
 * 
 * @author Eitan Suez
 */
public interface FlexibleListCellRenderer
{
	public Component getListCellRendererComponent(JComponent c, Object value, int index,
	  boolean isSelected, boolean hasFocus);
}
