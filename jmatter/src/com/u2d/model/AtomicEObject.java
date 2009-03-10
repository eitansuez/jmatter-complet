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
   void parseValue(String stringValue) throws java.text.ParseException;
   AtomicRenderer getRenderer();
   AtomicEditor getEditor();

   // validation-related:
   void addRule(Rule rule);
   void removeRule(Rule rule);
   Collection<Rule> rules();

   // this introduces the need to track properties on atomics
   boolean isReadOnly();
   void setReadOnly(boolean readOnly);

}
