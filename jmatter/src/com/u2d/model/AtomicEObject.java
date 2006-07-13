/*
 * Created on Jan 19, 2004
 */
package com.u2d.model;


/**
 * @author Eitan Suez
 */
public interface AtomicEObject extends EObject
{
   public void parseValue(String stringValue) throws java.text.ParseException;
   public AtomicRenderer getRenderer();
   public AtomicEditor getEditor();
}
