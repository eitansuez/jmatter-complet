package com.u2d.element;

import com.u2d.view.EView;
import com.u2d.view.View;
import com.u2d.model.*;
import com.u2d.app.Authorizer;
import com.u2d.type.atom.StringEO;
import com.u2d.ui.desktop.Positioning;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jan 18, 2008
 * Time: 6:58:44 PM
 */
public class BatchableCommand
      extends EOCommand
{
   private EOCommand _instanceCmd;
   
   public BatchableCommand() {}
   
   public BatchableCommand(EOCommand instanceCmd)
   {
      _instanceCmd = instanceCmd;
   }

   private AbstractListEO _target;
   /*
    * Desription:  execute command for each item in list.
    *  However:  if command takes arguments, then ask for them once.
    */
   public void execute(Object value, EView source)
         throws InvocationTargetException
   {
      _target = (AbstractListEO) value;
      if (_instanceCmd.haveParameters())
      {
         EObject firstItem = _target.first();
         CommandInfo cmdInfo = new CommandInfo(this, source);
         View paramsView = vmech().getParamListView(this, firstItem, cmdInfo);
         vmech().displayView(paramsView, Positioning.NEARMOUSE);
      }
      else
      {
         for (Iterator itr = _target.iterator(); itr.hasNext(); )
         {
            ComplexEObject eo = (ComplexEObject) itr.next();
            _instanceCmd.execute(eo, source);
         }
      }
   }

   public void execute(Object value, Object[] params)
         throws InvocationTargetException
   {
      for (Iterator itr = _target.iterator(); itr.hasNext(); )
      {
         ComplexEObject eo = (ComplexEObject) itr.next();
         _instanceCmd.execute(eo, params);
      }
   }

   public ParameterInfo[] paramInfo() { return _instanceCmd.paramInfo(); }

   
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
