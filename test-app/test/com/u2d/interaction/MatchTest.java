package com.u2d.interaction;

import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jan 22, 2008
 * Time: 10:24:07 PM
 */
public class MatchTest extends TestCase
{
   public void testSimpleMatch()
   {
      int cost = Match.cost("Persons", "Per");
      assertEquals(cost, 0);
      
      cost = Match.cost("Persons", "Po");
      assertEquals(cost, 3);
   }
   
   public void testInstructionsMatch()
   {
      Instruction i = new Instruction();
      i.matchTargetText("Po");
      System.out.println(i.getTargetMatches());
   }
}
