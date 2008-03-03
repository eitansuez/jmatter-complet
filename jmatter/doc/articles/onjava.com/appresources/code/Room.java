package org.jmatter.j1mgr;

import com.u2d.model.Title;
import com.u2d.model.AbstractComplexEObject;
import com.u2d.type.atom.*;
import com.u2d.persist.Persist;
import com.u2d.calendar.ScheduleEO;
import com.u2d.calendar.Schedule;
import com.u2d.calendar.DateTimeBounds;
import com.u2d.calendar.CellResChoice;
import com.u2d.list.RelationalList;

import java.util.Calendar;
import java.text.ParseException;

@Persist
public class Room extends AbstractComplexEObject
{
   private final StringEO name = new StringEO();
   private final IntEO capacity = new IntEO();

   public static String[] fieldOrder = {"name", "capacity"};
   
   public Room() { }

   public void initialize()
   {
      super.initialize();
      capacity.setValue(500);
   }

   public StringEO getName() { return name; }
   public IntEO getCapacity() { return capacity; }

   public Title title() { return name.title(); }

}
