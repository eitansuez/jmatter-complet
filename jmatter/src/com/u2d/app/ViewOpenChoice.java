package com.u2d.app;

import com.u2d.type.atom.ChoiceEO;

import java.util.Set;
import java.util.HashSet;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Dec 5, 2006
 * Time: 2:47:14 PM
 */
public class ViewOpenChoice extends ChoiceEO
{
   public ViewOpenChoice() {}
   public ViewOpenChoice(String value)
   {
      setValue(value);
   }
   
   public static final String IN_NEWTAB = "In new tab";
   public static final String IN_PLACE = "In place";
   public static final String IN_NEWWINDOW = "In new window";
   
   private static Set STATUS_OPTIONS = new HashSet();
   static
   {
      STATUS_OPTIONS.add(IN_NEWTAB);
      STATUS_OPTIONS.add(IN_PLACE);
      STATUS_OPTIONS.add(IN_NEWWINDOW);
   }

   public Collection entries() { return STATUS_OPTIONS; }

}