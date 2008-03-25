package com.u2d.model;

import com.u2d.element.Command;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: May 4, 2007
 * Time: 11:07:28 AM
 */
public class IconResolver
{
   public static String cmdIconRef(Command command, String size)
   {
      if (command.hasIconref())
      {
         return iconRef(command.iconref() + size);
      }
      return stateIconRef(command, size);
   }

   public static String stateIconRef(ComplexEObject eo, String size)
   {
      ComplexType type = eo.type();
      String path = type.name() + eo.getState().getName() + size;
      String resolvedPath = iconRef(path);
      
      if (resolvedPath == null)
      {
         return instanceIconRef(type, size);
      }
      return resolvedPath;
   }
   public static String instanceIconRef(ComplexType type, String size) { return icon(type, size, baseStrategy); }
   public static String typeIconRef(ComplexType type, String size) { return icon(type, size, typeStrategy); }
   public static String pluralIconRef(ComplexType type, String size) { return icon(type, size, pluralStrategy); }

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

      ComplexType baseType = type;
      while (icon == null)
      {
         baseType = baseType.superType();
         if (baseType == null)
            break;
         
         path = namer.name(baseType) + size;
         icon = iconRef(path);
      }
      
      while (icon == null && namer.fallbackStrategy() != null)
      {
         namer = namer.fallbackStrategy();
         path = namer.name(type) + size;
         icon = iconRef(path);
      }
      return icon;
   }

   interface NameStrategy
   {
      public String name(ComplexType type);
      public NameStrategy fallbackStrategy();
   }
   static NameStrategy baseStrategy = new NameStrategy()
   {
      public String name(ComplexType type) { return type.name(); }
      public NameStrategy fallbackStrategy() { return defaultInstanceStrategy; }
   };
   static NameStrategy pluralStrategy = new NameStrategy()
   {
      public String name(ComplexType type) { return type.getPluralName(); }
      public NameStrategy fallbackStrategy() { return defaultPluralStrategy; }
   };
   static NameStrategy defaultInstanceStrategy = new NameStrategy()
   {
      public String name(ComplexType type) { return "Objects"; }
      public NameStrategy fallbackStrategy() { return null; }
   };
   static NameStrategy defaultPluralStrategy = new NameStrategy()
   {
      public String name(ComplexType type) { return "list"; }
      public NameStrategy fallbackStrategy() { return null; }
   };
   static NameStrategy typeStrategy = new NameStrategy()
   {
      public String name(ComplexType type) { return type.getPluralName(); }
      public NameStrategy fallbackStrategy() { return baseStrategy; }
   };

}