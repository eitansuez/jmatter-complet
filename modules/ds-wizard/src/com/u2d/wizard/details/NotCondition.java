package com.u2d.wizard.details;

import com.u2d.wizard.abstractions.Condition;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 25, 2005
 * Time: 11:49:07 AM
 */
public class NotCondition implements Condition
{
   Condition _condition;
   public NotCondition(Condition condition)
   {
      _condition = condition;
   }

   public boolean evaluate()
   {
      return !(_condition.evaluate());
   }
}
