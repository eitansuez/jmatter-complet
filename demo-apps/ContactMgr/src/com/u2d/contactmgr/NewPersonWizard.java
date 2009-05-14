package com.u2d.contactmgr;

import com.u2d.wizard.details.*;
import com.u2d.view.swing.FormView;
import com.u2d.model.ComplexType;
import com.u2d.validation.ValidationNotifier;

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

   private int attempts = 0;

   class NameStep extends BasicStep
   {
      public String title() { return "Name Information"; }
      public String description() { return "Enter Person's Name"; }
      public JComponent getView()
      {
         return new FormView(pc.getName());
      }

      /*
        testing new validation hook added to wizard implementation
       */
      @Override
      public int validate(ValidationNotifier notifier)
      {
         attempts++;
         switch (attempts)
         {
            case 1:
            {
               notifier.fireValidationException("Try again.");
               return 1;
            }
            case 2:
            {
               notifier.fireValidationException("Not yet.  One more time please.");
               return 1;
            }
            default:
            {
               return 0;
            }
         }
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
