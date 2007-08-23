package com.u2d.wizard.builder;

import groovy.lang.Closure;
import javax.swing.JComponent;
import com.u2d.wizard.details.BasicStep;

public class BaseStep extends BasicStep
{
   private String _description;
   private String _title;
   private Closure _view;
   private Closure _viewDirty;

   public BaseStep( String title, String description )
   {
      _title = title;
      _description = description;
   }

   public String title() { return _title; }
   public String description() { return _description; }

   public JComponent getView()
   {
      Object viewValue = _view.call();
      if( viewValue instanceof JComponent )
      {
         return (JComponent) viewValue;
      }
      throw new RuntimeException( "View is not a JComponent" );
   }

   public Closure getViewClosure() { return _view; }
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