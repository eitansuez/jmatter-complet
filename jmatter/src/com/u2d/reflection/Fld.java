package com.u2d.reflection;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: May 11, 2006
 * Time: 4:54:42 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Fld
{
   /**
    * @return optional mnemonic (for the gui)
    */
   public char mnemonic() default '\0';

   /**
    * @return a way to override the caption/label for a field.  i prefer natural labels, 
    *   derived from the field name. 
    */
   public String label() default "";

   /**
    * @return field description.  possibly use as text for tool tips..
    */
   public String description() default "";

   /**
    * @return a way to override the database column name for a field
    */
   public String colname() default "";
   
   /**
    * @return a way to control the size of the corresponding database table field
    */
   public int colsize() default 0;

   /**
    * @return a way to control the size of the text field used for editing the field in the ui
    */
   public int displaysize() default 0;

   /**
    * @return applicable to TimeEO and DateEO.  Specified as a simpledateformat.
    *   Goveners both how times and dates are displayed and parsed.
    */
   public String format() default "";

   /**
    * @return whether field should be persisted.  most of the time this is true.
    *  for derived/calculated fields, this should be false.
    */
   public boolean persist() default true;
   
   public boolean hidden() default false;

   /**
    * @return A reference to an enum type used to restrict list of valid values for a field
    */
   public Class optionsEnum() default Class.class;
}
