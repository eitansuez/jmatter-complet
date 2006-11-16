package com.u2d.model;

import javax.swing.*;
import java.net.URL;

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
      ComplexType type = eo.type();
      String path = type.name() + eo.getState().getName() + size;
      Icon icon = loadIcon(path);
      
      if (icon == null)
      {
         return typeIcon(type, size);
      }
      return icon;
   }
   public static Icon typeIcon(ComplexType type, String size)
   {
      return icon(type, size, baseStrategy);
   }
   public static Icon pluralIcon(ComplexType type, String size)
   {
      return icon(type, size, pluralStrategy);
   }
   
   private static Icon icon(ComplexType type, String size, NameStrategy namer)
   {
      String path = namer.name(type) + size;
      Icon icon = loadIcon(path);
      
      while (icon == null)
      {
         type = type.superType();
         if (type == null)
            break;
         path = type.name() + size;
         icon = loadIcon(path);
      }
      
      if (icon == null)
      {
         return namer.defaultIcon(size);
      }
      return icon;
   }
   interface NameStrategy
   {
      public String name(ComplexType type);
      public Icon defaultIcon(String size);
   }
   static class PluralStrategy implements NameStrategy
   {
      public String name(ComplexType type)
      {
         return type.getPluralName();
      }
      public Icon defaultIcon(String size)
      {
         return ("32".equals(size)) ? LISTICON_LG : LISTICON_SM;
      }
   }
   static class BaseStrategy implements NameStrategy
   {
      public String name(ComplexType type)
      {
         return type.name();
      }
      public Icon defaultIcon(String size)
      {
         return ("32".equals(size)) ? DEFAULTICON_LG : DEFAULTICON_SM;
      }
   }
   static NameStrategy baseStrategy = new BaseStrategy();
   static NameStrategy pluralStrategy = new PluralStrategy();

   public static URL imgURL(String iconPath)
   {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      java.net.URL imgURL = loader.getResource(iconPath+".png");
      if (imgURL == null)
         imgURL = loader.getResource(iconPath+".gif");
      return imgURL;
   }
   public static String BASE = "images/";
   public static Icon loadIcon(String iconPath)
   {
      URL imgURL = imgURL(BASE + iconPath);
      if (imgURL == null) return null;
      return new ImageIcon(imgURL);
   }
   

   public static Icon DEFAULTICON_SM = loadIcon("Objects16");
   public static Icon DEFAULTICON_LG = loadIcon("Objects32");
   public static Icon LISTICON_SM = loadIcon("list16");
   public static Icon LISTICON_LG = loadIcon("list32");

}
