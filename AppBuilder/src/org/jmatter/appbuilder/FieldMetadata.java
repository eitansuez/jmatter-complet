package org.jmatter.appbuilder;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.BooleanEO;
import com.u2d.type.atom.StringEO;
import com.u2d.type.atom.IntEO;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Sep 14, 2008
 * Time: 4:06:20 PM
 */
public class FieldMetadata extends AbstractComplexEObject
{
   public static String[] fieldOrder = {"identity", "readOnly", "hidden", "persist", "format",
      "displaysize", "colsize", "colname"};
   
   public FieldMetadata() {}

   public void initialize()
   {
      persist.setValue(true);  // default
   }

   public boolean needToWriteAnnotation()
   {
      return getHidden().isTrue() || getPersist().isFalse() ||
            !getFormat().isEmpty() || getDisplaysize().intValue() != 0 ||
            getColsize().intValue() != 0 || !getColname().isEmpty();
   }
   
   private final BooleanEO identity = new BooleanEO();
   public BooleanEO getIdentity() { return identity; }

   private final BooleanEO readOnly = new BooleanEO();
   public BooleanEO getReadOnly() { return readOnly; }

   private final BooleanEO hidden = new BooleanEO();
   public BooleanEO getHidden() { return hidden; }

   private final BooleanEO persist = new BooleanEO();
   public BooleanEO getPersist() { return persist; }

   private final StringEO format = new StringEO();
   public StringEO getFormat() { return format; }

   private final IntEO displaysize = new IntEO();
   public IntEO getDisplaysize() { return displaysize; }

   private final IntEO colsize = new IntEO();
   public IntEO getColsize() { return colsize; }

   private final StringEO colname = new StringEO();
   public StringEO getColname() { return colname; }

   public Title title()
   {
      return new Title("Field Metadata");
   }
}
