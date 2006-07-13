/*
 * Created on Apr 13, 2004
 */
package com.u2d.domain;

import com.u2d.calendar.*;
import com.u2d.model.Title;
import com.u2d.type.atom.*;

/**
 * @author Eitan Suez
 */
public class Meeting extends CalEvent
{
   private final StringEO _title = new StringEO();
   private final TimeSpan _timeSpan = new TimeSpan();
   private Resource _resource;
   
   public static String[] fieldOrder = {"title", "timeSpan", "resource"};
   
   public Meeting() {}
   
   public Meeting(String title)
   {
      _title.setValue(title);
   }
   
   public StringEO getTitle() { return _title; }
   public TimeSpan getTimeSpan() { return _timeSpan; }

   public static String timespanFieldname = "timeSpan";
   public static String schedulableFieldname = "resource";

   public Title title() { return _title.title(); }
   public Title calTitle() { return title(); }
   
   public Resource getResource() { return _resource; }
   public void setResource(Resource resource)
   {
      Resource oldResource = _resource;
      _resource = resource;
      firePropertyChange("resource", oldResource, _resource);
   }
   
}
