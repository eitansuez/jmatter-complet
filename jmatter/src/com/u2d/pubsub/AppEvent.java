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
   private Object _eventInfo;
   
   public AppEvent(Object source, String type)
   {
      super(source);
      _type = type;
   }

   public AppEvent(Object source, String type, Object eventInfo)
   {
      this(source, type);
      _eventInfo = eventInfo;
   }
   
   public String getType() { return _type; }
   public Object getEventInfo() { return _eventInfo; }
   
}
