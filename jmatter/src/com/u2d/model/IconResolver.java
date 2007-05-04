package com.u2d.model;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: May 4, 2007
 * Time: 11:07:28 AM
 */
public class IconResolver
{
   public static String stateIconRef(ComplexEObject eo, String size)
   {
      ComplexType type = eo.type();
      String path = type.name() + eo.getState().getName() + size;
      String resolvedPath = iconRef(path);
      
      if (resolvedPath == null)
      {
         return typeIconRef(type, size);
      }
      return resolvedPath;
   }
   public static String typeIconRef(ComplexType type, String size)
   {
      return icon(type, size, baseStrategy);
   }
   public static String pluralIconRef(ComplexType type, String size)
   {
      return icon(type, size, pluralStrategy);
   }
   
   private static String BASE = "images/";
   private static String iconRef(String iconPath)
   {
      return imgPath(BASE + iconPath);
   }
   private static String imgPath(String iconPath)
   {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      String path = iconPath + ".png";
      if (!hasResource(loader, path))
      {
         path = iconPath + ".gif";
         if (!hasResource(loader, path))
         {
            return null;
         }
      }
      return path;
   }
   private static boolean hasResource(ClassLoader loader, String path)
   {
      return (loader.getResource(path) != null);
   }
   
   private static String icon(ComplexType type, String size, NameStrategy namer)
   {
      String path = namer.name(type) + size;
      String icon = iconRef(path);
      
      while (icon == null)
      {
         type = type.superType();
         if (type == null)
            break;
         path = type.name() + size;
         icon = iconRef(path);
      }
      
      if (icon == null)
      {
         return namer.defaultIconRef(size);
      }
      return icon;
   }

   interface NameStrategy
   {
      public String name(ComplexType type);
      public String defaultIconRef(String size);
   }
   static class BaseStrategy implements NameStrategy
   {
      public String name(ComplexType type)
      {
         return type.name();
      }
      public String defaultIconRef(String size)
      {
         return ("32".equals(size)) ? DEFAULTICON_LG : DEFAULTICON_SM;
      }
   }
   static class PluralStrategy implements NameStrategy
   {
      public String name(ComplexType type)
      {
         return type.getPluralName();
      }
      public String defaultIconRef(String size)
      {
         return ("32".equals(size)) ? LISTICON_LG : LISTICON_SM;
      }
   }
   static NameStrategy baseStrategy = new BaseStrategy();
   static NameStrategy pluralStrategy = new PluralStrategy();
   
   public static String DEFAULTICON_SM = iconRef("Objects16");
   public static String DEFAULTICON_LG = iconRef("Objects32");
   public static String LISTICON_SM = iconRef("list16");
   public static String LISTICON_LG = iconRef("list32");
}