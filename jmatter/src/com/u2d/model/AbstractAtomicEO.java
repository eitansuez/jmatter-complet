/*
 * Created on Apr 27, 2005
 */
package com.u2d.model;

import com.u2d.pattern.Onion;
import com.u2d.view.EView;
import javax.swing.Icon;

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

   public EView getView() { return vmech().getAtomicView(this); }
   public EView getMainView() { return getView(); }

   // somehow iconvalues for atomiceobjects should be made optional..
   public Icon iconSm() { return null; }
   public Icon iconLg() { return null; }

}
