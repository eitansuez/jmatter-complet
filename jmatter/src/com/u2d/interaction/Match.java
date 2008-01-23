package com.u2d.interaction;

import com.u2d.model.ComplexEObject;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jan 22, 2008
 * Time: 10:15:08 PM
 */
public class Match implements Comparable<Match>
{
   private int _cost;
   private ComplexEObject _eo;
      
   Match(int cost, ComplexEObject eo)
   {
      _cost = cost;  _eo = eo;
   }
   public int cost() { return _cost; }
   public ComplexEObject eo() { return _eo; }

   public int compareTo(Match match)
   {
      int cmp = new Integer(_cost).compareTo(match.cost());
      if (cmp == 0) // if they're equal order.. then force a tie breaker..
      {
         return _eo.title().toString().compareTo(match.eo().title().toString());
      }
      else return cmp;
   }

   public static int cost(String name, String text)
   {
      int cost = 0;
      String remainder = name;
      for (int i=0; i<text.length(); i++)
      {
         char c = text.charAt(i);
         int subcost = remainder.indexOf(c);
         if (subcost < 0) return subcost;
         remainder = remainder.substring(subcost+1);
         cost += subcost;
      }
      return cost;
   }

   public String toString()
   {
      if (_eo == null) return "";
      return String.format("%s.cost: %d", _eo.title().toString(), _cost);
   }

   public int hashCode()
   {
      return _eo.hashCode() + 31;
   }

   public boolean equals(Object obj)
   {
      if (obj == this) return true;
      if (!(obj instanceof Match)) return false;
      Match match = (Match) obj;
      return match.eo().equals(_eo);
   }
}
