package com.u2d.wizard.builder;

import com.u2d.wizard.details.CompositeStep;

public class BaseCompositeStep extends CompositeStep
{
   private String _title;
   private String _description;

   public BaseCompositeStep()
   {
      this( null, null );
   }

   public BaseCompositeStep( String title, String description )
   {
      _title = title;
      _description = description;
   }

   public String description()
   {
      return _description != null ? _description : super.description();
   }

   public String title()
   {
      return _title != null ? _title : super.title();
   }
}
