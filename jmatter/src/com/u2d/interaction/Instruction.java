package com.u2d.interaction;

import com.u2d.model.*;
import com.u2d.persist.Persist;
import com.u2d.element.Command;
import com.u2d.element.CommandInfo;
import com.u2d.reflection.Cmd;
import com.u2d.type.atom.BooleanEO;
import com.u2d.view.EView;
import com.u2d.list.SimpleListEO;
import com.u2d.field.Association;
import java.util.*;
import java.lang.reflect.InvocationTargetException;

@Persist
public class Instruction
      extends AbstractComplexEObject
{
   public static String[] fieldOrder = {"target", "action"};
   
   private ComplexEObject target;
   private final SimpleListEO _targetMatches = new SimpleListEO(ComplexEObject.class);
   
   private Command action;
   private final SimpleListEO _actionMatches = new SimpleListEO(ComplexEObject.class);
   
   private final BooleanEO active = new BooleanEO();

   
   public Instruction()
   {
   }
   
   public ComplexEObject getTarget() { return target; }
   public void setTarget(ComplexEObject target)
   {
      ComplexEObject oldTarget = this.target;
      this.target = target;
      setAction(target == null ? null : target.defaultCommand());
      matchActionText("");  // default show all commands in options listing
      firePropertyChange("target", oldTarget, this.target);
   }
   
   public Command getAction() { return action; }
   public void setAction(Command action)
   {
      Command oldAction = this.action;
      this.action = action;
      firePropertyChange("action", oldAction, this.action);
   }
   
   public void clear()
   {
      setTarget(null);
      setAction(null);
   }

   public BooleanEO getActive() { return active; }
   // convenience:
   public boolean active() { return active.booleanValue(); }
   
   public void activate() { active.setValue(true); }
   public void deactivate() { active.setValue(false); }


   @Cmd
   public void Invoke(CommandInfo cmdInfo)
   {
      if (target == null || action == null) return;

      try
      {
         action.execute(target, cmdInfo.getSource());
      }
      catch (InvocationTargetException e)
      {
         e.printStackTrace();
      }
      deactivate();
   }
   

   private synchronized void matchText(Association association,
                                      SimpleListEO matchesList, 
                                      Map<String, ComplexEObject> optMap,
                                      String text)
   {
      SortedSet<Match> matchedItems = new TreeSet<Match>();
      if (text == null) text = "";
      
      text = text.toLowerCase();
      for (String name : optMap.keySet() )
      {
         int cost = Match.cost(name, text);
         if (cost >= 0)
         {
            ComplexEObject eo = optMap.get(name);
            matchedItems.add(new Match(cost, eo));
         }
      }
      if (matchedItems.isEmpty())
      {
         association.set(null);
      }
      else
      {
         Match bestmatch = matchedItems.first();
         association.set(bestmatch.eo());
         
         List matchObjs = new ArrayList();
         int count = 0;
         for (Match match : matchedItems)
         {
            matchObjs.add(match.eo());
            count++;
            if (count > 10) break;
         }
         matchesList.setItems(matchObjs);
      }
   }
   public void matchTargetText(String text)
   {
      matchText(association("target"), _targetMatches, app().getIndex(), text);
   }
   public void matchActionText(String text)
   {
      if (target == null) return;
      Map<String, ComplexEObject> cmdOptions = new HashMap<String, ComplexEObject>();
      for (Iterator itr = target.commands().deepIterator(); itr.hasNext(); )
      {
         Command cmd = (Command) itr.next();
         cmdOptions.put(cmd.title().toString().toLowerCase(), cmd);
      }
      matchText(association("action"), _actionMatches, cmdOptions, text);
      // favor default action if matched..
      if (_actionMatches.contains(target.defaultCommand()))
      {
         setAction(target.defaultCommand());
      }
   }
   
   public SimpleListEO getTargetMatches()
   {
      return _targetMatches;
   }
   public SimpleListEO getActionMatches()
   {
      return _actionMatches;
   }
   
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
   
}
