/*
 * Created on Nov 17, 2003
 */
package com.u2d.type.composite;

import com.u2d.element.CommandInfo;
import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.reflection.Cmd;
import com.u2d.type.atom.StringEO;

/**
 * @author Eitan Suez
 */
public class Person extends AbstractComplexEObject implements Emailable
{
   protected final Name _name = new Name();
   protected final Contact _contact = new Contact();

   public static String[] fieldOrder = {"name", "contact"};
   public static String[] tabViews = {"contact"};
   public static final String[] flattenIntoParent = {"name"};
   
   public static String defaultSearchPath = "name.first";

   public Person() {}

   public void initialize()
   {
      _contact.initialize();
   }

   public Person(Name name)
   {
      _name.setValue(name);
      initialize();
   }

   public Person(Name name, Contact contact)
   {
      this(name);
      _contact.setValue(contact);
   }

   public Name getName() { return _name; }
   public Contact getContact() { return _contact; }
   public String emailAddress() { return _contact.emailAddress(); }

   
   public Title title()
   {
      return _name.title().append(",", _contact);
   }

   public String nameAndAddress()
   {
      String name = getName().toString();
      String address = getContact().getAddress().toString();
      return name + "\n" + address;
   }

   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (obj == this) return true;
      if (!(obj instanceof Person)) return false;
      Person p = (Person) obj;
      return _name.equals(p.getName()) && _contact.equals(p.getContact());
   }

   public int hashCode()
   {
      return _name.hashCode() + 31 * _contact.hashCode();
   }
   
   @Cmd(mnemonic='a', batchable=true)
   public void Email(CommandInfo cmdInfo)
   {
      EmailMessage msg = new EmailMessage(this, new StringEO());
      msg.OpenInEmailApp(null);
   }
   
}
