package com.u2d.reflection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

/**
 * Credit to Michadraelian for contributing the option to specify
 * whether to enable default action.
 *
 * Documentation: this annotation is to specify whether to enable the
 *  double-click gesture in the user interface.  It doesn't control/govern
 *  whether the underlying command is enabled.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultAction
{
   boolean enabled() default true;
}
