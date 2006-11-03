/*
 * Created on Nov 9, 2004
 */
package com.u2d.restrict;

import com.u2d.app.Role;
import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.element.Member;

/**
 * @author Eitan Suez
 */
public abstract class Restriction extends AbstractComplexEObject
{
   protected Role _role;
   protected Member _element;
   
   public Restriction() {}
   public Restriction(Role role, Member element)
   {
      _role = role;  _element = element;
   }
   
   public Role getRole() { return _role; }
   public void setRole(Role role)
   {
      Role oldRole = _role;
      _role = role;
      firePropertyChange("role", oldRole, _role);
   }
   
   public Member getElement() { return _element; }
   public void setElement(Member element)
   {
      Member oldElement = _element;
      _element = element;
      firePropertyChange("element", oldElement, _element);
   }
   
   public Title title()
   {
      if (_element != null && _role != null)
      {
         return new Title(_element).append(" for", _role);
      }
      return new Title("");
   }
   
   
}
