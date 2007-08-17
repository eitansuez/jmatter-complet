package com.u2d.wizard;

/**
 * @author Andres Almiray
 */
public class StepMetadata
{
   private String _description;
   private String _fieldName;
   private String _title;

   public StepMetadata( String fieldName )
   {
      this( fieldName, null, null );
   }

   public StepMetadata( String fieldName, String title )
   {
      this( fieldName, title, null );
   }

   public StepMetadata( String fieldName, String title, String description )
   {
      _fieldName = fieldName;
      _title = title;
      _description = description;
   }

   public String getDescription()
   {
      return _description;
   }

   public String getFieldName()
   {
      return _fieldName;
   }

   public String getTitle()
   {
      return _title;
   }

   public void setDescription( String description )
   {
      this._description = description;
   }

   public void setFieldName( String fieldName )
   {
      this._fieldName = fieldName;
   }

   public void setTitle( String title )
   {
      this._title = title;
   }

   public String toString()
   {
      return new StringBuffer().append( hashCode() )
            .append( "@" )
            .append( "[fieldName=" )
            .append( _fieldName )
            .append( ", title=" )
            .append( _title )
            .append( ", description=" )
            .append( _description )
            .toString();
   }
}