/*
 * Created on Nov 9, 2004
 */
package com.u2d.restrict;

import com.u2d.app.Role;
import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.element.Member;

/**
 * Discussion: The reason that a restriction's "member" is not
 * refactored into the base class (here) is due to differences in
 * the way that Fields and EOCommands are persisted.  Polymorphic
 * implementation also implies polymorphic data model which doesn't
 * work in this case.  The field would be stored in the base table
 * which is problematic because Field and EOCommand are stored in
 * different ways.  Fields are serialized into a single string
 * representation (the fieldpath) while commands use two columns.
 * This can be done but would have to write an adapter user type.
 * 
 * 
 * @author Eitan Suez
 */
public abstract class Restriction extends AbstractComplexEObject
{
   protected Role _role;
   
   public Restriction() {}
   public Restriction(Role role)
   {
      _role = role;
   }
   
   public Role getRole() { return _role; }
   public void setRole(Role role)
   {
      Role oldRole = _role;
      _role = role;
      firePropertyChange("role", oldRole, _role);
   }
   
   public abstract Member member();
   
   public Title title()
   {
      if (member() != null && _role != null)
      {
         return new Title(member()).appendParens(_role.toString()+" role");
      }
      return new Title("");
   }


   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (!(obj instanceof Restriction)) return false;
      if (obj == this) return true;
      Restriction r = (Restriction) obj;
      // state not yet set..assume false..
      if (_role == null || member() == null)
      {
         return false;
      }
      return _role.equals(r.getRole()) && member().equals(r.member());
   }

}
