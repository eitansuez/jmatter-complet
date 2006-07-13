/*
 * Created on Nov 9, 2004
 */
package com.u2d.restrict;

import com.u2d.app.Role;
import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.EObject;
import com.u2d.model.Title;

/**
 * @author Eitan Suez
 */
public abstract class Restriction extends AbstractComplexEObject
{
   protected Role _role;
   
   public Restriction() {}
   
   public abstract Restrictable element();

   public Role getRole() { return _role; }
   public void setRole(Role role)
   {
      Role oldRole = _role;
      _role = role;
      firePropertyChange("role", oldRole, _role);
   }
   
   public Title title()
   {
      if (element() != null && _role != null)
      {
         EObject eo = (EObject) element();
         return new Title(eo).append(" for", _role);
      }
      return new Title("");
   }
   
   
}
