package com.u2d.element;

import com.u2d.view.EView;
import com.u2d.model.AbstractListEO;
import com.u2d.model.ComplexEObject;
import com.u2d.model.Localized;
import com.u2d.model.Title;
import com.u2d.app.Authorizer;
import com.u2d.type.atom.StringEO;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jan 18, 2008
 * Time: 6:58:44 PM
 */
public class ListCommand extends Command
{
   private Command _instanceCmd;
   
   public ListCommand() {}
   
   public ListCommand(Command instanceCmd)
   {
      _instanceCmd = instanceCmd;
   }
   
   public void execute(Object value, EView source)
         throws InvocationTargetException
   {
      AbstractListEO target = (AbstractListEO) value;
      for (Iterator itr = target.iterator(); itr.hasNext(); )
      {
         ComplexEObject eo = (ComplexEObject) itr.next();
         _instanceCmd.execute(eo, source);
      }
   }

   public boolean sensitive() { return _instanceCmd.sensitive(); }
   public boolean blocks() { return _instanceCmd.blocks(); }
   public String shortcut() { return _instanceCmd.shortcut(); }
   public char mnemonic() { return _instanceCmd.mnemonic(); }

   public boolean batchable() { return false; }

   public void deriveLabel()
   {
      super.deriveLabel();
   }

   public Authorizer getOwner(ComplexEObject parent) { return _instanceCmd.getOwner(parent); }

   public StringEO getLabel() { return _instanceCmd.getLabel(); }
   public StringEO getDescription() { return _instanceCmd.getDescription(); }
   public String localizedLabel(Localized l) { return _instanceCmd.localizedLabel(l); }

   public Title title()
   {
      return _instanceCmd.title();
   }
}
