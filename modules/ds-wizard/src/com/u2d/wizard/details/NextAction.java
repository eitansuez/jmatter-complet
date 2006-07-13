package com.u2d.wizard.details;

import com.u2d.wizard.abstractions.NavAction;
import com.u2d.wizard.abstractions.Step;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 25, 2005
 * Time: 9:26:34 AM
 */
public class NextAction extends NavAction
{
   public static NextAction INSTANCE = new NextAction();

   public Step invoke(Step step) { return step.nextStep(); }

   public static List times(int numTimes)
   {
      return NavAction.times(INSTANCE, numTimes);
   }
}
