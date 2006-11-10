package com.u2d.restrict;

import com.u2d.model.EObject;
import com.u2d.app.User;

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
      
      User user = (User) target;
      return (!user.equals(currentUser()));
   }
}
