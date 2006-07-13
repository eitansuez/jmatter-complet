/*
 * Created on Mar 26, 2004
 */
package com.u2d.pubsub;

import java.util.EventObject;

/**
 * @author Eitan Suez
 */
public class AppEvent extends EventObject
{
   private String _type;
   private Object _target;
   
   public AppEvent(Object source, String type, Object target)
   {
      this(source, type);
      _target = target;
   }
   
   public AppEvent(Object source, String type)
   {
      super(source);
      _type = type;
   }
   
   public String getType() { return _type; }
   public Object getTarget() { return _target; }
   
}
