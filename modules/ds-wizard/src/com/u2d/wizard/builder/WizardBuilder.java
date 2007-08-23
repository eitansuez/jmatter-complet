package com.u2d.wizard.builder;

import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.runtime.InvokerHelper;
import com.u2d.wizard.abstractions.Condition;
import com.u2d.wizard.abstractions.Step;
import com.u2d.wizard.details.CompositeStep;
import com.u2d.wizard.details.Wizard;

public class WizardBuilder extends GroovyObjectSupport
{
   private boolean commitStepGiven;
   private Stack<Step> stepStack = new Stack<Step>();

   public Step commit( Map properties, Closure closure )
   {
      Step top = stepStack.peek();
      if( !(top instanceof CompositeStep) )
      {
         throw new IllegalStateException( "Can't nest commit() here" );
      }
      if( commitStepGiven )
      {
         throw new IllegalStateException( "Only one commit step is allowed" );
      }
      commitStepGiven = true;
      String[] props = getTitleAndDescription( properties );
      Step step = new BaseCommitStep( props[0], props[1] );
      nextNode( step, closure );
      ((CompositeStep) top).addStep( step );

      return step;
   }

   public Condition condition( Closure closure )
   {
      Step top = stepStack.peek();
      if( !(top instanceof BaseConditionalStep) )
      {
         throw new IllegalStateException( "Parent step is not conditionalStep" );
      }
      return ((BaseConditionalStep) top).setCondition( closure );
   }

   public Step conditionalStep( Closure closure )
   {
      return conditionalStep( null, closure );
   }

   public Step conditionalStep( Map properties, Closure closure )
   {
      Step top = stepStack.peek();
      if( !(top instanceof CompositeStep) )
      {
         throw new IllegalStateException( "Can't nest step() here" );
      }

      BaseConditionalStep step = new BaseConditionalStep();
      if( properties != null )
      {
         for( Iterator iter = properties.entrySet()
               .iterator(); iter.hasNext(); )
         {
            Map.Entry entry = (Map.Entry) iter.next();
            String property = entry.getKey()
                  .toString();
            Object value = entry.getValue();
            InvokerHelper.setProperty( step, property, value );
         }
      }
      nextNode( step, closure );
      if( top instanceof BaseCompositeStep )
      {
         ((BaseCompositeStep) top).addStep( step.getConditionStep() );
      }
      else
      {
         ((CompositeStep) top).addStep( step.getConditionStep() );
      }

      return step.getConditionStep();
   }

   public Closure doCommit( Closure closure )
   {
      Step top = stepStack.peek();
      if( !(top instanceof BaseCommitStep) )
      {
         throw new IllegalStateException( "Parent step is not commit" );
      }
      ((BaseCommitStep) top).setCommit( closure );
      return closure;
   }

   public Step multiStep( Closure closure )
   {
      return multiStep( null, closure );
   }

   public Step multiStep( Map properties, Closure closure )
   {
      Step top = stepStack.peek();
      if( !(top instanceof CompositeStep) )
      {
         throw new IllegalStateException( "Can't nest step() here" );
      }

      BaseCompositeStep step = null;
      if( properties != null )
      {
         String title = (String) properties.get( "title" );
         String description = (String) properties.get( "description" );
         title = StringUtils.isBlank( title ) ? null : title;
         description = StringUtils.isBlank( description ) ? null : description;
         step = new BaseCompositeStep( title, description );
      }
      else
      {
         step = new BaseCompositeStep();
      }
      nextNode( step, closure );
      ((CompositeStep) top).addStep( step );
      step.ready();

      return step;
   }

   public Step step( Map properties, Closure closure )
   {
      Boolean commit = (Boolean) properties.get( "commit" );
      if( commit != null && commit.booleanValue() )
      {
         return commit( properties, closure );
      }

      Step top = stepStack.peek();
      if( !(top instanceof CompositeStep) )
      {
         throw new IllegalStateException( "Can't nest step() here" );
      }

      String[] props = getTitleAndDescription( properties );
      BaseStep step = new BaseStep( props[0], props[1] );
      step.setView( closure );
      ((CompositeStep) top).addStep( step );

      return step;
   }

   public Closure view( Closure closure )
   {
      Step top = stepStack.peek();
      if( !(top instanceof BaseCommitStep) )
      {
         throw new IllegalStateException( "Parent step is not commit" );
      }
      ((BaseCommitStep) top).setView( closure );
      return closure;
   }

   public Closure viewDirty( Closure closure )
   {
      Step top = stepStack.peek();
      if( top instanceof BaseCommitStep )
      {
         ((BaseCommitStep) top).setViewDirty( closure );
         return closure;
      }
      else if( top instanceof BaseStep )
      {
         ((BaseStep) top).setViewDirty( closure );
         return closure;
      }
      throw new IllegalStateException( "Can't assign viewDirty() here" );
   }

   public Wizard wizard( String title, Closure closure )
   {
      if( !stepStack.isEmpty() )
      {
         throw new IllegalStateException( "Can't nest wizard()" );
      }

      CompositeStep step = new CompositeStep( title );
      nextNode( step, closure );
      step.ready();

      return new Wizard( step );
   }

   protected void setClosureDelegate( Closure closure, Object node )
   {
      closure.setDelegate( this );
   }

   private String[] getTitleAndDescription( Map properties )
   {
      String title = (String) properties.get( "title" );
      String description = (String) properties.get( "description" );
      if( StringUtils.isBlank( title ) )
      {
         throw new IllegalArgumentException( "You must provide a value for 'title'" );
      }
      if( StringUtils.isBlank( description ) )
      {
         throw new IllegalArgumentException( "You must provide a value for 'description'" );
      }
      return new String[] { title, description };
   }

   private void nextNode( Step step, Closure closure )
   {
      stepStack.push( step );
      setClosureDelegate( closure, step );
      closure.call();
      stepStack.pop();
   }
}
