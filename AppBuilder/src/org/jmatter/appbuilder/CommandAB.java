package org.jmatter.appbuilder;

import com.u2d.type.atom.BooleanEO;
import com.u2d.type.atom.TextEO;
import javax.persistence.Entity;

@Entity
public class CommandAB extends MemberAB
{
   public CommandAB() { }

   private final BooleanEO sensitive = new BooleanEO();
   public BooleanEO getSensitive() { return sensitive; }

   private final TextEO body = new TextEO();
   public TextEO getBody() { return body; }

   public static String naturalName() { return "Command"; }
}
