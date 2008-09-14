package org.jmatter.appbuilder;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.StringEO;
import com.u2d.type.atom.TextEO;
import com.u2d.type.atom.CharEO;
import com.u2d.reflection.Fld;

import javax.persistence.Entity;

@Entity
public class MemberAB extends AbstractComplexEObject
{
   public static String[] fieldOrder = {"name", "caption", "description", "mnemonic"};

   public MemberAB() { }

   private final StringEO name = new StringEO();
   public StringEO getName() { return name; }

   private final StringEO caption = new StringEO();
   @Fld(description="Leave blank unless want to override default derivation from name")
   public StringEO getCaption() { return caption; }

   private final TextEO description = new TextEO();
   @Fld(description="Used as a tooltip in GUI")
   public TextEO getDescription() { return description; }

   private final CharEO mnemonic = new CharEO();
   public CharEO getMnemonic() { return mnemonic; }
   
   public Title title() { return name.title(); }

   public static String naturalName() { return "Member"; }
}
