package com.u2d.element;

import com.u2d.view.EView;
import com.u2d.view.View;
import com.u2d.model.ComplexType;
import com.u2d.model.ComplexEObject;
import com.u2d.model.AbstractListEO;
import com.u2d.ui.desktop.Positioning;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jan 18, 2008
 * Time: 9:56:12 PM
 */
public class TypeCommand extends EOCommand
{
   public TypeCommand() { }

   public TypeCommand(Method method, ComplexType parent, char mnemonic, ParameterInfo[] params, boolean sensitive, Positioning viewPosition)
   {
      super(method, parent, mnemonic, params, sensitive, viewPosition);
   }

   protected boolean haveParameters() { return _method.getParameterTypes().length > 2; }
   public Object getTarget(Object value)
   {
      // list types sometimes are delegates for their item types' commands..
      if (value instanceof AbstractListEO)
      {
         AbstractListEO leo = (AbstractListEO) value;
         if (AbstractListEO.class.isAssignableFrom(_method.getDeclaringClass()))
            return leo;
         else
            return leo.type();
      }

      // otherwise..
      return value;
   }

   public void execute(Object value, EView source) throws InvocationTargetException
   {
      Object targetType = getTarget(value);
      CommandInfo cmdInfo = new CommandInfo(this, source);

      if (haveParameters())
      {
         View paramsView = vmech().getParamListView(this, value, cmdInfo);
         vmech().displayView(paramsView, Positioning.NEARMOUSE);
      }
      else
      {
         try
         {
            Object[] param = new Object[] {cmdInfo, targetType};
            execute(targetType, param);
         }
         catch (IllegalArgumentException ex)
         {
            Object[] param = new Object[] {cmdInfo};  // fallback for backward compatibility, and
                                                      // second parameter is designed to be optional
            execute(targetType, param);
         }
      }
   }

   public void execute(Object target, Object[] params) throws InvocationTargetException, IllegalArgumentException
   {
      try
      {
         tracer().fine("target is: "+target+"; invoking "+this);
         Object result = _method.invoke(target, params);

         if (_callback != null)
         {
            _callback.call(result);
         }

         EView source = ((CommandInfo) params[0]).getSource();
         vmech().displayViewFor(result, source, _positioningHint);
      }
      catch (IllegalAccessException ex)
      {
         System.err.println(ex.getMessage());
         ex.printStackTrace();
      }
   }

}