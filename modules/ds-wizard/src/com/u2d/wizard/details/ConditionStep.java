package com.u2d.wizard.details;

import com.u2d.wizard.abstractions.Condition;
import com.u2d.wizard.abstractions.Step;
import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 24, 2005
 * Time: 6:04:54 PM
 */
public class ConditionStep implements Step
{
   private Step _firstStep, _secondStep, _elseStep;
   private Condition _condition;
   private Step _currentStep;

   protected ConditionStep() {}

   public ConditionStep(Step firstStep, Step secondStep, Condition condition)
   {
      this(firstStep, secondStep, condition, null);
   }

   public ConditionStep(Step firstStep, Step secondStep, Condition condition, Step elseStep)
   {
      init(firstStep, secondStep, condition, elseStep);
   }
   
   protected void init(Step firstStep, Step secondStep, Condition condition, Step elseStep)
   {
      _firstStep = new FirstStepState(firstStep);
      _secondStep = new SecondStepState(secondStep);
      if (elseStep != null)
      {
         _elseStep = new ElseStepState(elseStep);
      }
      _condition = condition;
      _currentStep = _firstStep;
   }

   public Step nextStep()    { return _currentStep.nextStep(); }
   public Step previousStep() { return _currentStep.previousStep(); }

   public Step currentStep() { return _currentStep.currentStep(); }

   public boolean hasNextStep() { return _currentStep.hasNextStep(); }
   public boolean hasPreviousStep() { return _currentStep.hasPreviousStep(); }

   public JComponent getView() { return _currentStep.getView(); }
   public boolean viewDirty() { return _currentStep.viewDirty(); }
   
   public String title() { return _currentStep.title(); }
   public String description() { return _currentStep.description(); }

   /// ==================================

   class FirstStepState
         implements Step
   {
      Step _step;
      FirstStepState(Step step) { _step = step; }

      public Step nextStep()
      {
         if (_step instanceof CommitStep)
         {
            ((CommitStep) _step).commit();
         }

         Step next = _step.nextStep();
         if (next == null)
         {
            boolean conditionResult = _condition.evaluate();
            //System.out.println("condition result: "+conditionResult);
            if (conditionResult)
            {
               _currentStep = _secondStep;
               return ConditionStep.this;
            }
            else
            {
               if (_elseStep != null)
               {
                  _currentStep = _elseStep;
                  return ConditionStep.this;
               }
            }
         }
         return null;
      }

      public Step currentStep() { return _step.currentStep(); }

      public Step previousStep()
      {
         Step prev = _step.previousStep();
         return (prev == null) ? null : ConditionStep.this;
      }

      public boolean hasNextStep()
      {
         if (_step.hasNextStep()) return true;
         boolean conditionResult = _condition.evaluate();
         if (conditionResult)
            return true;
         else
            return _secondStep.hasNextStep();
      }
      public boolean hasPreviousStep()
      {
         if (_step.hasPreviousStep()) return true;
         return false;
      }

      public String title() { return _step.title(); }
      public String description() { return _step.description(); }
      
      public JComponent getView() { return _step.getView(); }
      public boolean viewDirty() { return _step.viewDirty(); }
   }

   /// ==================================

   class SecondStepState
         implements Step
   {
      Step _step;
      SecondStepState(Step step) { _step = step; }

      public Step nextStep()
      {
         if (_step instanceof CommitStep)
         {
            ((CommitStep) _step).commit();
         }
         Step next = _step.nextStep();
         return (next == null) ? null : ConditionStep.this;
      }

      public Step previousStep()
      {
         Step prev = _step.previousStep();
         if (prev instanceof CommitStep) return ConditionStep.this;
         if (prev == null)
         {
            _currentStep = _firstStep;
         }
         return ConditionStep.this;
      }

      public Step currentStep() { return _step.currentStep(); }

      public boolean hasNextStep()
      {
         if (_step.hasNextStep()) return true;
         return false;
      }

      public boolean hasPreviousStep()
      {
         return true;
      }

      public String title() { return _step.title(); }
      public String description() { return _step.description(); }

      public JComponent getView() { return _step.getView(); }
      public boolean viewDirty() { return _step.viewDirty(); }
   }

   /// ==================================

   class ElseStepState
         implements Step
   {
      Step _step;
      ElseStepState(Step step) { _step = step; }

      public Step nextStep()
      {
         if (_step instanceof CommitStep)
         {
            ((CommitStep) _step).commit();
         }
         Step next = _step.nextStep();
         return (next == null) ? null : ConditionStep.this;
      }

      public Step previousStep()
      {
         Step prev = _step.previousStep();
         if (prev instanceof CommitStep) return ConditionStep.this;
         if (prev == null)
         {
            _currentStep = _firstStep;
         }
         return ConditionStep.this;
      }

      public Step currentStep() { return _step.currentStep(); }

      public boolean hasNextStep()
      {
         if (_step.hasNextStep()) return true;
         return false;
      }

      public boolean hasPreviousStep()
      {
         return true;
      }

      public String title() { return _step.title(); }
      public String description() { return _step.description(); }

      public JComponent getView() { return _step.getView(); }
      public boolean viewDirty() { return _step.viewDirty(); }
   }

}
