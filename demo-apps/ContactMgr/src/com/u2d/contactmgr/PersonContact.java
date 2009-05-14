package com.u2d.contactmgr;

import com.u2d.element.CommandInfo;
import com.u2d.model.ComplexType;
import com.u2d.reflection.Cmd;
import com.u2d.type.composite.Person;
import com.u2d.wizard.details.Wizard;
import javax.persistence.Entity;

@Entity
public class PersonContact extends Person
{
   // An example of how to override the default command for a single type..
   static
   {
      ComplexType.forClass(PersonContact.class).setDefaultCommandName("NewPersonWizard");
   }
   
   @Cmd(shortcut="control Z")
   public static Wizard NewPersonWizard(CommandInfo cmdInfo)
   {
      // basic:
       return new Wizard(new NewPersonWizard());
      
      // New Alternative:
//      DomainWizard wizard = new DomainWizard(PersonContact.class,
//         new String[][]{{"name"},{"contact.address"},{"contact"}});
//      wizard.setLastPropertyCommits( true );
//      wizard.ready();
//      return new Wizard( wizard );
      
      // Third alternative, using Groovy Builder for JMatter Wizards:
//      return PersonWizardBuilder.wizard();
   }
   
   
}
