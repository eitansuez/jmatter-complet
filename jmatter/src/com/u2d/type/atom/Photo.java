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
public class Photo extends ImgEO
{
   public Photo() { super(); }
   public Photo(ImageIcon value) { super(value); }

   public String nullIconResourcePath() { return "images/photo_portrait_32.png"; }
   public String emptyCaption() { return "No photo"; }
   public Image processRawIcon(ImageIcon icon)
   {
      return PhotoIconAssistant.scale(icon, 128, 128);
   }

   public EObject makeCopy() { return new Photo(imageValue()); }
}
