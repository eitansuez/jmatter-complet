package com.u2d.contactmgr;

import com.u2d.element.CommandInfo;
import com.u2d.model.ComplexType;
import com.u2d.reflection.Cmd;
import com.u2d.reporting.ReportFormat;
import com.u2d.reporting.Reportable;
import com.u2d.type.composite.Person;
import com.u2d.wizard.details.Wizard;
import javax.swing.table.TableModel;
import javax.persistence.Entity;
import java.util.Properties;

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
      // was:
//       return new Wizard(new NewPersonWizard());
      
      // New Alternative:
//      DomainWizard wizard = new DomainWizard(PersonContact.class,
//         new String[][]{{"name"},{"contact.address"},{"contact"}});
//      wizard.setLastPropertyCommits( true );
//      wizard.ready();
//      return new Wizard( wizard );
      
      // Third alternative, using Groovy Builder for JMatter Wizards:
      return PersonWizardBuilder.wizard();
   }
   
   
   @Cmd
   public static Reportable Report(CommandInfo cmdInfo)
   {
      return new Reportable()
      {
         public String reportName() { return "com/u2d/contactmgr/Basic.xml"; }
         public Properties properties() { return new Properties(); }
         public ReportFormat reportFormat() { return ReportFormat.PDF; }

         public TableModel tableModel()
         {
            return ComplexType.forClass(PersonContact.class).list().tableModel();
         }
      };
   }
}
