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
   private Method _method;
   private ParameterInfo[] _params;

   public EOCommand(Method method, ComplexType parent)
   {
      _method = method;
      String name = ComplexType.reflector().commandName(method);
      getName().setValue(name);
      _parent = parent;

//      setState(_readState, true);
   }

   public EOCommand(Method method, ComplexType parent, char mnemonic)
   {
      this(method, parent);
      setMnemonic(mnemonic);
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
      setSensitive(sensitive);
   }

   private Positioning _positioningHint = Positioning.NEARMOUSE;
   public Positioning getPositioningHint() { return _positioningHint; }
   public void setPositioningHint(Positioning hint) { _positioningHint = hint; }


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
   
   private boolean haveParameters()
   {
      return _method.getParameterTypes().length > 1;
   }

   public void execute(Object value, Object[] params) throws InvocationTargetException
   {
      Object target = getTarget(value);
      try
      {
//         System.out.println("target is: "+target+"; invoking "+this);
         Object result = _method.invoke(target, params);
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
               " method: " + _method.getName() + " on object: " + target);
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


   public Title title()
   {
//      return ((ComplexType) _parent).title().append(" ", _name);
      return new Title(label());
   }
   
   public String qualifiedName()
   {
      return _parent.name() + "." + _name;
   }

   public EOCommand copy()
   {
      return new EOCommand(_method, (ComplexType) _parent, _mnemonic, _params, _sensitive);
   }

   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (obj == this) return true;
      if (!(obj instanceof EOCommand)) return false;
      EOCommand cmd = (EOCommand) obj;
      return _method.equals(cmd._method) && _parent.equals(cmd._parent);
   }

   public int hashCode()
   {
      return _method.hashCode() + 31*_parent.hashCode();
   }

   public String getFullPath()
   {
      return _parent.getJavaClass().getName() + "#" + _name;
   }
   public static EOCommand forPath(String path)
   {
      if (path == null) return null;

      try
      {
         String[] parts = path.split("#");  // split on fullpath's # separator
         Class cls = Class.forName(parts[0]);
         ComplexType type = ComplexType.forClass(cls);
         String commandName = parts[1];
         
         return (EOCommand) type.findCommand(commandName);
      }
      catch (ClassNotFoundException ex)
      {
         System.err.println("ClassNotFoundException: "+ex.getMessage());
         ex.printStackTrace();
      }
      return null;
   }
}
