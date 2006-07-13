package com.u2d.element;

/**
 * Date: Jun 10, 2005
 * Time: 2:39:03 PM
 *
 * @author Eitan Suez
 */
public class ParameterInfo
{
   private Class paramType;
   private String caption;

   public ParameterInfo(Class paramType, String caption)
   {
      this.paramType = paramType;
      if (caption == null)
      {
         this.caption = paramType.getName();
      }
      else
      {
         this.caption = caption;
      }
   }
   public ParameterInfo(Class paramType)
   {
      this.paramType = paramType;
      this.caption = paramType.getName();
   }
   
   public String caption() { return caption; }
   public Class type() { return paramType; }
   
   public String toString() { return caption; }

}
