package com.u2d.wizard.details;

import com.u2d.wizard.abstractions.NavAction;
import com.u2d.wizard.abstractions.Step;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 25, 2005
 * Time: 9:26:54 AM
 */
public class PreviousAction extends NavAction
{
   public static PreviousAction INSTANCE = new PreviousAction();

   public Step invoke(Step step) { return step.previousStep(); }

   public static List times(int numTimes)
   {
      return NavAction.times(INSTANCE, numTimes);
   }
}
