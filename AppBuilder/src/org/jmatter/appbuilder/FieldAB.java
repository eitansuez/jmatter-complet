package org.jmatter.appbuilder;

import com.u2d.type.atom.StringEO;
import com.u2d.type.atom.BooleanEO;
import com.u2d.reflection.Fld;
import javax.persistence.Entity;

@Entity
public class FieldAB extends MemberAB
{
   public static String[] fieldOrder = {"name", "fieldType", "composite", "indexed",
         "caption", "description", "mnemonic"};
   public static String[] tabViews = {"metadata"};

   public FieldAB() { }

   public void initialize()
   {
      composite.setValue(true);  // make the most common case the default
   }

   // for now define field type as a string
   private final StringEO fieldType = new StringEO();
   public StringEO getFieldType() { return fieldType; }

   private final BooleanEO indexed = new BooleanEO();
   @Fld(description="Cardinality.  Indexed means to many")
   public BooleanEO getIndexed() { return indexed; }

   private final BooleanEO composite = new BooleanEO();
   @Fld(description="Composite or Association?")
   public BooleanEO getComposite() { return composite; }

   private final FieldMetadata metadata = new FieldMetadata();
   public FieldMetadata getMetadata() { return metadata; }

   public boolean isCompositeField()
   {
      return getComposite().isTrue() && getIndexed().isFalse();
   }
   public boolean isAssociationField()
   {
      return getComposite().isFalse() && getIndexed().isFalse();
   }
   public boolean isRelationalListField()
   {
      return getComposite().isFalse() && getIndexed().isTrue();
   }
   public boolean isCompositeListField()
   {
      return getComposite().isTrue() && getIndexed().isTrue();
   }

   public static String naturalName() { return "Field"; }

}
