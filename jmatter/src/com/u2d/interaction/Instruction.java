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
import com.u2d.list.SimpleListEO;

import java.util.*;

@Persist
public class Instruction
      extends AbstractComplexEObject
{
   private ComplexEObject target;
   private final SimpleListEO _targetMatches = new SimpleListEO(ComplexEObject.class);
   
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
         typesMap.put(type.title().toString().toLowerCase(), type);
      }
   }
   
   public void clear()
   {
      setTarget(null);
      setAction(null);
   }

   public ComplexEObject getTarget() { return target; }
   public void setTarget(ComplexEObject target)
   {
      ComplexEObject oldTarget = this.target;
      this.target = target;
      setAction(target == null ? null : target.defaultCommand());
      firePropertyChange("target", oldTarget, this.target);
   }
   
   private SortedSet<Match> matchedItems = new TreeSet<Match>();
   public synchronized void matchText(String text)
   {
      matchedItems.clear();
      if (text == null || text.length() == 0)
      {
         _targetMatches.clear();
         return;
      }
      
      text = text.toLowerCase();
      for (String name : typesMap.keySet() )
      {
         int cost = Match.cost(name, text);
         if (cost >= 0)
         {
            ComplexType type = typesMap.get(name);
            matchedItems.add(new Match(cost, type));
         }
      }
      if (matchedItems.isEmpty())
      {
         setTarget(null);
      }
      else
      {
         Match bestmatch = matchedItems.first();
         setTarget(bestmatch.eo());
         matchedItems.remove(bestmatch);
         
         List matchObjs = new ArrayList();
         for (Match match : matchedItems)
         {
            matchObjs.add(match.eo());
         }
         _targetMatches.setItems(matchObjs);
      }
   }
   public Set matchedItems() { return matchedItems; }
   public SimpleListEO getTargetMatches()
   {
      return _targetMatches;
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
