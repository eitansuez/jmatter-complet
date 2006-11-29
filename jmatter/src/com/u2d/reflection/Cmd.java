package com.u2d.reflection;

import com.u2d.ui.desktop.Positioning;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cmd
{
   public char mnemonic() default '\0';
   public boolean isSensitive() default false;
   public Positioning viewPosition() default Positioning.NEARMOUSE;
}
