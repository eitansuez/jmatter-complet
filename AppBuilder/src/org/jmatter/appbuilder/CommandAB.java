package org.jmatter.appbuilder;

import com.u2d.type.atom.BooleanEO;
import com.u2d.type.atom.TextEO;
import com.u2d.type.atom.StringEO;
import com.u2d.list.RelationalList;
import com.u2d.reflection.IdxFld;

import javax.persistence.Entity;

@Entity
public class CommandAB extends MemberAB
{
   public static String[] fieldOrder = {"name", "arguments", "returnType", "body",
         "caption", "description", "mnemonic",
         "sensitive" };
   
   public CommandAB() { }

   private final StringEO returnType = new StringEO();
   public StringEO getReturnType() { return returnType; }

   private final RelationalList arguments = new RelationalList(Argument.class);
   public static Class argumentsType = Argument.class;
   @IdxFld(ordered=true)
   public RelationalList getArguments() { return arguments; }

   private final TextEO body = new TextEO();
   public TextEO getBody() { return body; }

   private final BooleanEO sensitive = new BooleanEO();
   public BooleanEO getSensitive() { return sensitive; }

   public static String naturalName() { return "Command"; }
}
