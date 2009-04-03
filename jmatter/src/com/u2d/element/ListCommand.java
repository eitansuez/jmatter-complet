package com.u2d.element;

import com.u2d.view.EView;
import com.u2d.view.View;
import com.u2d.model.ComplexType;
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
public class ListCommand extends EOCommand
{
   public ListCommand() { }
   public ListCommand(Method method, ComplexType parent, ParameterInfo[] params)
   {
      super(method, parent, params);
   }

   protected boolean haveParameters() { return _method.getParameterTypes().length > 2; }
   public Object getTarget(Object value) { return ((AbstractListEO) value).type(); }

   public void execute(Object value, EView source) throws InvocationTargetException
   {
      AbstractListEO list = (AbstractListEO) value;
      ComplexType target = list.type();
      CommandInfo cmdInfo = new CommandInfo(this, source);

      if (haveParameters())
      {
         vmech().displayParamsListView(this, value, cmdInfo);
      }
      else
      {
         Object[] param = new Object[] {cmdInfo, list};
         execute(target, param);
      }
   }

   public void execute(Object target, Object[] params) throws InvocationTargetException
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
      catch (IllegalArgumentException ex)
      {
         System.err.println("EOCommand:  Failed in attempt to invoke " +
               " method: " + _method + " on object: " + target);
         throw ex;
      }
   }

}
