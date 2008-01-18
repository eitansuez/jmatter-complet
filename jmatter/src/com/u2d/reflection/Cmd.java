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
    
   public String shortcut() default "";
   
   /**
    * @return a way to override the caption/label for a field.  i prefer natural labels, 
    *   derived from the field name. 
    */
   public String label() default "";

   /**
    * @return field description.  possibly use as text for tool tips..
    */
   public String description() default "";

   public boolean sensitive() default false;
   public Positioning viewPosition() default Positioning.NEARMOUSE;

   /**
    * @return whether to disable the command's view through the duration of invocation
    */
   public boolean blocks() default false;

   /**
    * @return whether command can be exposed on lists to invoke on multiple instances
    * i.e. list.items { item -> item.@cmd }
    */
   public boolean batchable() default false;
}
