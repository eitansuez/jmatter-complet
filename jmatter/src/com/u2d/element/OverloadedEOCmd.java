package com.u2d.element;

import com.u2d.view.EView;
import com.u2d.model.Typed;
import com.u2d.model.ComplexType;
import com.u2d.model.Localized;
import com.u2d.model.FieldParent;
import com.u2d.type.atom.StringEO;
import com.u2d.ui.desktop.Positioning;

import javax.swing.*;
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
      Command cmd = this;
      
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

      if (cmd == this)
      {
         super.execute(value, source);
      }
      else
      {
         cmd.execute(value, source);
      }
   }

}
