package com.u2d.interaction;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.model.ComplexEObject;
import com.u2d.model.ComplexType;
import com.u2d.persist.Persist;
import com.u2d.element.Command;
import com.u2d.element.CommandInfo;
import com.u2d.reflection.Cmd;
import com.u2d.type.atom.BooleanEO;
import com.u2d.view.EView;
import com.u2d.list.PlainListEObject;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

@Persist
public class Instruction
      extends AbstractComplexEObject
{
   private ComplexEObject target;
   private Command action;
   private final BooleanEO active = new BooleanEO();
   
   private static Instruction _instance = new Instruction();
   public static Instruction getInstance() { return _instance; }
   
   private Map<String, ComplexType> typesMap = new HashMap<String, ComplexType>();
   
   // TODO:  how to implement arguments
   
   public static String[] fieldOrder = {"target", "action"};

   public Instruction()
   {
      buildTypesMap();
   }
   
   private void buildTypesMap()
   {
      PlainListEObject types = ComplexType.persistedTypes();
      for (Iterator itr = types.iterator(); itr.hasNext(); )
      {
         ComplexType type = (ComplexType) itr.next();
         typesMap.put(type.getNaturalName().toLowerCase(), type);
      }
   }

   public ComplexEObject getTarget() { return target; }
   public void setTarget(ComplexEObject target)
   {
      ComplexEObject oldTarget = this.target;
      this.target = target;
      firePropertyChange("target", oldTarget, this.target);
   }
   
   public void matchText(String text)
   {
      String textLower = text.toLowerCase();
      for (String name : typesMap.keySet() )
      {
         if (name.startsWith(textLower))
         {
            setTarget(typesMap.get(name));
         }
      }
   }
   
   public Command getAction() { return action; }
   public void setAction(Command action)
   {
      Command oldAction = this.action;
      this.action = action;
      firePropertyChange("action", oldAction, this.action);
   }
   
   public BooleanEO getActive() { return active; }
   // convenience:
   public boolean active() { return active.booleanValue(); }
   
   @Cmd()
   public void Activate(CommandInfo cmdInfo) { activate(); }
   public void activate() { active.setValue(true); }
   public void deactivate() { active.setValue(false); }

   public EView getMainView() { return vmech().getInstructionView(this); }

   public Title title()
   {
      if (target == null)
      {
         return new Title("A new instruction");
      }
      else
      {
         return target.title().appendParens("instruction");
      }
   }
   
   @Cmd(mnemonic='a')
   public void Invoke(CommandInfo cmdInfo)
   {
      action.Execute(cmdInfo, target);
   }
   
}
