package com.u2d.contactmgr;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.element.CommandInfo;
import com.u2d.reflection.Cmd;

/**
 * Put in strictly as an example to demonstrate how to use the service object to expose
 *  "top-level" services (not bound to an entity) in an application
 */
public class ServiceObject extends AbstractComplexEObject
{
   public ServiceObject() { }

   @Cmd(mnemonic='h')
   public String SayHi(CommandInfo cmdInfo)
   {
      return "Hullo There";
   }

   private Title title = new Title("Services");
   public Title title() { return title; }
}
