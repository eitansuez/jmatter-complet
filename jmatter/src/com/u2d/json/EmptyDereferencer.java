package com.u2d.json;

import com.u2d.model.ComplexEObject;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Aug 14, 2008
 * Time: 11:23:55 AM
 */
public class EmptyDereferencer implements Dereferencer
{
   public ComplexEObject get(Class cls, Long id)
   {
      return null;
   }
}
