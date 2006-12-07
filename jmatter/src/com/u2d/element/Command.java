/*
 * Created on Jan 19, 2004
 */
package com.u2d.element;

import com.u2d.restrict.CommandRestriction;
import com.u2d.restrict.Restriction;
import com.u2d.view.*;
import com.u2d.app.User;
import com.u2d.app.Role;
import com.u2d.model.*;
import com.u2d.type.atom.StringEO;
import com.u2d.type.atom.BooleanEO;
import com.u2d.type.composite.Person;
import com.u2d.reflection.Cmd;
import com.u2d.reflection.Arg;
import com.u2d.pattern.Filter;
import java.util.Arrays;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Eitan Suez
 */
public abstract class Command extends Member 
{
   protected final StringEO _fullPath = new StringEO();

   public static String[] fieldOrder = {"name", "fullPath", "label", "mnemonic", "description", "sensitive"};
   public static String[] readOnly = {"name", "fullPath"};
   public static String[] identities = {"fullPath"};

   public Command() {}
   
   protected void init(FieldParent parent, String name)
   {
      _parent = parent;
      getName().setValue(name);
      
      computePath();
      
      setState(_readState, true);
   }
   
   protected void computePath()
   {
      if (_parent == null)
      {
         // currently the case for commands set on non-complex types
         // such as list types and atomic types.  this will be fixed shortly.
         _fullPath.setValue("#" + _name);
         return;
      }
      
      String fullPath = _parent.getJavaClass().getName() + "#" + _name;
      _fullPath.setValue(fullPath);
   }

   // under construction..
//   @Cmd
//   public void Execute(CommandInfo cmdInfo, @Arg("Target") ComplexEObject target)
//   {
//      try
//      {
//         execute(target, cmdInfo.getSource());
//      }
//      catch (InvocationTargetException ex)
//      {
//         ex.printStackTrace();
//      }
//   }

   public abstract void execute(Object value, EView source)
            throws java.lang.reflect.InvocationTargetException;

   /**
    * A sensitive command is one that should be guarded against
    * inadvertant invocation.  Its cost is high and undoing is 
    * difficult.  A ui is advised to make it difficult for an 
    * end user to inadvertantly invoke the command.
    */
   protected final BooleanEO _sensitive = new BooleanEO();
   public BooleanEO getSensitive() { return _sensitive; }
   public boolean sensitive() { return _sensitive.booleanValue(); }


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
   public CommandRestriction restriction() { return _restriction; }
   
   public void liftRestriction() { _restriction = null; }
   
   public boolean isForbidden(EObject target)
   {
      tracer().fine("Checking if command "+this.getFullPath()+" is forbidden.."+
         "(restriction is: "+_restriction+")");
      if (_restriction != null)
      {
         return _restriction.forbidden(target);
      }
      
      Command superCmd = superCmd(target);
      if (superCmd != null && superCmd != this)
      {
         return superCmd.isForbidden(target);
      }

      return false;
   }
   
   public Command superCmd(EObject target)
   {
      if (target instanceof ComplexEObject)
      {
         ComplexEObject targetCeo = (ComplexEObject) target;
         if (targetCeo instanceof ComplexType)
         {
            if (_parent == null)
            {
               return null;
            }
            ComplexType type = ComplexType.forClass(_parent.getJavaClass());
            ComplexType superType = type.superType();
            if (superType != null)
            {
               return superType.command(name());
            }
         }
         else if (targetCeo instanceof Member)
         {
            if (_parent == null)
            {
               return null;
            }
            ComplexType type = ComplexType.forClass(_parent.getJavaClass());
            ComplexType superType = type.superType();
            if (superType != null)
            {
               return superType.instanceCommand(name());
            }
         }
         else
         {
            ComplexType type = targetCeo.type();
            ComplexType superType = type.superType();
            if (superType != null)
            {
               return superType.instanceCommand(name());
            }
         }
      }
      else if (target instanceof AbstractListEO)
      {
         // e.g. A list view will sport its type's commands
         AbstractListEO leo = (AbstractListEO) target;
         ComplexType type = leo.type();
         return type.command(name());
         // problem:  the list of commands is constructed once.
         // TODO: fix.
      }
      return null;
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
         return name().hashCode() * 31;
      return name().hashCode() * 31 + _parent.hashCode();
   }


   /*
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
   
   
   public StringEO getFullPath() { return _fullPath; }
   public String fullPath() { return _fullPath.stringValue(); }

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
//         Command cmd = type.findCommand(commandName);
         Command cmd = type.findCommand(commandName);
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

   
   public Title title()
   {
      if (_parent == null) return super.title();
      // TODO: refactor as naturalName property, similar to how field does it.
      return(_name.title().append(((ComplexType) _parent).getPluralName()));
   }
   
   @Cmd(mnemonic='f')
   public void ForbidForRole(CommandInfo cmdInfo, @Arg("Role") Role role)
   {
      CommandRestriction restriction = new CommandRestriction(role, this);
      role.addCmdRestriction(restriction);
      persistor().updateAssociation(restriction, role);
   }
   
   
   
   static class CommandFilter implements Filter
   {
      EObject _target;
      CommandFilter(EObject target)
      {
         _target = target;
      }
      public boolean exclude(Object item)
      {
         if (!(item instanceof Command))
            return true;
         
         Command cmd = (Command) item;
         
         if (cmd.isForbidden(_target))
         {
//            cmd.tracer().info("command "+cmd+" is forbidden for " +
//                  "user "+cmd.currentUser()+" on target "+_target+" (skipping)");
            return true;
         }
         
         if (_target instanceof ComplexEObject)
         {
            User owner = cmd.getOwner((ComplexEObject) _target);
            if (owner != null && !owner.equals(cmd.currentUser()))
            {
               return true;
            }
         }
         
         if (_target.field() != null &&
             _target.field().isAggregate() &&
             "delete".equalsIgnoreCase(cmd.name()))
         {
            return true;
         }

         return false;
      }
   }
   
   public static Filter commandFilter(EObject target)
   {
      return new CommandFilter(target);
   }
   
}
