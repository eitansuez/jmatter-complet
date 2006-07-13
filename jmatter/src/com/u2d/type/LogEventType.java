package com.u2d.type;

import com.u2d.type.atom.ChoiceEO;
import com.u2d.type.composite.LoggedEvent;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;

/**
 * Date: Jun 9, 2005
 * Time: 10:48:20 AM
 *
 * @author Eitan Suez
 */
public class LogEventType extends ChoiceEO
{
   public LogEventType() {}
   public LogEventType(String value)
   {
      setValue(value);
   }

   private static Set STATUS_OPTIONS = new HashSet();
   static
   {
      STATUS_OPTIONS.add(LoggedEvent.DEBUG);
      STATUS_OPTIONS.add(LoggedEvent.INFO);
      STATUS_OPTIONS.add(LoggedEvent.IMPORTANT);
      STATUS_OPTIONS.add(LoggedEvent.ERROR);
      STATUS_OPTIONS.add(LoggedEvent.LOGIN);
      STATUS_OPTIONS.add(LoggedEvent.LOGOUT);
   }

   public Collection entries() { return STATUS_OPTIONS; }

}

