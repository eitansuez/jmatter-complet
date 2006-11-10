/*
 * Created on Jan 19, 2004
 */
package com.u2d.element;

import com.u2d.restrict.CommandRestriction;
import com.u2d.restrict.Restriction;
import com.u2d.view.*;
import com.u2d.app.User;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.model.Localized;
import com.u2d.model.ComplexType;
import com.u2d.persist.type.CommandUserType;
import java.util.Arrays;

/**
 * @author Eitan Suez
 */
public abstract class Command extends Member 
{
   public abstract void execute(Object value, EView source)
            throws java.lang.reflect.InvocationTargetException;


   /**
    * A sensitive command is one that should be guarded against
    * inadvertant invocation.  Its cost is high and undoing is 
    * difficult.  A ui is advised to make it difficult for an 
    * end user to inadvertantly invoke the command.
    */
   protected boolean _sensitive;
   public boolean isSensitive() { return _sensitive; }
   public void setSensitive(boolean sensitive) { _sensitive = sensitive; }


   public String localizedLabel(Localized l)
   {
      String key = "Command." + name();
      return l.localeLookup(key);
   }

   /* authorization-related */

   protected CommandRestriction _restriction = null;

   public void applyRestriction(Restriction restriction)
   {
      if ((restriction != null) && !(restriction instanceof CommandRestriction))
         throw new IllegalArgumentException("Restriction must be a command restriction");

      _restriction = (CommandRestriction) restriction;
   }
   public CommandRestriction getRestriction() { return _restriction; }
   public void setRestriction(CommandRestriction restriction)
   {
      Restriction oldValue = _restriction;
      applyRestriction(restriction);
      firePropertyChange("restriction", oldValue, restriction);
   }
   
   public void liftRestriction() { _restriction = null; }
   
   public boolean isForbidden(EObject target)
   {
      tracer().fine("Checking if command "+this+" is forbidden.."+
         "(restriction is: "+_restriction+")");
      return (_restriction != null) && (_restriction.forbidden(target));
   }

   
   
   private Field _ownerField = null;
   public void setOwner(Field ownerField)
   {
      _ownerField = ownerField;
   }

   public User getOwner(ComplexEObject parent)
   {
      if (_ownerField == null) return null;
      return (User) _ownerField.get(parent);
   }


   public static com.u2d.pattern.SimpleFinder finder(String commandName)
   {
      return new NameFinder(commandName);
   }

   static class NameFinder implements com.u2d.pattern.SimpleFinder
   {
      String _cmdName = null;
      NameFinder(String cmdName)
      {
         _cmdName = cmdName;
      }
      public boolean found(Object candidate)
      {
         if (!(candidate instanceof Command))
            throw new IllegalArgumentException("Illegal Argument Type for CommandFinder");

         Command cmd = (Command) candidate;
         return cmd.name().equals(_cmdName);
      }
   }



   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (!(obj instanceof Command)) return false;
      if (obj == this) return true;
      Command cmd = (Command) obj;
      if (_parent == null) return false;
      return name().equals(cmd.name()) &&
            _parent.equals(cmd.parent());
   }

   public int hashCode()
   {
      if (_parent == null)
         return name().hashCode();
      return name().hashCode() * 31 + _parent.hashCode();
   }


   /**
    * basically a guard against grafting the "Open" command
    * on maximized views
    */
   public boolean isOpenInNonMinimizedContext(EView source)
   {
      if (source instanceof Minimizable)
      {
         Minimizable minsource = (Minimizable) source;
         return ("Open".equals(name()) && !minsource.isMinimized() );
      }
      return false;
   }

   static java.util.List MINORCOMMANDS = Arrays.asList(new String[] {"Copy", "Paste"});

   /**
    * Swallow minor commands for maximized views as they should only apply to minimized
    * views (iconview or listitemview)
    * 
    * @return true if command is a minor command and view is not minimized
    */
   public boolean isMinorCommand(EView source)
   {
      if (source instanceof Minimizable)
      {
         Minimizable minsource = (Minimizable) source;
         return (MINORCOMMANDS.contains(name()) && !minsource.isMinimized() );
      }
      return false;
   }
   
   
   public boolean filter(EObject eo)
   {
      if (isForbidden(eo))
      {
         tracer().info("command "+this+" is forbidden for " +
               "user "+currentUser()+" on target "+eo+" (skipping)");
         return true;
      }
            
      if (eo instanceof ComplexEObject)
      {
         User owner = getOwner((ComplexEObject) eo);
         if (owner != null && !owner.equals(currentUser()))
         {
            return true;
         }
      }
         
      if (eo.field() != null &&
          eo.field().isAggregate() &&
          "delete".equalsIgnoreCase(name()))
      {
         return true;
      }

      return false;
   }

   public String getFullPath()
   {
      return _parent.getJavaClass().getName() + "#" + _name;
   }
   public static Command forPath(String path)
   {
      if (path == null) return null;

      try
      {
         String[] parts = path.split("#");  // split on fullpath's # separator
         Class cls = Class.forName(parts[0]);
         ComplexType type = ComplexType.forClass(cls);
         String commandName = parts[1];
         
         // this obviously needs work.  there shouldn't be two command lookup methods (!)
         Command cmd = type.findCommand(commandName);
//         Command cmd = type.command(commandName);
         if (cmd == null)
         {
            System.err.println("Can't find command: "+commandName+" on type: "+type);
         }
         return cmd;
      }
      catch (ClassNotFoundException ex)
      {
         System.err.println("ClassNotFoundException: "+ex.getMessage());
         ex.printStackTrace();
      }
      return null;
   }

   public static Class getCustomTypeImplementorClass()
   {
      return CommandUserType.class;
   }
}
