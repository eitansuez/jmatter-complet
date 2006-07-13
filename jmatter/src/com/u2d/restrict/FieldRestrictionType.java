/*
 * Created on Dec 29, 2004
 */
package com.u2d.restrict;

import java.util.*;
import com.u2d.type.atom.ChoiceEO;

/**
 * @author Eitan Suez
 */
public class FieldRestrictionType extends ChoiceEO
{
   public FieldRestrictionType() {}
   public FieldRestrictionType(String value)
   {
      setValue(value);
   }
   
   private static Set STATUS_OPTIONS = new HashSet();
   static
   {
      STATUS_OPTIONS.add(FieldRestriction.NONE);
      STATUS_OPTIONS.add(FieldRestriction.READ_ONLY);
      STATUS_OPTIONS.add(FieldRestriction.HIDDEN);
   }

   public Collection entries() { return STATUS_OPTIONS; }
}
