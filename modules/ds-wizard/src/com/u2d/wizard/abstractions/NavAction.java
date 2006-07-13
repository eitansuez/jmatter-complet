package com.u2d.wizard.abstractions;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 25, 2005
 * Time: 9:26:22 AM
 */
public abstract class NavAction
{

   public abstract Step invoke(Step step);

   public static List times(NavAction action, int numTimes)
   {
      List list = new ArrayList();
      for (int i=0; i<numTimes; i++)
      {
         list.add(action);
      }
      return list;
   }

}
