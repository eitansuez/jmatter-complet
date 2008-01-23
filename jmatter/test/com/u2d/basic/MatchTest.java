package com.u2d.basic;
/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jan 23, 2008
 * Time: 12:15:26 AM
 */

import junit.framework.TestCase;
import com.u2d.interaction.Match;

public class MatchTest
      extends TestCase
{
   public void testSimpleMatch()
   {
      assertTrue(Match.cost("Person Contacts".toLowerCase(), "Po".toLowerCase())>=0);
   }
}