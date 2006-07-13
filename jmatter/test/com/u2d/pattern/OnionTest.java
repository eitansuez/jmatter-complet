/*
 * Created on Nov 8, 2004
 */
package com.u2d.pattern;

import junit.framework.TestCase;

/**
 * @author Eitan Suez
 */
public class OnionTest extends TestCase
{
   static String[] set1 = {"one", "two", "three"};
   static String[] set2 = {"ehad", "shtayim", "shalosh", "arba"};
   static String[] set3 = {"un", "deux", "trois", "quatre", "cinq"};
   
   protected void setUp() throws Exception
   {
   }
   
//   public void testDeepIterator()
//   {
//      Onion cmds = _shipmentType.commands();
//      Iterator itr = cmds.deepIterator();
//      int count = 0;
//      Command cmd = null;
//      while (itr.hasNext())
//      {
//         cmd = (Command) itr.next();
////         System.out.println(cmd.getName());
//         count++;
//      }
//      String msg = "total command count for shipment type should be 8 (type cmds (4) + instance cmds (4))";
//      assertEquals(msg, 8, count);
//   }
   
   public void testMergeIn()
   {
      Onion onion1 = new Onion();
      add(onion1, set1);
      Onion onion2 = new Onion();
      add(onion2, set2);
      onion2.wrap(onion1);
      
      assertEquals(set1.length + set2.length, onion2.size());
      // now have one onion, onion2, with an inner layer (onion1)
      
      Onion onion3 = new Onion();
      add(onion3, set3);
      onion2.mergeIn(onion3);  // onion3 should get added as an inner layer
       // of the innerlayer of onion2
      
      assertEquals(set1.length + set2.length + set3.length, onion2.size());
      assertEquals(set1.length + set3.length, onion1.size());
   }
   
   public void testDeepCopy()
   {
      Onion onion1 = new Onion();
      add(onion1, set1);
      Onion onion2 = new Onion();
      add(onion2, set2);
      onion2.wrap(onion1);
      
      Onion onion3 = new Onion();
      add(onion3, set3);
      
      onion2.mergeIn(onion3);
      
      Onion copy = onion2.deepCopy();
      
      assertNotSame(copy, onion2);
      assertEquals(copy.size(), onion2.size());
      assertEquals(copy.numLayers(), onion2.numLayers());
      
   }
   
   public void testNumLayers()
   {
      Onion onion1 = new Onion();
      add(onion1, set1);
      Onion onion2 = new Onion();
      add(onion2, set2);
      onion2.wrap(onion1);
      
      Onion onion3 = new Onion();
      add(onion3, set3);
      
      onion2.mergeIn(onion3);
      
      assertEquals(3, onion2.numLayers());
   }
   
   public void testWrap()
   {
      Onion onion1 = new Onion();
      add(onion1, set1);
      
      Onion onion2 = new Onion();
      add(onion2, set2);
      
      onion2.wrap(onion1);
      
      assertEquals(set1.length + set2.length, onion2.size());
      assertSame(onion2.getInnerLayer(), onion1);
   }
   
   private void add(Onion onion, Object[] list)
   {
      for (int i=0; i<list.length; i++)
      {
         onion.add(list[i]);
      }
   }
   
}
