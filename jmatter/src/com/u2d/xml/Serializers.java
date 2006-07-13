package com.u2d.xml;

import com.u2d.model.ComplexType;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Sep 13, 2005
 * Time: 2:22:34 PM
 */
public class Serializers
{
   public static String serializeClass(Class cls)
   {
      return cls.getName();
   }

   public static Class deserializeClass(String clsName)
         throws Exception
   {
      return Class.forName(clsName);
   }

   public static String serializeComplexType(ComplexType type)
   {
      return type.getJavaClass().getName();
   }
   public static ComplexType deserializeComplexType(String clsName)
         throws Exception
   {
      Class cls = Class.forName(clsName);
      return ComplexType.forClass(cls);
   }
}
