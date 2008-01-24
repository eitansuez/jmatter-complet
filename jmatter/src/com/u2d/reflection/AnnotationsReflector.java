package com.u2d.reflection;

import com.u2d.element.EOCommand;
import com.u2d.element.ParameterInfo;
import com.u2d.element.ListCommand;
import com.u2d.model.ComplexType;
import com.u2d.model.IconLoader;
import com.u2d.model.IconResolver;
import com.u2d.type.atom.StringEO;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

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
      return method.isAnnotationPresent(Cmd.class);
   }
   public boolean isListCommand(Method method)
   {
      return method.isAnnotationPresent(ListCmd.class);
   }
   
   private static transient Map<EOCommand, EOCommand> _commandCache = 
         new HashMap<EOCommand, EOCommand>();

   /*
    * this logic ensures that the same command object is returned
    * instead of multiple copies, albeit equal().
    */
   public EOCommand reflectCommand(Method method, Class klass, ComplexType parent)
   {
      Cmd at = method.getAnnotation(Cmd.class);
      EOCommand cmd = new EOCommand(method,
                                    parent,
                                    at.mnemonic(),
                                    parameterInfo(method),
                                    at.sensitive(),
                                    at.viewPosition());
      cmd.blocks(at.blocks());
      cmd.batchable(at.batchable());
      
      if (!StringEO.isEmpty(at.label()))
         cmd.getLabel().setValue(at.label());
         
      if (!StringEO.isEmpty(at.description()))
         cmd.getDescription().setValue(at.description());
      
      if (!StringEO.isEmpty(at.iconref()))
         cmd.iconref(at.iconref());

      if (!StringEO.isEmpty(at.shortcut()))
        cmd.getShortcut().setValue(at.shortcut());
      
      if (parent == null)  // hack for lists..(temporary)
         return cmd;
      
      synchronized(this)
      {
         if (!_commandCache.containsKey(cmd))
         {
            _commandCache.put(cmd, cmd);
         }
         return _commandCache.get(cmd);
      }
   }
   public EOCommand reflectListCommand(Method method, ComplexType parent)
   {
      return new ListCommand(method, parent, parameterInfo(method));
   }

   private ParameterInfo[] parameterInfo(Method method)
   {
      int length = method.getParameterTypes().length;
      List<ParameterInfo> paramInfo = new ArrayList<ParameterInfo>(length -1);
      int startIndex = 1; // skip commandInfo argument
      
      if (isListCommand(method)) // second argument is list to operate on
      {
         startIndex = 2;
      }
         
      for (int i=startIndex; i<length; i++)
      {
         if (method.getParameterAnnotations()[i].length > 0)
         {
            Arg pat = (Arg) method.getParameterAnnotations()[i][0];
            paramInfo.add(new ParameterInfo(method.getParameterTypes()[i], pat.value()));
         }
         else
         {
            paramInfo.add(new ParameterInfo(method.getParameterTypes()[i]));
         }
      }
      return paramInfo.toArray(new ParameterInfo[paramInfo.size()]);
   }

}
