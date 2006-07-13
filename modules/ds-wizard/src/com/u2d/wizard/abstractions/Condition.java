package com.u2d.wizard.abstractions;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 25, 2005
 * Time: 9:08:57 AM
 */
public interface Condition
{
   public boolean evaluate();

   public static Condition TRUE = new Condition() { public boolean evaluate() { return true; } };
   public static Condition FALSE = new Condition() { public boolean evaluate() { return false; } };
}
