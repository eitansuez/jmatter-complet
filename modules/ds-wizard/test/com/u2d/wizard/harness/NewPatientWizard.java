package com.u2d.wizard.harness;

import com.u2d.wizard.abstractions.Step;
import com.u2d.wizard.abstractions.Condition;
import com.u2d.wizard.details.*;
//import com.u2d.wizard.ui.WizardPane;
import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 26, 2005
 * Time: 10:49:46 AM
 */
public class NewPatientWizard extends CompositeStep
{
   public NewPatientWizard()
   {
      PatientInfoStep patientInfo = new PatientInfoStep();
      EmergencyContactStep emergencyInfo = new EmergencyContactStep();

      ReferringPhysicianOptionStep hasRefPhysician = new ReferringPhysicianOptionStep();
      ReferringPhysicianStep refPhysician = new ReferringPhysicianStep();

      ConditionStep refPhysCondStep = new ConditionStep(hasRefPhysician, refPhysician, Condition.FALSE);

      Step insuranceStep = new InsuranceStep();
      Step secondaryInsuranceStep = new InsuranceStep();

      SecondaryInsuredOptionStep hasSecondaryInsurance = new SecondaryInsuredOptionStep();
      ConditionStep secondaryInsCondStep = new ConditionStep(hasSecondaryInsurance,
                                                             secondaryInsuranceStep,
                                                             Condition.FALSE);

      addStep(patientInfo);
      addStep(emergencyInfo);
      addStep(refPhysCondStep);
      addStep(insuranceStep);
      addStep(secondaryInsCondStep);
      ready();
   }

   public Wizard wizard()
   {
      return new Wizard(this);
   }

//   public static void main(String[] args)
//   {
//      JPanel wp = new WizardPane(new NewPatientWizard().wizard());
//      JFrame f = new JFrame();
//      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//      f.getContentPane().add(wp, BorderLayout.CENTER);
//      f.setBounds(100, 100, 400, 500);
//      f.setVisible(true);
//   }


   // ====================================================================

   class PatientInfoStep extends BasicStep
   {
      public String title() { return "Patient Information"; }
      public String description() { return "Enter Patient's Basic Information"; }
      public JComponent getView()
      {
         return new JPanel(); // stubbed out for now
      }
   }
   class EmergencyContactStep extends BasicStep
   {
      public String title() { return "Emergency Contact Information"; }
      public String description() { return "Enter Emergency Contact's Information"; }
      public JComponent getView()
      {
         return new JPanel(); // stubbed out for now
      }
   }

   class ReferringPhysicianOptionStep extends BasicStep
   {
      public String title() { return "Referring Physician?"; }
      public String description() { return "Were you referred to the clinic by a physician?"; }
      public JComponent getView()
      {
         return new JPanel(); // stubbed out for now
      }
   }

   class ReferringPhysicianStep extends BasicStep
   {
      public String title() { return "Referring Physician"; }
      public String description() { return "Please enter referring physician information"; }
      public JComponent getView()
      {
         return new JPanel(); // stubbed out for now
      }
   }

   class InsuranceStep extends CompositeStep
   {
      public InsuranceStep()
      {
         PatientInsuredRelationStep insuranceRelation = new PatientInsuredRelationStep();
         InsuredPersonInfoStep insurancePersonInfo = new InsuredPersonInfoStep();
         Condition relationIsSelf = Condition.TRUE;
         ConditionStep isSelfCondStep = new ConditionStep(insuranceRelation, insurancePersonInfo,
                                                          new NotCondition(relationIsSelf));

         InsuranceInfoStep insuranceInfo = new InsuranceInfoStep();

         addStep(isSelfCondStep);
         addStep(insuranceInfo);
         ready();
      }

      class PatientInsuredRelationStep extends BasicStep
      {
         public String title() { return "Relation to Patient"; }
         public String description() { return "Please specify insured's relationship to patient"; }
         public JComponent getView()
         {
            return new JPanel(); // stubbed out for now
         }
      }

      class InsuredPersonInfoStep extends BasicStep
      {
         public String title() { return "Insured Person Information"; }
         public String description() { return "Please enter information for the insured person"; }
         public JComponent getView()
         {
            return new JPanel(); // stubbed out for now
         }
      }

      class InsuranceInfoStep extends BasicStep
      {
         public String title() { return "Insurance Information"; }
         public String description() { return "Please enter insurance information for the insured person"; }
         public JComponent getView()
         {
            return new JPanel(); // stubbed out for now
         }
      }

   }


   class SecondaryInsuredOptionStep extends BasicStep
   {
      public String title() { return "Secondary Insured?"; }
      public String description()
      {
         return "Is there secondary insurance information for [patientname]?"; // +
         //_patient.name().toString() + "?";
      }
      public JComponent getView()
      {
         return new JPanel(); // stubbed out for now
      }
   }

}
