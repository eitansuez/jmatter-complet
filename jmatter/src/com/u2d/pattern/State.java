/*
 * Created on Jan 31, 2004
 */
package com.u2d.pattern;

/**
 * @author Eitan Suez
 */
public abstract class State
{
   protected String _name;
   {
      String clsName = getClass().getName();
      if (clsName.endsWith("State"))
      {
         int idx1 = clsName.lastIndexOf("$") + 1;
         int idx2 = clsName.lastIndexOf(("State"));
         _name = clsName.substring(idx1, idx2);
      }
      else
      {
         throw new RuntimeException("What? A state class whose name does not end" +
               " with state? i want to see: "+clsName);
      }
   }
   
   public String getName() { return _name; }
   
   public String toString() { return getName(); }
   
   public boolean equals(Object obj)
   {
      if (obj != null && obj instanceof State)
      {
         State stateObj = (State) obj;
         return getName().equals(stateObj.getName());
      }
      return false;
   }

   public int hashCode() { return getName().hashCode(); }

}
