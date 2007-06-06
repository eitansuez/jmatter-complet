package com.u2d.contactmgr;

import com.u2d.type.composite.Person;
import com.u2d.wizard.details.Wizard;
import com.u2d.element.CommandInfo;
import com.u2d.reporting.Reportable;
import com.u2d.reporting.ReportFormat;
import com.u2d.model.ComplexType;
import com.u2d.reflection.Cmd;
import com.u2d.persist.Persist;
import javax.swing.table.TableModel;
import java.util.Properties;

@Persist
public class PersonContact extends Person
{
   // an example of how to override the default command for a single type..
   static
   {
      ComplexType.forClass(PersonContact.class).setDefaultCommandName("NewPersonWizard");
   }
   
   @Cmd
   public static Wizard NewPersonWizard(CommandInfo cmdInfo)
   {
      return new Wizard(new NewPersonWizard());
   }

   @Cmd
   public static Reportable Report(CommandInfo cmdInfo)
   {
      return new Reportable()
      {
         public String reportName() { return "/com/u2d/contactmgr/Basic.xml"; }
         public Properties properties() { return new Properties(); }
         public ReportFormat reportFormat() { return ReportFormat.PDF; }

         public TableModel tableModel()
         {
            return ComplexType.forClass(PersonContact.class).list().tableModel();
         }
      };
   }
}
