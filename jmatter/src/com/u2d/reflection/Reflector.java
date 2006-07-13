package com.u2d.reflection;

import com.u2d.element.EOCommand;
import com.u2d.model.ComplexType;

import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Feb 14, 2006
 * Time: 4:55:24 PM
 */
public interface Reflector
{
   public boolean isCommand(Method method);
   
   public EOCommand reflectCommand(Method method,
                                   Class klass,
                                   ComplexType parent);
   
   public String commandName(Method method);
   
}
