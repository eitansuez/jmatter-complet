/*
 * Created on Apr 27, 2005
 */
package com.u2d.model;

import com.u2d.pattern.Onion;
import com.u2d.view.EView;
import com.u2d.validation.Rule;
import javax.swing.Icon;
import java.util.Collection;
import java.util.ArrayList;

/**
 * @author Eitan Suez
 */
public abstract class AbstractAtomicEO extends AbstractEObject
                                       implements AtomicEObject
{
   public int validate() { return 0; }

   public Onion commands() { return type().commands(); }

   public Onion filteredCommands()
   {
      return commands();  // for now..
   }

   public AtomicType type() { return AtomicType.forObject(this); }

   Collection<Rule> _rules = new ArrayList<Rule>();
   public Collection<Rule> rules() { return _rules; }
   public void addRule(Rule rule) { _rules.add(rule); }
   public void removeRule(Rule rule) { _rules.remove(rule); }


   public EView getView() { return vmech().getAtomicView(this); }
   public EView getMainView() { return getView(); }

   // somehow iconvalues for atomiceobjects should be made optional..
   public Icon iconSm() { return null; }
   public Icon iconLg() { return null; }
   public String iconSmResourceRef() { return null; }
   public String iconLgResourceRef() { return null; }

   public String marshal() {  return toString(); } 
}
