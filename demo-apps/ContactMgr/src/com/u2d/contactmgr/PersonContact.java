package com.u2d.contactmgr;

import com.u2d.type.composite.Person;
import com.u2d.wizard.details.Wizard;
import com.u2d.element.CommandInfo;
import com.u2d.reporting.Reportable;
import com.u2d.reporting.ReportFormat;
import com.u2d.model.ComplexType;
import com.u2d.reflection.Cmd;
import javax.swing.table.TableModel;
import java.util.Properties;

public class PersonContact extends Person
{
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
