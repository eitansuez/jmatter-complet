package com.u2d.wizard;

import com.u2d.wizard.abstractions.Condition;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 25, 2005
 * Time: 11:03:30 AM
 */
public class MockCondition implements Condition
{
   public MockCondition() {}

   boolean _answer = false;
   public MockCondition instrument(boolean answer) { _answer = answer; return this; }

   public boolean evaluate()
   {
      return _answer;
   }
}
