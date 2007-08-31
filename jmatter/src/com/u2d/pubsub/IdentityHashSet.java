package com.u2d.pubsub;

import java.util.*;

/**
 * A revision of java.util.HashSet where the difference is that the set is
 * backed by an identityhashmap instead of a hashmap.
 */
public class IdentityHashSet<E>
      extends AbstractSet<E>
      implements Set<E>, Cloneable, java.io.Serializable
{
   static final long serialVersionUID = -5024744406713321676L;

   private transient IdentityHashMap<E, Object> map;

   private static final Object PRESENT = new Object();

   public IdentityHashSet()
   {
      map = new IdentityHashMap<E, Object>();
   }

   public IdentityHashSet(Collection<? extends E> c)
   {
      map = new IdentityHashMap<E, Object>(Math.max((int) (c.size() / .75f) + 1, 16));
      addAll(c);
   }

   public IdentityHashSet(int initialCapacity)
   {
      map = new IdentityHashMap<E, Object>(initialCapacity);
   }

   public Iterator<E> iterator() { return map.keySet().iterator(); }
   public int size() { return map.size(); }
   public boolean isEmpty() { return map.isEmpty(); }
   public boolean contains(Object o) { return map.containsKey(o); }

   public boolean add(E o) { return map.put(o, PRESENT) == null; }
   public boolean remove(Object o) { return map.remove(o) == PRESENT; }
   public void clear() { map.clear(); }

   public Object clone()
   {
      try
      {
         IdentityHashSet<E> newSet = (IdentityHashSet<E>) super.clone();
         newSet.map = (IdentityHashMap<E, Object>) map.clone();
         return newSet;
      }
      catch (CloneNotSupportedException e)
      {
         throw new InternalError();
      }
   }

   private void writeObject(java.io.ObjectOutputStream s)
         throws java.io.IOException
   {
      // Write out any hidden serialization magic
      s.defaultWriteObject();

      s.writeInt(map.size());

      for (Iterator i = map.keySet().iterator(); i.hasNext();)
         s.writeObject(i.next());
   }

   private void readObject(java.io.ObjectInputStream s)
         throws java.io.IOException, ClassNotFoundException
   {
      s.defaultReadObject();

      map = new IdentityHashMap<E, Object>();

      int size = s.readInt();

      for (int i = 0; i < size; i++)
      {
         E e = (E) s.readObject();
         map.put(e, PRESENT);
      }
   }
}
