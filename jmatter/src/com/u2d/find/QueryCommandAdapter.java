/*
 * Created on May 4, 2005
 */
package com.u2d.find;

import java.lang.reflect.InvocationTargetException;
import com.u2d.element.Command;
import com.u2d.element.CommandInfo;
import com.u2d.list.CriteriaListEO;
import com.u2d.view.EView;
import com.u2d.model.FieldParent;
import com.u2d.ui.desktop.Positioning;

/**
 * @author Eitan Suez
 */
public class QueryCommandAdapter extends Command
{
   private CompositeQuery _query;

   public QueryCommandAdapter(CompositeQuery query, FieldParent parent)
   {
      _query = query;
      _name.setValue(_query.getName());
      _parent = parent;
   }

   public void execute(Object value, EView source)
         throws InvocationTargetException
   {
      CriteriaListEO leo =
         _query.Execute(new CommandInfo(null, source));
      // TODO: resolve issue of CommandAt constructor requiring an EOCommand

      vmech().displayViewFor(leo, source, Positioning.NEARMOUSE);
   }

}
