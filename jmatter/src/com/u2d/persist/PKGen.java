package com.u2d.persist;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PKGen
{
   public PKGenStrategy strategy();
   // TODO: add argument to strategy. e.g. sequence name for sequence.
}
