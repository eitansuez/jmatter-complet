/*
 * Created on Jan 19, 2004
 */
package com.u2d.model;

import com.u2d.validation.Rule;

import java.util.Collection;


/**
 * @author Eitan Suez
 */
public interface AtomicEObject extends EObject, Marshallable
{
   public void parseValue(String stringValue) throws java.text.ParseException;
   public AtomicRenderer getRenderer();
   public AtomicEditor getEditor();

   // validation-related:
   public void addRule(Rule rule);
   public void removeRule(Rule rule);
   public Collection<Rule> rules();
}
