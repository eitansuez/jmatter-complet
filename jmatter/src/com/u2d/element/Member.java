/*
 * Created on Jan 19, 2004
 */
package com.u2d.element;

import java.util.*;

import com.u2d.model.FieldParent;
import com.u2d.restrict.Restrictable;

/**
 * @author Eitan Suez
 */
public abstract class Member extends ProgrammingElement implements Restrictable
{
   protected FieldParent _parent;
   
   public FieldParent parent() { return _parent; }

   public static Member member(String memberName, List members)
   {
      Iterator itr = members.iterator();
      Member member = null;
      while (itr.hasNext())
      {
         member = (Member) itr.next();
         if (member.name().equals(memberName))
            return member;
      }
      return null;
   }
   
   
   public static Comparator nameComparator(String[] memberOrder)
   {
      return  new NameComparator(memberOrder);
   }

   
   private static class NameComparator implements java.util.Comparator
   {
      private String[] _memberOrder;
      private Map _inverted = new HashMap();
      
      NameComparator(String[] memberOrder)
      {
         _memberOrder = memberOrder;
         for (int i=0; i<memberOrder.length; i++)
            _inverted.put(memberOrder[i], new Integer(i));
      }
      
      public int compare(Object o1, Object o2)
      {
         Member f1 = (Member) o1; Member f2 = (Member) o2;
         return getIndex(f1.name()) - getIndex(f2.name());
      }
      
      private int getIndex(String memberName)
      {
         Integer value = (Integer) _inverted.get(memberName);
         if (value == null)
         {
            //System.err.println("No index for member name "+memberName);
            return _inverted.size();
         }
         return value.intValue();
      }
      
      public boolean equals(Object obj)
      {
         if (obj == this) return true;
         if (!(obj instanceof NameComparator)) return false;
         NameComparator fobj = (NameComparator) obj;
         return fobj.getInfo().equals(_memberOrder);
      }

      public int hashCode() { return _memberOrder.hashCode(); }

      public String[] getInfo() { return _memberOrder; }
   }

   protected char _mnemonic;
   public char getMnemonic() { return _mnemonic; }
   public boolean hasMnemonic() { return _mnemonic != '\0'; }

   /**
    * Note: when dealing with Swing: JButtons bound to swing Action's
    * Then the mnemonic is set via a call to putValue() which takes
    * as an argument, the Integer value of the mnemonic character.
    * 
    * I have verified that there's a bug in java where the integer value
    * must be the integer code of the upper case version of the mnemonic.
    * Otherwise, invoking the mnemonic won't work (although it will display
    * correctly).
    * 
    * This should explain the implementation below: 
    */
   public void setMnemonic(char mnemonic)
   {
      _mnemonic = Character.toUpperCase(mnemonic);
   }
   
}
