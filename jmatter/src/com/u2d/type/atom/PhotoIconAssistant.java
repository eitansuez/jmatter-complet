/*
 * Created on Apr 27, 2004
 */
package com.u2d.type.atom;

import com.u2d.model.ComplexEObject;
import com.u2d.model.IconLoader;
import com.u2d.model.ComplexType;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.*;
import java.awt.image.*;

/**
 * @author Eitan Suez
 */
public class PhotoIconAssistant
{
   private Icon _iconLg, _iconSm;
   private ImgEO _photo;
   private ComplexEObject _parent;
   
   public PhotoIconAssistant(ComplexEObject parent, ImgEO photo)
   {
      _parent = parent;
      _photo = photo;
      
      photo.addChangeListener(new javax.swing.event.ChangeListener()
            {
               // when photo updates, icons need to update too..
               public void stateChanged(javax.swing.event.ChangeEvent evt)
               {
                  update();
                  _parent.firePropertyChange("icon", null, null);
               }
            });
   }
   
   public Icon iconLg()
   {
      if (_photo == null || _photo.isEmpty())
      {
         _iconSm = _iconLg = null;
         // ideally need to do this:  return _parent.super.iconLg();
         return IconLoader.instanceIcon(_parent, "32", ComplexType.DEFAULTICON_LG);
      }
      if (_iconLg == null)
      {
         _iconLg = scale(_photo, 32, 32);
      }
      return _iconLg;
   }
   
   public Icon iconSm()
   {
      if (_photo == null || _photo.isEmpty())
      {
         _iconSm = _iconLg = null;
         // ideally need to do this:  return _parent.super.iconLg();
         return IconLoader.instanceIcon(_parent, "16", ComplexType.DEFAULTICON_LG);
      }
      if (_iconSm == null)
      {
         _iconSm = scale(_photo, 16, 16);
      }
      return _iconSm;
   }
   
   private void update()
   {
      _iconLg = scale(_photo, 32, 32);
      _iconSm = scale(_photo, 16, 16);
   }
   
   public static Icon scale(ImgEO photo, int w, int h)
   {
      ImageIcon icon = photo.imageValue();
      Image scaledImg = scale(icon, w, h);
      return new ImageIcon(scaledImg);
   }
      
   public static Image scale(ImageIcon icon, int w, int h)
   {
      int h1 = icon.getIconHeight();
      int w1 = icon.getIconWidth();
      double ratio = (double) h1 / w1;
      double desiredRatio = (double) h / w;
      Image img = icon.getImage();
      if (ratio > desiredRatio)  // trim height
      {
         int h2 = (int) (w1 * desiredRatio);
         int y = ( h1 - h2 ) / 2;
         img = crop(img, 0, y, w1, h2);
      }
      else if (ratio < desiredRatio)  // trim width
      {
         int w2 = (int) ( h1 / desiredRatio ) ;
         int x = ( w1 - w2 ) / 2;
         img = crop(img, x, 0, w2, h1);
      }
      
      return img.getScaledInstance(w, h, Image.SCALE_FAST);
   }
   
   private static Image crop(Image img, int x, int y, int w, int h)
   {
      ImageFilter filter = new CropImageFilter(x, y, w, h);
      FilteredImageSource source = new FilteredImageSource(img.getSource(), filter);
      return Toolkit.getDefaultToolkit().createImage(source);
   }
   
}
