package com.u2d.view.swing;

import com.u2d.element.Command;
import com.u2d.view.EView;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Apr 17, 2008
 * Time: 2:02:14 PM
 */
public class TestableCommandAdapter extends CommandAdapter
{
   public static int count = 0;
   static Map<CommandAdapter, Exception> tracemap = new HashMap<CommandAdapter, Exception>();

   public TestableCommandAdapter(Command command, EView source)
   {
      super(command, source);
      count++;
      tracemap.put(this, new Exception());
   }

   public void detach()
   {
      super.detach();

      count--;
      tracemap.remove(this);
   }


   public static int count()
   {
      System.out.printf("count: %d\n", count);
      return count;
   }
   public static Collection<Exception> constrTraces()
   {
      return tracemap.values();
   }

}
