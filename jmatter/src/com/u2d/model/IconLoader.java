package com.u2d.model;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Nov 23, 2005
 * Time: 7:43:32 PM
 */
public class IconLoader
{
   public static String BASE = "images/";
   
   public static Icon instanceIcon(ComplexEObject eo, String size, Icon defaultIcon)
   {
      String path = BASE + eo.type().name() + eo.getState().getName() + size;
      return loadIcon(path, icon(eo.type(), size, defaultIcon));
   }

   public static Icon icon(ComplexType type, String size, Icon defaultIcon)
   {
      String iconPath = BASE + type.name() + size;
      return loadIcon(iconPath, defaultIcon);
   }
   public static Icon icons(ComplexType type, String size, Icon defaultIcon)
   {
      String iconPath = BASE + type.getPluralName() + size;
      return loadIcon(iconPath, defaultIcon);
   }

   public static Icon loadIcon(String iconPath, Icon defaultIcon)
   {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      java.net.URL imgURL = loader.getResource(iconPath+".png");
      if (imgURL == null)
         imgURL = loader.getResource(iconPath+".gif");

      if (imgURL == null)
      {
//         System.err.println("Unable to locate "+iconPath);
         return defaultIcon;
      }
      return new ImageIcon(imgURL);
   }

}
