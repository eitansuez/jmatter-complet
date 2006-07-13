/*
 * Created on Mar 10, 2004
 */
package com.u2d.domain;

import com.u2d.app.Application;
import com.u2d.model.AbstractListEO;
import com.u2d.model.ComplexType;
import java.util.*;
import com.u2d.calendar.*;

/**
 * @author Eitan Suez
 */
public class App extends Application
{
   public void initObjects()
   {
//       Order order = new Order("My Order");
//       order.addItem("item 1");
//       order.addItem("item 2");
//       order.addItem("item 3");
//       order.save();
   }
   
   public List loadSchedules()
   {
      ComplexType type = ComplexType.forClass(Resource.class);
      AbstractListEO resources = type.Browse(null);
      List schedules = new ArrayList();
      Iterator itr = resources.iterator();
      Resource resource;  Schedule schedule;
      while (itr.hasNext())
      {
         resource = (Resource) itr.next();
         schedule = resource.ShowSchedule(null);
         schedules.add(schedule);
      }
      return schedules;
   }

   public static void main(String[] args)
   {
      new App();
   }
}
