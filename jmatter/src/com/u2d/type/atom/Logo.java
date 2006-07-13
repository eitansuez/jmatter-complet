/*
 * Created on Jan 30, 2004
 */
package com.u2d.type.atom;

import com.u2d.model.EObject;
import javax.swing.ImageIcon;
import java.awt.*;

/**
 * @author Eitan Suez
 */
public class Logo extends ImgEO
{
   public Logo() { super(); }
   public Logo(ImageIcon value) { super(value); }

   public Image processRawIcon(ImageIcon icon)
   {
      boolean condition = icon.getIconHeight() > 64;
      return LogoIconAssistant.scale(icon, 64, condition);
   }

   public EObject makeCopy() { return new Logo(imageValue()); }
}
