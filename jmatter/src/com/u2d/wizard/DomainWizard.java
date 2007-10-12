package com.u2d.wizard;

import com.u2d.app.Context;
import com.u2d.element.Field;
import com.u2d.model.AtomicEObject;
import com.u2d.model.ComplexEObject;
import com.u2d.model.ComplexType;
import com.u2d.model.EObject;
import com.u2d.type.atom.StringEO;
import com.u2d.wizard.details.BasicStep;
import com.u2d.wizard.details.CommitStep;
import com.u2d.wizard.details.CompositeStep;
import org.springframework.context.NoSuchMessageException;
import javax.swing.*;
import java.util.*;

/**
 * See illustration of use of DomainWizard in ContactMgr demo app.
 * 
 * @author Andres Almiray
 */
public class DomainWizard extends CompositeStep
{
   private Class _domainClass;
   private ComplexEObject _domainInstance;
   private Map<String, EObject> _fieldValues = new TreeMap<String, EObject>();
   private boolean _lastPropertyCommits;
   private List<StepMetadata> _steps;

   public DomainWizard( Class domainClass )
   {
      this( domainClass, null, (List<StepMetadata>) null );
   }

   public DomainWizard( Class domainClass, String title )
   {
      this( domainClass, title, (List<StepMetadata>) null );
   }

   public DomainWizard( Class domainClass, String title, String[][] fields )
   {
      super( title );
      _domainClass = domainClass;
      _steps = transformSteps( fields );
   }

   public DomainWizard( Class domainClass, String[][] fields )
   {
      this( domainClass, null, fields );
   }

   private DomainWizard( Class domainClass, String title, List<StepMetadata> steps )
   {
      super( title );
      _domainClass = domainClass;
      _steps = steps != null ? steps : createStepMetadata();
   }

   public String compositeTitle()
   {
      if( _compositeTitle == null )
      {
         _compositeTitle = getResourceProperty( "title" );
         if( _compositeTitle == null )
         {
            _compositeTitle = "<Wizard>";
         }
      }
      return super.compositeTitle();
   }

   protected Class getDomainClass() { return _domainClass; }
   protected ComplexEObject getDomainInstance() { return _domainInstance; }

   public boolean isLastPropertyCommits() { return _lastPropertyCommits; }
   public void setLastPropertyCommits( boolean lastPropertyCommits )
   {
      _lastPropertyCommits = lastPropertyCommits;
   }

   public void ready()
   {
      createSteps();
      super.ready();
   }

   private List<StepMetadata> createStepMetadata()
   {
      List<StepMetadata> steps = new ArrayList<StepMetadata>();
      _domainInstance = (ComplexEObject) ComplexType.forClass( _domainClass )
            .instance();
      List<Field> fields = _domainInstance.childFields();
      for( Field field : fields )
      {
         if( field.hidden() || "createdOn".equals( field.name() ) || "status".equals( field.name() ) )
            continue;
         steps.add( new StepMetadata( field.name() ) );
      }
      return steps;
   }

   private List<StepMetadata> transformSteps( String[][] fields )
   {
      List<StepMetadata> steps = new ArrayList<StepMetadata>();
      for( String[] fieldData : fields )
      {
         StepMetadata step = new StepMetadata( fieldData[0] );
         switch (fieldData.length)
         {
            case 3:
               step.setDescription( fieldData[2] );
            case 2:
               step.setTitle( fieldData[1] );
         }
         steps.add( step );
      }
      return steps;
   }
   
   private void createSteps()
   {
      _domainInstance = (ComplexEObject) ComplexType.forClass( _domainClass ).instance();
      int size = _steps.size();
      int count = 0;
      for( StepMetadata metadata : _steps )
      {
         count++;
         String fieldName = metadata.getFieldName();
         Field field = findFieldInPath( fieldName, _domainInstance );
         if( metadata.getTitle() == null )
         {
            String title = getResourceProperty( fieldName + ".title" );
            if( title == null )
            {
               title = field.label();
            }
            metadata.setTitle( title );
         }
         if( metadata.getDescription() == null )
         {
            String description = getResourceProperty( fieldName + ".description" );
            if( StringEO.isEmpty(description) )
            {
               description = field.description();
            }
            if( description == null )
            {
               description = field.label();
            }
            metadata.setDescription( description );
         }
         
         if( count == size && _lastPropertyCommits )
         {
            addStep( new CommitPropertyStep( metadata ) );
         }
         else
         {
            addStep( new PropertyStep( metadata ) );
         }
      }
      
      if( !_lastPropertyCommits )
      {
         addStep( new DomainCommitStep() );
      }
   }

   private void doCommit()
   {
      for( Map.Entry<String, EObject> entry : _fieldValues.entrySet() )
      {
         String fieldPath = entry.getKey();
         EObject value = entry.getValue();
         evalFieldInPath( fieldPath, _domainInstance, value );
         _domainInstance.save();
      }
   }

   private void evalFieldInPath( String fieldPath, ComplexEObject context, EObject value )
   {
      Field field = null;
      EObject fieldValue = null;
      if( fieldPath.indexOf( '.' ) > -1 )
      {
         String[] nested = fieldPath.split( "\\." );
         ComplexEObject element = context;
         for( int i = 0; i < nested.length - 1; i++ )
         {
            String fname = nested[i];
            String cname = fieldPath.substring( 0, fieldPath.indexOf( fname ) + fname.length() );
            EObject pathValue = _fieldValues.get( cname );
            field = element.field( fname );
            fieldValue = (EObject) field.reflectGet( element );
            fieldValue.setValue( pathValue );
            _fieldValues.put( cname, fieldValue );
            element = (ComplexEObject) fieldValue;
         }
         field = element.field( nested[nested.length - 1] );
         fieldValue = (EObject) field.reflectGet( element );
      }
      else
      {
         field = context.field( fieldPath );
         fieldValue = (EObject) field.reflectGet( context );
      }
      fieldValue.setValue( value );
   }

   private Field findFieldInPath( String fieldPath, ComplexEObject context )
   {
      Field field = null;
      if( fieldPath.indexOf( '.' ) > 1 )
      {
         String[] nested = fieldPath.split( "\\." );
         ComplexEObject root = context;
         for( int i = 0; i < nested.length - 1; i++ )
         {
            String fname = nested[i];
            String cname = fieldPath.substring( 0, fieldPath.indexOf( fname ) + fname.length() );
            if( _fieldValues.get( cname ) == null )
            {
               root = (ComplexEObject) newValue( root.field( fname ) );
               _fieldValues.put( cname, root );
            }
            else
            {
               root = (ComplexEObject) _fieldValues.get( fname );
            }
         }
         field = root.field( nested[nested.length - 1] );
         _fieldValues.put( fieldPath, (EObject) newValue( field ) );
      }
      else
      {
         field = context.field( fieldPath );
         if( _fieldValues.get( fieldPath ) == null )
         {
            _fieldValues.put( fieldPath, (EObject) newValue( field ) );
         }
      }
      return field;
   }

   private String getResourceProperty( String key )
   {
      return getMessageForKey("Wizard." + _domainClass.getName() + "." + key);
   }
   private String getMessageForKey(String key)
   {
      try
      {
         return Context.getInstance()
               .getMessageSource()
               .getMessage( key, null, Locale.getDefault() );
      }
      catch( NoSuchMessageException e )
      {
         // do nothing, return value will be null
      }
      return null;
   }

   private JComponent getViewForStep( String fieldName )
   {
      Object value = _fieldValues.get( fieldName );
      if( value instanceof ComplexEObject )
      {
          ComplexEObject ceo = (ComplexEObject) value;
          return (JComponent) ceo.getMainView();
      }
      else
      {
         // TODO handle non ComplexEObject here
         return null;
      }
   }

   private Object newValue( Field field )
   {
      Class targetClass = field.getJavaClass();
      if( ComplexEObject.class.isAssignableFrom( targetClass ) )
      {
         return ComplexType.forClass( targetClass )
               .instance();
      }
      else
         if( AtomicEObject.class.isAssignableFrom( targetClass ) )
         {
            try
            {
               return targetClass.newInstance();
            }
            catch( InstantiationException e )
            {
               throw new RuntimeException( e );
            }
            catch( IllegalAccessException e )
            {
               throw new RuntimeException( e );
            }
         }
      throw new RuntimeException( "This shouldn't happen." );
   }

   private class CommitPropertyStep extends CommitStep
   {
      private StepMetadata _metadata;

      public CommitPropertyStep( StepMetadata metadata )
      {
         super();
         _metadata = metadata;
      }

      public void commit()
      {
         doCommit();
      }

      public String description()
      {
         return _metadata.getDescription();
      }

      public JComponent getView()
      {
         return getViewForStep( _metadata.getFieldName() );
      }

      public String title()
      {
         return _metadata.getTitle();
      }
   }

   private class DomainCommitStep extends CommitStep
   {
      private JLabel _content;
      private String _description;
      private String _title;

      public DomainCommitStep()
      {
         super();
      }

      public void commit()
      {
         doCommit();
      }

      public String description()
      {
         if( _description == null )
         {
            _description = getResourceProperty( "finish-step.description" );
            if (StringEO.isEmpty(_description))
            {
               _description = "Finish";
            }
         }
         return _description;
      }

      public JComponent getView()
      {
         if( _content == null )
         {
            String label = getResourceProperty( "finish-step.content" );
            if (StringEO.isEmpty(label))
            {
               label = "Click 'Finish' to end the wizard";
            }
            _content = new JLabel( label );
         }
         return _content;
      }

      public String title()
      {
         if( _title == null )
         {
            _title = getResourceProperty( "finish-step.title" );
            if (StringEO.isEmpty(_title))
            {
               _title = "Finish";
            }
         }
         return _title;
      }

      private String getResourceProperty( String key )
      {
         String value = DomainWizard.this.getResourceProperty( key );
         if (StringEO.isEmpty(value))
         {
            value = getMessageForKey("Wizard." + key);
         }
         return value;
      }
   }

   private class PropertyStep extends BasicStep
   {
      private StepMetadata _metadata;

      public PropertyStep( StepMetadata metadata )
      {
         super();
         _metadata = metadata;
      }

      public String title() { return _metadata.getTitle(); }
      public String description() { return _metadata.getDescription(); }

      public JComponent getView()
      {
         return getViewForStep( _metadata.getFieldName() );
      }
   }
}