package com.u2d.wizard.builder;

import groovy.lang.Closure;

import com.u2d.wizard.abstractions.Condition;
import com.u2d.wizard.abstractions.Step;
import com.u2d.wizard.details.CommitStep;
import com.u2d.wizard.details.CompositeStep;
import com.u2d.wizard.details.ConditionStep;

public class BaseConditionalStep extends CompositeStep
{
   private Condition _condition;
   private Step _firstStep;
   private Step _secondStep;

   public void addStep( Step step )
   {
      if( _firstStep == null )
      {
         if( !(step instanceof CommitStep) )
         {
            _firstStep = step;
         }
         else
         {
            throw new IllegalStateException( "First step can't be commit" );
         }
      }
      else if( _secondStep == null )
      {
         _secondStep = step;
      }
      else
      {
         throw new IllegalStateException( "Can't add more than two steps to a conditonalStep" );
      }
   }

   public ConditionStep getConditionStep()
   {
      return new ConditionStep( _firstStep, _secondStep, getCondition() );
   }

   public Condition setCondition( final Closure closure )
   {
      return new Condition()
      {
         public boolean evaluate()
         {
            Object value = closure.call();
            if( value instanceof Boolean )
            {
               return ((Boolean) value).booleanValue();
            }
            throw new IllegalStateException( "The condition did not evaluate to boolean" );
         }
      };
   }

   public Step getFirstStep() { return _firstStep; }
   public void setFirstStep( Step step ) { _firstStep = step; }

   public Step getSecondStep() { return _secondStep; }
   public void setSecondStep( Step step ) { _secondStep = step; }

   public Condition getCondition()
   {
      if( _condition == null )
      {
         _condition = Condition.TRUE;
      }
      return _condition;
   }
   public void setCondition( Condition condition ) { _condition = condition; }

}
