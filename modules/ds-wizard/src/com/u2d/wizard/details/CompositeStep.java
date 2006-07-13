package com.u2d.wizard.details;

import com.u2d.wizard.abstractions.Step;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 24, 2005
 * Time: 5:39:31 PM
 */
public class CompositeStep implements Step
{
   protected List _steps = new ArrayList();
   private List _flattenedSteps = new ArrayList();
   protected int _i = 0;
   protected Step _currentStep;
   protected String _compositeTitle = "";

   public CompositeStep() { this(""); }
   public CompositeStep(String compositeTitle)
   {
      _compositeTitle = compositeTitle;
   }

   public CompositeStep(List steps)
   {
      _steps = steps;
      ready();
   }

   public void addStep(Step step) { _steps.add(step); }

   public void ready()
   {
      _currentStep = (Step) _steps.get(0);
      setupFlattenedSteps();
   }
   private void setupFlattenedSteps()
   {
      Step step = null;
      for (int i=0; i<_steps.size(); i++)
      {
         step = (Step) _steps.get(i);
         if (step instanceof CompositeStep)
         {
            CompositeStep cstep = (CompositeStep) step;
            _flattenedSteps.addAll(cstep.steps());
         }
         else
         {
            _flattenedSteps.add(step);
         }
      }
   }


   public Step nextStep()
   {
      if (_currentStep instanceof CommitStep)
      {
         ((CommitStep) _currentStep).commit();
      }

      Step next = _currentStep.nextStep();
      if (next == null)
      {
         _i++;
         if (_i >= _steps.size())
         {
            _i--;
            return null;
         }
         _currentStep = (Step) _steps.get(_i);
      }
      return this;
   }

   public Step previousStep()
   {
      Step prev = _currentStep.previousStep();
      if (prev != null) return this;
      if (_i<=0) return null;
      if (_steps.get(_i-1) instanceof CommitStep) return this;

      _i--;
      _currentStep = (Step) _steps.get(_i);
      return this;
   }

   public Step currentStep()
   {
      Step current = _currentStep;
      while (!(current instanceof BasicStep))
        current = current.currentStep();
      return current;
   }

   public boolean hasNextStep()
   {
      if (_currentStep.hasNextStep()) return true;
      if (_i < (_steps.size() - 1))
         return true;
      else
         return false;
   }
   public boolean hasPreviousStep()
   {
      if (_currentStep.hasPreviousStep()) return true;
      if (_i <= 0) return false;
      Step prev = (Step) (_steps.get(_i - 1));
      if (prev.currentStep() instanceof CommitStep) return false;
      return true;
   }


   public JComponent getView() { return _currentStep.getView(); }
   public boolean viewDirty() { return _currentStep.viewDirty(); }

   public String title()
   {
      return _currentStep.title();
   }
   public String numberedTitle()
   {
      if (stepNumber() == 0)
         return _currentStep.title();
      else
         return stepNumber() + ". " + _currentStep.title();
   }


   public String description() { return _currentStep.description(); }

   public String compositeTitle() { return _compositeTitle; }
   public List steps() { return _flattenedSteps; }

   public int stepNumber()
   {
      int counter = 0;
      Step step = null;
      for (int i=0; i<=_i; i++)
      {
         step = (Step) _steps.get(i);
         if (step instanceof CompositeStep)
         {
            CompositeStep cstep = (CompositeStep) step;
            counter += cstep.stepNumber();
         }
         else
         {
            counter += 1;
         }
      }
      return counter;
   }

   public String toString() { return compositeTitle(); }

}
