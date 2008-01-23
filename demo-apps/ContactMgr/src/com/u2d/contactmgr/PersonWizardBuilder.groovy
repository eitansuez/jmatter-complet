package com.u2d.contactmgr

import com.u2d.type.composite.*
import com.u2d.model.*
import com.u2d.view.swing.FormView
import com.u2d.wizard.builder.*
import com.u2d.wizard.details.Wizard

/**
 * Example and Implementation courtesy of Andres Almiray
 */
class PersonWizardBuilder
{
   static Wizard wizard()
   {
      def _name = ComplexType.forClass(Name.class).instance()
      def _address = ComplexType.forClass(USAddress.class).instance()
      def _contact = ComplexType.forClass(Contact.class).instance()

      new WizardBuilder().wizard( "New Person Wizard" )
      {
         step( title: "Name Information",
               description: "Enter Person's Name" )
         {
            new FormView(_name)
         }
         step( title: "Address Information",
               description: "Enter Person's Physical Address" )
         {
            new FormView(_address)
         }
         step( title: "Person's Contact Information", commit: true,
               description: "Please specify person's contact information" )
         {
            view { new FormView(_contact) }
            doCommit
            {
               PersonContact pc = new PersonContact()
               pc.name.setValue(_name)
               pc.contact.setValue(_contact)
               pc.contact.address.setValue(_address)
               pc.save()
            }
         }
      }
   }
}
