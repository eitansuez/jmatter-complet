/*
 * Created on Apr 27, 2004
 */
package com.u2d.type.atom;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.*;

/**
 * Basic behavior for logo icon assistant is to provide suitable
 * iconSm and iconLg derivatives from the logo.  No cropping
 * is done.  Aspect ratio is maintained.
 * 
 * @author Eitan Suez
 */
public class LogoIconAssistant
{
   private Icon _iconLg, _iconSm;
   
   public Icon iconLg(ImgEO logo, Icon defaultIcon)
   {
      if (logo == null || logo.isEmpty())
      {
         _iconSm = _iconLg = null;
         return defaultIcon;
      }
      if (_iconLg == null)
      {
         _iconLg = scale(logo, 32);
      }
      return _iconLg;
   }
   
   public Icon iconSm(ImgEO logo, Icon defaultIcon)
   {
      if (logo == null || logo.isEmpty())
      {
         _iconSm = _iconLg = null;
         return defaultIcon;
      }
      if (_iconSm == null)
      {
         _iconSm = scale(logo, 16);
      }
      return _iconSm;
   }
   
   public void update(ImgEO logo)
   {
      _iconLg = scale(logo, 32);
      _iconSm = scale(logo, 16);
   }
   
   public static Icon scale(ImgEO logo, int height)
   {
      ImageIcon icon = logo.imageValue();
      Image scaledImg = scale(icon, height);
      return new ImageIcon(scaledImg);
   }
      
   public static Image scale(ImageIcon icon, int height)
   {
      int h1 = icon.getIconHeight();
      int w1 = icon.getIconWidth();
      int width = (int)  ( (double) w1 / (double) h1 * (double) height );
      Image img = icon.getImage();
      return img.getScaledInstance(width, height, Image.SCALE_FAST);
   }
   
   public static Image scale(ImageIcon icon, int desiredHeight, boolean condition)
   {
      if (condition)
      {
         return scale(icon, desiredHeight);
      }
      return icon.getImage();
   }
   
}
