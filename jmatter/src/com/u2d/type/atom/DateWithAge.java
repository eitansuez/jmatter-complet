/*
 * Created on Feb 10, 2005
 */
package com.u2d.type.atom;

import com.u2d.model.AtomicRenderer;
import com.u2d.model.AtomicEditor;
import java.util.*;

/**
 * @author Eitan Suez
 */
public class DateWithAge extends DateEO
{
   public DateWithAge() {}
   
   public DateWithAge(Date value)
   {
      super(value);
   }
   
   public AtomicRenderer getRenderer() { return vmech().getDateWithAgeRenderer(); }
   public AtomicEditor getEditor() { return vmech().getDateWithAgeEditor(); }

}
