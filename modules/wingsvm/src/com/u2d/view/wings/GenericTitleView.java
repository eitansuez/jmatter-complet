package com.u2d.view.wings;

import com.u2d.view.View;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.wings.SLabel;
import org.wings.SFont;
import org.wings.SImageIcon;
import org.wings.SConstants;

/**
 * @author Eitan Suez
 */
public class GenericTitleView
      extends SLabel
{
   private static SFont TITLE_FONT;
   static
   {
      TITLE_FONT = new SFont();
      TITLE_FONT.setStyle(SFont.BOLD);
      TITLE_FONT.setSize(16);
   }
   
   public GenericTitleView(View view)
   {
      setHorizontalAlignment(SConstants.LEFT);
      setVerticalAlignment(SConstants.CENTER);
      setHorizontalTextPosition(SConstants.RIGHT);
      setVerticalTextPosition(SConstants.CENTER);
      
      // TODO:  assign fonts and colors from preferences
      setFont(TITLE_FONT);
      
      setText(view.getTitle());
      Icon icon = view.iconLg();
      if (icon != null)
         setIcon(new SImageIcon((ImageIcon) icon));
      
      setStyle("border-bottom: thin solid black;");
   }
   
}
