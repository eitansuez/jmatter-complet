package com.u2d.reflection;

import com.u2d.element.EOCommand;
import com.u2d.element.ParameterInfo;
import com.u2d.model.ComplexType;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Feb 14, 2006
 * Time: 4:57:25 PM
 */
public class AnnotationsReflector implements Reflector
{
   public String commandName(Method method)
   {
      return method.getName();
   }

   public boolean isCommand(Method method)
   {
      return method.isAnnotationPresent(CommandAt.class);
   }
   
   private static transient Map<EOCommand, EOCommand> _commandCache = 
         new HashMap<EOCommand, EOCommand>();

   /*
    * this logic ensures that the same command object is returned
    * instead of multiple copies, albeit equal().
    */
   public EOCommand reflectCommand(Method method, Class klass, ComplexType parent)
   {
      CommandAt at = method.getAnnotation(CommandAt.class);
      EOCommand cmd = new EOCommand(method,
                                    parent,
                                    at.mnemonic(),
                                    parameterInfo(method),
                                    at.isSensitive());
      cmd.setPositioningHint(at.viewPosition());
      
      if (parent == null)  // hack for lists..(temporary)
         return cmd;
      
      if (!_commandCache.containsKey(cmd))
      {
         _commandCache.put(cmd, cmd);
      }
      return _commandCache.get(cmd);
   }

   private ParameterInfo[] parameterInfo(Method method)
   {
      int length = method.getParameterTypes().length;
      ParameterInfo[] paramInfo = new ParameterInfo[length];
      for (int i=1; i<length; i++)
      {
         if (method.getParameterAnnotations()[i].length > 0)
         {
            ParamAt pat = (ParamAt) method.getParameterAnnotations()[i][0];
            paramInfo[i] = new ParameterInfo(method.getParameterTypes()[i], pat.value());
         }
         else
         {
            paramInfo[i] = new ParameterInfo(method.getParameterTypes()[i]);
         }
      }
      return paramInfo;
   }

}
