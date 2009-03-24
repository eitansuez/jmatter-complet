package com.u2d.validation;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: May 15, 2008
 * Time: 4:50:29 PM
 */
public abstract class BaseRule implements Rule
{
   public boolean pass() { return !fail(); }

   public Severity severity() { return Severity.DEFAULT; }

   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (!(obj instanceof BaseRule)) return false;
      Rule rule = (Rule) obj;
      String path = targetObject().field().fullPath();
      return path.equals(rule.targetObject().field().fullPath());
   }

   public int hashCode()
   {
      return targetObject().field().fullPath().hashCode() * 17;
   }
}
