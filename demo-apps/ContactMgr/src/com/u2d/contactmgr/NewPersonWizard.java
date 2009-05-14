package com.u2d.contactmgr;

import com.u2d.wizard.details.*;
import com.u2d.view.swing.FormView;
import com.u2d.model.ComplexType;

import javax.swing.*;

public class NewPersonWizard extends CompositeStep
{
   private PersonContact pc;

   public NewPersonWizard()
   {
      super("New Person Wizard");
      pc = (PersonContact) ComplexType.forClass(PersonContact.class).instance();
      setupSteps();
   }

   private void setupSteps()
   {
      NameStep nameStep = new NameStep();
      AddressStep addrStep = new AddressStep();
      ContactStep contactStep = new ContactStep();

      addStep(nameStep);
      addStep(addrStep);
      addStep(contactStep);
      ready();
   }

   // ====================================================================

   class NameStep extends BasicStep
   {
      public String title() { return "Name Information"; }
      public String description() { return "Enter Person's Name"; }
      public JComponent getView()
      {
         return new FormView(pc.getName());
      }
   }
   class AddressStep extends BasicStep
   {
      public String title() { return "Address Information"; }
      public String description() { return "Enter Person's Physical Address"; }
      public JComponent getView()
      {
         return new FormView(pc.getContact().getAddress());
      }
   }
   class ContactStep extends CommitStep
   {
      public String title() { return "Person's Contact Information"; }
      public String description() { return "Please specify person's contact information"; }
      public JComponent getView()
      {
         return new FormView(pc.getContact());
      }

      public void commit()
      {
         pc.save();
      }
   }

}
