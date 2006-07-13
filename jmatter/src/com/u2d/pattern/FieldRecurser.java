/*
 * Created on Jan 22, 2004
 */
package com.u2d.pattern;

import java.util.Iterator;
import java.util.List;
import com.u2d.element.Field;
import com.u2d.field.AggregateField;
import com.u2d.model.FieldParent;

/**
 * @author Eitan Suez
 */
public class FieldRecurser
{

   public static void recurseFields(FieldParent fp, FieldProcessor processor)
   {
      recurseFields(fp.fields(), processor);
   }
   
   public static void recurseFields(List fields, FieldProcessor processor)
   {
      Iterator itr = fields.iterator();
      Field field = null;
      while (itr.hasNext())
      {
         field = (Field) itr.next();
         processor.processField(field);
         if (field.isAggregate())
         {
            AggregateField aggregate = (AggregateField) field;
            recurseFields(aggregate.fields(), processor);
         }
      }
   }
   
}
