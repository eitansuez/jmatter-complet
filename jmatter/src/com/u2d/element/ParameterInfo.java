package com.u2d.element;

import com.u2d.model.ComplexType;

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
         if (paramType.equals(ComplexType.class))
         {
            ComplexType ct = ComplexType.forClass(paramType);
            this.caption = ct.getNaturalName() + " Type: ";
         }
         else
         {
            this.caption = paramType.getName();
            // not sure whether the above is the best default candidate for the caption;
            // could possibly be:  "Type: "
         }
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
