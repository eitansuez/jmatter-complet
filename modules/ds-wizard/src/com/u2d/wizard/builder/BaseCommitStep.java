package com.u2d.wizard.builder;

import groovy.lang.Closure;
import javax.swing.JComponent;
import com.u2d.wizard.details.CommitStep;

public class BaseCommitStep extends CommitStep
{
   private Closure _commit;
   private String _title;
   private String _description;
   private Closure _view;
   private Closure _viewDirty;

   public BaseCommitStep( String title, String description )
   {
      _title = title;
      _description = description;
   }

   public void commit() { _commit.call(); }
   public Closure getCommitClosure() { return _commit; }
   public void setCommit( Closure commit ) { _commit = commit; }

   public String title() { return _title; }
   public String description() { return _description; }

   public Closure getViewClosure() { return _view; }
   public JComponent getView()
   {
      Object viewValue = _view.call();
      if( viewValue instanceof JComponent )
      {
         return (JComponent) viewValue;
      }
      throw new RuntimeException( "View is not a JComponent" );
   }
   public void setView( Closure view ) { _view = view; }

   public Closure getViewDirtyClosure() { return _viewDirty; }
   public void setViewDirty( Closure viewDirty ) { _viewDirty = viewDirty; }
   public boolean viewDirty()
   {
      if( _viewDirty != null )
      {
         Object dirty = _viewDirty.call();
         if( dirty instanceof Boolean )
         {
            return ((Boolean)dirty).booleanValue();
         }
         else
         {
            // did not return a boolean or BooleanEO
            // no further processing, will return false
         }
      }
      return false;
   }
}