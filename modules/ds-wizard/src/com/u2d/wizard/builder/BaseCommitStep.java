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

}
