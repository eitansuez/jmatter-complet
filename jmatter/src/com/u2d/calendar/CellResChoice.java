package com.u2d.calendar;

import com.u2d.type.atom.TimeInterval;
import com.u2d.type.atom.ChoiceEO;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Mar 12, 2007
 * Time: 5:07:18 PM
 */
public class CellResChoice extends ChoiceEO
{
   private int _minutes;
   
   public CellResChoice() {}
   public CellResChoice(int minutes)
   {
      _minutes = minutes;
      setValue(""+_minutes);
   }

   public static final CellResChoice ONE_MINUTE = new CellResChoice(1);
   public static final CellResChoice TWO_MINUTES = new CellResChoice(2);
   public static final CellResChoice FIVE_MINUTES = new CellResChoice(5);
   public static final CellResChoice TEN_MINUTES = new CellResChoice(10);
   public static final CellResChoice FIFTEEN_MINUTES = new CellResChoice(15);
   public static final CellResChoice THIRTY_MINUTES = new CellResChoice(30);
   public static final CellResChoice ONE_HOUR = new CellResChoice(60);
   
   private static List<CellResChoice> OPTIONS = new ArrayList<CellResChoice>();
   static
   {
      OPTIONS.add(ONE_MINUTE);
      OPTIONS.add(TWO_MINUTES);
      OPTIONS.add(FIVE_MINUTES);
      OPTIONS.add(TEN_MINUTES);
      OPTIONS.add(FIFTEEN_MINUTES);
      OPTIONS.add(THIRTY_MINUTES);
      OPTIONS.add(ONE_HOUR);
   }

   public Collection entries() { return OPTIONS; }

   public int minutes() { return _minutes; }
   
   public TimeInterval timeInterval()
   {
      return new TimeInterval(Calendar.MINUTE, _minutes);
   }
   
   public String toString()
   {
      return _minutes + " min.";
   }

   // --
   
   public CellResChoice next()
   {
      int index = Math.min(OPTIONS.indexOf(this) + 1, OPTIONS.size() - 1);
      return OPTIONS.get(index);
   }
   public CellResChoice previous()
   {
      int index = Math.max(OPTIONS.indexOf(this) - 1, 0);
      return OPTIONS.get(index);
   }

}