package com.u2d.reflection;

import com.u2d.element.EOCommand;
import com.u2d.element.ParameterInfo;
import com.u2d.model.ComplexType;
import com.u2d.model.ComplexEObject;
import com.u2d.ui.desktop.Positioning;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Feb 14, 2006
 * Time: 4:57:08 PM
 */
public class ClassicReflector implements Reflector
{
   public String commandName(Method method)
   {
      return method.getName().substring("command".length());
   }

   public boolean isCommand(Method method)
   {
      String methodName = method.getName();
      return methodName.startsWith("command") &&
             methodName.length() > 7 &&
             Character.isUpperCase(methodName.charAt(7)) &&
             ! Modifier.isAbstract(method.getModifiers());
   }

   public EOCommand reflectCommand(Method method, Class klass, ComplexType parent)
   {
      char mnemonic = getMnemonic(method, klass);
      ParameterInfo[] paramInfo = harvestParameters(method, klass);
      boolean isSensitive = isSensitive(method, klass);
      EOCommand cmd = new EOCommand(method, parent, mnemonic, paramInfo, isSensitive);
      Positioning positioningHint = positioningHint(method, klass);
      cmd.setPositioningHint(positioningHint);
      return cmd;
   }

   // e.g.:
   //    public static char commandEditMnemonic = 'e';
   private char getMnemonic(Method method, Class clazz)
   {
      // note:  clazz is possibly inner state class
      try
      {
         String fieldName = method.getName()+"Mnemonic";
         java.lang.reflect.Field field = clazz.getField(fieldName);
         return ((Character) field.get(null)).charValue();
      }
      catch (Exception ex)
      {
         // important note:  fallback to declaring (containing) class (check in two places)
         clazz = clazz.getDeclaringClass();
         if (clazz != null)
            return getMnemonic(method, clazz);
      }
      return '\0';
   }

   private boolean isSensitive(Method method, Class klass)
   {
      try
      {
         String fieldName = method.getName()+"Sensitive";
         java.lang.reflect.Field field = klass.getField(fieldName);
         return ((Boolean) field.get(null)).booleanValue();
      }
      catch (Exception ex)
      {
         // important note:  fallback to declaring (containing) class (check in two places)
         klass = klass.getDeclaringClass();
         if (klass != null)
            return isSensitive(method, klass);
      }
      return false;
   }

   private Positioning positioningHint(Method method, Class klass)
   {
      try
      {
         String fieldName = method.getName()+"ViewPosition";
         java.lang.reflect.Field field = klass.getField(fieldName);
         return (Positioning) field.get(null);
      }
      catch (Exception ex)
      {
         klass = klass.getDeclaringClass();
         if (klass != null)
            return positioningHint(method, klass);
      }
      return Positioning.NEARMOUSE;
   }

   private ParameterInfo[] harvestParameters(Method method, Class klass)
   {
      ParameterInfo[] params =
            new ParameterInfo[method.getParameterTypes().length];
      String caption = null;

      try
      {
         for (int i=1; i<method.getParameterTypes().length; i++)
         {
            String fieldName = method.getName()+"Parameter"+i+"Caption";
            java.lang.reflect.Field field = klass.getField(fieldName);
            caption = (String) field.get(null);
            params[i] = new ParameterInfo(method.getParameterTypes()[i], caption);
         }
         return params;
      }
      catch (Exception ex)
      {
         klass = klass.getDeclaringClass();
         if (klass == null)
         {
            for (int i=1; i<method.getParameterTypes().length; i++)
            {
               Class cls = method.getParameterTypes()[i];
               if (ComplexEObject.class.isAssignableFrom(cls))
               {
                  caption = ComplexType.forClass(cls).getNaturalName() + ": ";
               }
               else
               {
                  caption = "Parameter " + i + ": ";
               }
               params[i] = new ParameterInfo(cls, caption);
            }
            return params;
         }
         else
         {
            return harvestParameters(method, klass);
         }
      }
   }

}
