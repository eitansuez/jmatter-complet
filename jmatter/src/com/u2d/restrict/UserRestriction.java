package com.u2d.restrict;

import com.u2d.model.EObject;
import com.u2d.app.User;
import com.u2d.field.IndexedField;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Nov 9, 2006
 * Time: 11:37:35 AM
 */
public class UserRestriction extends CommandRestriction
{
   public UserRestriction() {}
   
   public boolean forbidden(EObject target)
   {
      tracer().fine("Checking if command: " + member() + 
            " is forbidden for user " + currentUser() + 
            " on target object " + target);
      
      if (target instanceof IndexedField) return false;  // special case..
      
      if (!(target instanceof User))
      {
         String msg = "Detected a UserRestriction on a target not of type User:\n";
         msg += "\tTarget type: "+target.getClass() + "\n";
         msg += "\tCommand: "+_member;
         throw new RuntimeException(msg);
      }
      User user = (User) target;
      return (!user.equals(currentUser()));
   }
}
