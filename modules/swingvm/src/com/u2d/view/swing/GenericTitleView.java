/*
 * Created on Oct 8, 2003
 */
package com.u2d.view.swing;

import java.awt.*;
import javax.swing.*;
import com.u2d.view.*;

/**
 * @author Eitan Suez
 */
public class GenericTitleView extends JLabel
{
   private static Font TITLE_FONT;
   static
   {
      TITLE_FONT = UIManager.getFont("Label.font").deriveFont(Font.BOLD, 16.0f);
   }
   
   /**
    * TODO:  create a titleview for listeobjects
    */
   public GenericTitleView(View view)
   {
      setHorizontalAlignment(JLabel.LEADING);
      setVerticalAlignment(JLabel.CENTER);
      setHorizontalTextPosition(JLabel.TRAILING);
      setVerticalTextPosition(JLabel.CENTER);
      setOpaque(false);
      
      // TODO:  assign fonts and colors from preferences
      setFont(TITLE_FONT);
      
      setText(view.getTitle());
      Icon icon = view.iconLg();
      if (icon != null)
         setIcon(icon);
   }
   
   private Insets _insets = new Insets(2, 5, 6, 8);
	public Insets getInsets() { return _insets; }
   
	public Dimension getMinimumSize()
	{
		return getPreferredSize();
	}
	public Dimension getMaximumSize()
	{
		return getPreferredSize();
	}
	public Dimension getPreferredSize()
	{
		Dimension d = super.getPreferredSize();
		d.width += getInsets().left + getInsets().right;
		d.height += getInsets().top + getInsets().bottom;
		return d;
	}
   
   protected void paintBorder(java.awt.Graphics g)
   {
      super.paintBorder(g);
      Graphics2D g2 = (Graphics2D) g;
      g2.setStroke(new BasicStroke(1f));
      int y = getLocation().y + getSize().height - 1;
      int x1 = getLocation().x;
      int x2 = x1 + getSize().width;
      g2.drawLine(x1, y, x2, y);
   }
   
}
