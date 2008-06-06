package com.u2d.restrict;

import com.u2d.model.ComplexType;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Nov 9, 2006
 * Time: 11:39:56 AM
 */
public class CreationRestriction extends CommandRestriction
{
   public CreationRestriction() {}

   public CreationRestriction(ComplexType type)
   {
      _member = type.command("New");
   }
}
