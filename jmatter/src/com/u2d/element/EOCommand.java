/*
 * Created on Jan 20, 2004
 */
package com.u2d.element;

import java.lang.reflect.*;
import com.u2d.model.*;
import com.u2d.pattern.*;
import com.u2d.ui.desktop.Positioning;
import com.u2d.view.*;

/**
 * @author Eitan Suez
 */
public class EOCommand extends Command
{
   protected Method _method;
   protected ParameterInfo[] _params;
   
   public EOCommand() {}

   public EOCommand(Method method, ComplexType parent)
   {
      _method = method;
      String name = ComplexType.reflector().commandName(method);
      init(parent, name);
   }

   public EOCommand(Method method, ComplexType parent, char mnemonic)
   {
      this(method, parent);
      _mnemonic.setValue(mnemonic);
   }
   
   public EOCommand(Method method, ComplexType parent, ParameterInfo[] params)
   {
      this(method, parent);
      _params = params;
   }

   public EOCommand(Method method, ComplexType parent,
                    char mnemonic, ParameterInfo[] params)
   {
      this(method, parent, mnemonic);
      _params = params;
   }

   public EOCommand(Method method, ComplexType parent,
                    char mnemonic, ParameterInfo[] params, boolean sensitive)
   {
      this(method, parent, mnemonic, params);
      _sensitive.setValue(sensitive);
   }
   
   public EOCommand(Method method, ComplexType parent,
                    char mnemonic, ParameterInfo[] params, boolean sensitive,
                    Positioning viewPosition)
   {
      this(method, parent, mnemonic, params, sensitive);
      setPositioningHint(viewPosition);
   }

   protected Positioning _positioningHint = Positioning.NEARMOUSE;
   public Positioning getPositioningHint() { return _positioningHint; }
   public void setPositioningHint(Positioning hint) { _positioningHint = hint; }


   protected String calcPath()
   {
      String fullPath = super.calcPath();
      if (State.class.isAssignableFrom(_method.getDeclaringClass()))
      {
         return fullPath + "#" + _method.getDeclaringClass().getName();
      }
      else
      {
         return fullPath;
      }
   }

   public void execute(Object value, EView source) throws InvocationTargetException
   {
      Object target = getTarget(value);

      CommandInfo cmdInfo = new CommandInfo(this, source);

      if (haveParameters())
      {
         View paramsView = vmech().getParamListView(this, value, cmdInfo);
         vmech().displayView(paramsView, Positioning.NEARMOUSE);
      }
      else
      {
         Object[] param = new Object[] {cmdInfo};
         execute(target, param);
      }
   }
   
   protected boolean haveParameters()
   {
      return _method.getParameterTypes().length > 1;
   }

   public void execute(Object value, Object[] params) throws InvocationTargetException
   {
      Object target = getTarget(value);
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

   // determines whether the target object is the object itself or a state inner class
   public Object getTarget(Object value)
   {
      if (value instanceof ComplexEObject)
      {
         ComplexEObject ceo = (ComplexEObject) value;
         if (State.class.isAssignableFrom(_method.getDeclaringClass()))
            return ceo.getState();
      }
      else if (value instanceof AbstractListEO)
      {
         // list types sometimes are delegates for their item types' commands..
         AbstractListEO leo = (AbstractListEO) value;
         if (AbstractListEO.class.isAssignableFrom(_method.getDeclaringClass()))
            return leo;
         else
            return leo.type();
      }
      
      // otherwise..
      return value;
   }

   public ParameterInfo[] paramInfo() { return _params; }


   public String localizedLabel(Localized l)
   {
      Class cls = _method.getDeclaringClass();
      Class declaringClass = cls.getDeclaringClass();
      if (declaringClass == null)
      {
         declaringClass = cls;
      }
      String key = ComplexType.shortName(declaringClass) + "." + name();
      String value = l.localeLookup(key);
      if (value == null)
      {
         return super.localizedLabel(l);
      }
      return value;
   }


   public String qualifiedName()
   {
      return _parent.name() + "." + _name;
   }

   public EOCommand copy()
   {
      return new EOCommand(_method, (ComplexType) _parent, 
                           _mnemonic.charValue(), _params, sensitive());
   }

   public OverloadedEOCmd overload(EOCommand secondCmd)
   {
      OverloadedEOCmd ocmd = new OverloadedEOCmd(_method, (ComplexType) _parent, _mnemonic.charValue(),
            _params, sensitive(), _positioningHint, secondCmd);
      ocmd.iconref(iconref());
      return ocmd;
   }
   
//   public boolean overrides(Command cmd)
//   {
//      if (!cmd.name().equals(name()))
//         return false;
//      
//      if (!(cmd instanceof EOCommand))
//         return false;
//      
//      EOCommand eocmd = (EOCommand) cmd;
//      
//      if (_params.length != eocmd.paramInfo().length)
//         return false;
//      
//      ParameterInfo info = null, cmdinfo = null;
//      for (int i=0; i<_params.length; i++)
//      {
//         info = _params[i];
//         cmdinfo = eocmd.paramInfo()[i];
//         if (!info.type().equals(cmdinfo.type()))
//         {
//            return false;
//         }
//      }
//      
//      return true;
//   }


   // need a way to distinguish the commands in an overloaded command
   // e.g. ComplexType.New has two versions..
   // specifically, AnnotationsReflector.reflectCommand() invokes:
   //  _commandCache.containsKey(cmd) which must return false in the case
   //  over two commands with the same name, same full path.
   public boolean equals(Object obj)
   {
      if (!super.equals(obj))
         return false;
      if (!(obj instanceof EOCommand))
         return false;
      EOCommand cmd = (EOCommand) obj;
      return _params.length == cmd.paramInfo().length;
   }
}
