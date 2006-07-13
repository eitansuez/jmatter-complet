package com.u2d.model;

/**
 * Date: Jun 8, 2005
 * Time: 1:04:22 PM
 *
 * @author Eitan Suez
 */
public interface AtomicRenderer
{
   public void render(AtomicEObject value);
   public void passivate();
}
