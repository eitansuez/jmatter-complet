/*
 * Created on Jan 19, 2004
 */
package com.u2d.element;

import com.u2d.restrict.CommandRestriction;
import com.u2d.restrict.Restrictable;
import com.u2d.restrict.Restriction;
import com.u2d.view.*;
import com.u2d.app.User;
import com.u2d.app.Tracing;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import java.util.Arrays;

/**
 * @author Eitan Suez
 */
public abstract class Command extends Member 
      implements Restrictable
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


   /* authorization-related */

   private boolean _forbidden = false;
   public void forbid() { _forbidden = true; }
   public void allow() { _forbidden = false; }
   public boolean isForbidden() { return _forbidden; }

   public void applyRestriction(Restriction restriction)
   {
      if (!(restriction instanceof CommandRestriction))
         throw new IllegalArgumentException("Restriction must be a command restriction");

      forbid();
   }
   public void liftRestriction(Restriction restriction)
   {
      if (!(restriction instanceof CommandRestriction))
         throw new IllegalArgumentException("Restriction must be a command restriction");

      allow();
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
      Command cmd = (Command) obj;
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
      if (isForbidden())
      {
         Tracing.tracer().info("command "+this+" is forbidden;  skipping");
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

}
