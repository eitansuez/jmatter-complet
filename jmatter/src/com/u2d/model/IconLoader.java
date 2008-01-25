package com.u2d.model;

import com.u2d.element.Command;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Nov 23, 2005
 * Time: 7:43:32 PM
 */
public class IconLoader
{
   public static Icon stateIcon(ComplexEObject eo, String size)
   {
      return loadIcon(IconResolver.stateIconRef(eo, size));
   }
   public static Icon typeIcon(ComplexType type, String size)
   {
      return loadIcon(IconResolver.typeIconRef(type, size));
   }
   public static Icon pluralIcon(ComplexType type, String size)
   {
      return loadIcon(IconResolver.pluralIconRef(type, size));
   }
   
   public static Icon loadIcon(String resourcePath)
   {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      java.net.URL imgUrl = loader.getResource(resourcePath);
      if (imgUrl == null) return null;
      return new ImageIcon(imgUrl);
   }
}