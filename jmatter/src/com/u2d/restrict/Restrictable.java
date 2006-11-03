/*
 * Created on Nov 28, 2004
 */
package com.u2d.restrict;

/**
 * @author Eitan Suez
 */
public interface Restrictable
{
   public void applyRestriction(Restriction restriction);
   public void liftRestriction();
}
