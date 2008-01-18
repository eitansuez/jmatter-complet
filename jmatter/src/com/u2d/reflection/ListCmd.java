package com.u2d.reflection;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jan 18, 2008
 * Time: 9:27:27 PM
 * 
 * Marker interface.  Target methods must be static
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ListCmd
{
}
