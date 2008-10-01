package com.u2d.reflection;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Feb 1, 2007
 * Time: 4:42:36 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface IdxFld
{
   boolean ordered() default false;
   boolean ownschildren() default false;
}
