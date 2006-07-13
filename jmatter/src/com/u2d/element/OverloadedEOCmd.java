package com.u2d.element;

import com.u2d.view.EView;
import com.u2d.model.Typed;
import com.u2d.model.ComplexType;
import java.lang.reflect.InvocationTargetException;

/**
 * Date: Jun 14, 2005
 * Time: 3:01:53 PM
 *
 * @author Eitan Suez
 */
public class OverloadedEOCmd extends Command
{
   EOCommand _cmd, _overloadedCmd;

   public OverloadedEOCmd(EOCommand first, EOCommand second)
   {
      if (first.paramInfo().length > second.paramInfo().length)
      {
         _cmd = second; _overloadedCmd = first;
      }
      else
      {
         _cmd = first;  _overloadedCmd = second;
      }

      _name.setValue(_cmd.getName());
   }

   public void execute(Object value, EView source) throws InvocationTargetException
   {
      EOCommand cmd = _cmd;

      if (value instanceof ComplexType)
      {
         ComplexType type = (ComplexType) value;
         if (type.isAbstract())
            cmd = _overloadedCmd;
      }
      else if (value instanceof Typed)
      {
         if (((Typed) value).type().isAbstract())
            cmd = _overloadedCmd;
      }

      cmd.execute(value, source);
   }

}
