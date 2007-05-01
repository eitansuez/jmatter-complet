package com.u2d.wizard.abstractions;

import java.util.List;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 25, 2005
 * Time: 8:50:33 AM
 */
public class ScriptEngine
{
   Step _step; List _actions;

   public ScriptEngine(Step step, List actions)
   {
      _step = step; _actions = actions;
   }

   public void start()
   {
      Step step = _step;

      step.getView();

      for (Iterator itr = _actions.iterator(); itr.hasNext(); )
      {
         NavAction action = (NavAction) itr.next();
         if (action == null) break; // done
         if ( (step = action.invoke(step)) == null) break; // done

         step.getView();
      }
   }
}
