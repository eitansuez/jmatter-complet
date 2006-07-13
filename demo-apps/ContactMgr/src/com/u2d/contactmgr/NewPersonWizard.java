package com.u2d.contactmgr;

import com.u2d.wizard.details.*;
import com.u2d.type.composite.*;
import com.u2d.model.ComplexType;
import com.u2d.view.swing.FormView;
import javax.swing.*;

public class NewPersonWizard extends CompositeStep
{
   private Name _name;
   private USAddress _address;
   private Contact _contact;

   public NewPersonWizard()
   {
      super("New Person Wizard");
      createObjects();
      setupSteps();
   }

   private void createObjects()
   {
      _name = (Name) ComplexType.forClass(Name.class).instance();
      _address = (USAddress) ComplexType.forClass(USAddress.class).instance();
      _contact = (Contact) ComplexType.forClass(Contact.class).instance();
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
         return new FormView(_name);
      }
   }
   class AddressStep extends BasicStep
   {
      public String title() { return "Address Information"; }
      public String description() { return "Enter Person's Physical Address"; }
      public JComponent getView()
      {
         return new FormView(_address);
      }
   }
   class ContactStep extends CommitStep
   {
      public String title() { return "Person's Contact Information"; }
      public String description() { return "Please specify person's contact information"; }
      public JComponent getView()
      {
         return new FormView(_contact);
      }

      public void commit()
      {
         PersonContact pc = new PersonContact();
         pc.getName().setValue(_name);
         pc.getContact().setValue(_contact);
         pc.getContact().getAddress().setValue(_address);
         pc.save();
      }
   }

}
