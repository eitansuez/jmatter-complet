/*
 * Created on Jan 20, 2004
 */
package com.u2d.domain;

import com.u2d.element.Command;
import com.u2d.view.*;

/**
 * @author Eitan Suez
 */
public class HelloCommand extends Command
{
   public HelloCommand()
   {
      _name.setValue("Hello");
      _label.setValue("Say Hello");
      _mnemonic.setValue('h');
   }
   
   public void execute(Object value, EView source)
   {
      System.out.println(value.toString()+" says \"Hello\"");
   }
}
