/*
 * Created on Apr 13, 2004
 */
package com.u2d.domain;

import com.u2d.calendar.*;
import com.u2d.element.CommandInfo;
import com.u2d.type.atom.*;
import com.u2d.list.RelationalList;
import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.reflection.Cmd;
import com.u2d.persist.Persist;

/**
 * @author Eitan Suez
 */
@Persist
public class Resource extends AbstractComplexEObject implements Schedulable
{
   public static String[] fieldOrder = {"name", "meetings"};
   
   public Resource() {}
   
   private final StringEO _name = new StringEO();
   public StringEO getName() { return _name; }

   public Title title() { return _name.title(); }
   
   private final RelationalList _meetings = new RelationalList(Meeting.class);
   public static Class meetingsType = Meeting.class;
   public RelationalList getMeetings() { return _meetings; }

   
   @Cmd
   public Schedule ShowSchedule(CommandInfo cmdInfo)
   {
      return schedule();
   }
   
   public String getTitle() { return title().toString(); }
   
   
   public Class eventType() { return Meeting.class; }
   private Schedule _schedule = null;
   public Schedule schedule()
   {
      if (_schedule == null)
         _schedule = new Schedule(this);
      return _schedule;
   }

}
