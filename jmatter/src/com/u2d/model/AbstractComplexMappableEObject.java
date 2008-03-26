package com.u2d.model;

import com.u2d.reflection.Cmd;
import com.u2d.reflection.ListCmd;
import com.u2d.element.CommandInfo;
import com.u2d.app.Context;

/**
 * Note/TODO:  instead of introducing this class and requiring its extension to "get" these commands,
 *  you should be able to dynamically mix this in.  That is, define a class just like this.  Then, as
 *  you harvest the types, check if implements Mappable.  If so, then grab this "type"'s commands and
 *  add them to that type's commands. 
 * 
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jan 25, 2008
 * Time: 9:20:53 PM
 */
public abstract class AbstractComplexMappableEObject extends AbstractComplexEObject implements MappableEO
{
   @Cmd(mnemonic='a',iconref="compass")
   public Object ViewOnMap(CommandInfo cmdInfo)
   {
      try
      {
         return vmech().getMapView(this);
      }
      catch (RuntimeException ex)
      {
         return ex.getMessage(); // display error message to user.
      }
   }
   
   @ListCmd
   public static Object ViewOnMap(CommandInfo cmdInfo, AbstractListEO list)
   {
      try
      {
         return Context.getInstance().getViewMechanism().getListViewOnMap(list);
      }
      catch (RuntimeException ex)
      {
         return ex.getMessage(); // display error message to user.
      }
   }
   
}
