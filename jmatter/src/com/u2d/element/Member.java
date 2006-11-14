/*
 * Created on Jan 19, 2004
 */
package com.u2d.element;

import java.util.*;

import com.u2d.model.FieldParent;
import com.u2d.restrict.Restrictable;
import com.u2d.type.atom.StringEO;
import com.u2d.type.atom.CharEO;

/**
 * @author Eitan Suez
 */
public abstract class Member extends ProgrammingElement implements Restrictable
{
   protected FieldParent _parent;
   protected final StringEO _description = new StringEO();
   
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
      private Map<String, Integer> _inverted = new HashMap<String, Integer>();
      
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

   protected final CharEO _mnemonic = new CharEO();
   public CharEO getMnemonic() { return _mnemonic; }
   public boolean hasMnemonic() { return !_mnemonic.isEmpty(); }
   public char mnemonic() { return _mnemonic.charValue(); }
   
   /*
    * consider moving descrption up to base class (ProgrammingElement).
    * will do here for now.
    */
   public StringEO getDescription() { return _description; }
   // convenience..
   public String description() { return _description.stringValue(); }
   
   public abstract StringEO getFullPath();
   
}
