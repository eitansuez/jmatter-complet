package com.u2d.element;

import com.u2d.view.EView;
import com.u2d.model.Typed;
import com.u2d.model.ComplexType;
import com.u2d.ui.desktop.Positioning;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Date: Jun 14, 2005
 * Time: 3:01:53 PM
 *
 * @author Eitan Suez
 */
public class OverloadedEOCmd extends EOCommand
{
   EOCommand _overloadedCmd, _secondCmd;

   public OverloadedEOCmd() {}
   
   public OverloadedEOCmd(Method method, ComplexType parent, char mnemonic,
                          ParameterInfo[] paramInfo, boolean sensitive, 
                          Positioning positioning, EOCommand secondCmd)
   {
      super(method, parent, mnemonic, paramInfo, sensitive, positioning);       
      _secondCmd = secondCmd;
      
      if (paramInfo().length > secondCmd.paramInfo().length)
      {
         _overloadedCmd = this;
      }
      else
      {
         _overloadedCmd = _secondCmd;
      }
   }

   public void execute(Object value, EView source) throws InvocationTargetException
   {
      Command cmd = (_overloadedCmd == this) ? _secondCmd : this;
      
      if (value instanceof ComplexType)
      {
         ComplexType type = (ComplexType) value;
         if (type.isAbstract() || type.hasConcreteSubTyptes())
         {
            cmd = _overloadedCmd;
         }
      }
      else if (value instanceof Typed)
      {
         ComplexType type = ((Typed) value).type();
         if (type.isAbstract() || type.hasConcreteSubTyptes())
         {
            cmd = _overloadedCmd;
         }
      }

      // to avoid recursion to self..
      if (cmd == this)
         super.execute(value, source);
      else
         cmd.execute(value, source);
   }

}
