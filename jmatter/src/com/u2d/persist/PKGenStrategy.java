package com.u2d.persist;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Jul 16, 2008
 * Time: 3:56:52 PM
 */
public enum PKGenStrategy
{
   ASSIGNED, NATIVE, SEQUENCE;

   public String strategyName()
   {
      return name().toLowerCase();
   }
}
