package com.u2d.sympster;

/**
 * User: eitan
 * Date: Mar 10, 2008
 */
public class Application extends com.u2d.app.Application
{
   public void postInitialize()
   {
      super.postInitialize();
      contributeToIndex(Talk.class, Speaker.class);
   }

}
