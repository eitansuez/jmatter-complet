/*
 * Created on Mar 2, 2005
 */
package com.u2d.model;

import com.u2d.element.Field;
import com.u2d.pattern.Onion;
import com.u2d.validation.ValidationNotifier;

/**
 * @author Eitan Suez
 */
public interface EObject
       extends ValidationNotifier, ChangeNotifier, Viewable
{
   public Title title();
   public boolean isEmpty();
   public int validate();
   
   public javax.swing.Icon iconSm();
   public javax.swing.Icon iconLg();

   public Onion commands();
   public Onion filteredCommands();
   public void setField(Field field, ComplexEObject parent);
   public Field field();
   
   public ComplexEObject parentObject();
   
   public EObject makeCopy();
   
   public void setValue(EObject value);
   
}