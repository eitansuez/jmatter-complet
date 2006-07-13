/*
 * Created on Apr 7, 2004
 */
package com.u2d.element;

import com.u2d.view.*;

/**
 * @author Eitan Suez
 */
public class CommandInfo
{
   private EView _source = null;
   private EOCommand _cmd;

   public CommandInfo(EOCommand cmd, EView source)
   {
      _source = source;
      _cmd = cmd;
   }


   public EOCommand getCommand() { return _cmd; }
   public EView getSource() { return _source; }

}
