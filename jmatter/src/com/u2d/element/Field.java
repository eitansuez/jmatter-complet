/*
 * Created on Jan 19, 2004
 */
package com.u2d.element;

import java.beans.*;
import java.lang.reflect.*;
import java.util.List;
import java.util.ArrayList;

import com.u2d.field.*;
import com.u2d.find.Searchable;
import com.u2d.model.*;
import com.u2d.pattern.*;
import com.u2d.restrict.*;
import com.u2d.type.Choice;
import com.u2d.type.atom.BooleanEO;
import com.u2d.type.atom.IntEO;
import com.u2d.type.atom.StringEO;
import com.u2d.validation.Required;
import com.u2d.view.*;
import com.u2d.reflection.Fld;

/**
 * @author Eitan Suez
 */
public abstract class Field extends Member
         implements java.io.Serializable
{
   protected Class _clazz;
   private String _cleanPath, _path, _naturalPath;
   private final StringEO _fullPath = new StringEO();
   private Title _title;
   
   protected transient Method _getter, _setter;

   public static String[] fieldOrder = {"name", "fullPath", "label", "required", "mnemonic", 
      "description"};
   public static String[] readOnly = {"name", "fullPath"};
   public static String[] identities = {"fullPath"};
   
   

   public Field() {}

   public Field(FieldParent parent, String name) throws IntrospectionException
   {
      PropertyDescriptor descriptor = new PropertyDescriptor(name, parent.getJavaClass());
      init(parent, descriptor);
   }

   public Field(FieldParent parent, PropertyDescriptor descriptor)
   {
      init(parent, descriptor);
   }

   protected void init(FieldParent parent, PropertyDescriptor descriptor)
   {
      _parent = parent;

      getName().setValue(descriptor.getName());  // (also derives Label)
      _setter = descriptor.getWriteMethod();
      _getter = descriptor.getReadMethod();
      _clazz = descriptor.getPropertyType();

      computeFieldPaths();

      setState(_readState, true);
   }

   public Object reflectGet(EObject parent)
   {
      Class parentClass = parent().getJavaClass();
      try
      {
         if (!parentClass.isAssignableFrom(parent.getClass()))
         {
            throw new IllegalArgumentException("Invalid parent type: "+parent.getClass()+"; expected: "+parentClass);
         }

         return _getter.invoke(parent);
      }
      catch (IllegalAccessException ex)
      {
         System.err.println(ex.getMessage());
         ex.printStackTrace();
      }
      catch (InvocationTargetException ex)
      {
         System.err.println(ex.getMessage());
         ex.printStackTrace();
      }

      return null;
   }

   public abstract EObject get(ComplexEObject parent);
   public abstract void set(ComplexEObject parent, Object value);
   public void restore(ComplexEObject parent, Object value)
   {
      set(parent, value);
   }

   public String localizedLabel(Localized l)
   {
      String key = getPath();
      return l.localeLookup(key);
   }

   public abstract EView getView(ComplexEObject parent);
   protected FieldViewHandler viewHandler;
   public void setViewHandler(FieldViewHandler handler)
   {
      viewHandler = handler;
   }
   
   public abstract int validate(ComplexEObject parent);
   public abstract void setState(ComplexEObject parent, State state);
   public abstract void pushState(ComplexEObject parent, State state);
   public abstract void popState(ComplexEObject parent);

   public EObject createInstance()
   {
      EObject eo = null;

      if (!isAtomic())
      {
         eo = fieldtype().instance();
      }
      else
      {
         try
         {
            eo = (EObject) _clazz.newInstance();
         }
         catch (java.lang.InstantiationException ex)
         {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
         }
         catch (IllegalAccessException ex)
         {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
         }
      }

      if (eo != null)
         eo.setField(this, null);

      return eo;
   }


   private boolean _inherited = false;
   public void setInherited(boolean inherited) { _inherited = inherited; }
   public boolean isInherited() { return _inherited; }



   public boolean isAssociable()
   {
      return (this instanceof Associable);
   }

   public ComplexType fieldtype()
   {
      if (ComplexEObject.class.isAssignableFrom(_clazz))
      {
         return ComplexType.forClass(_clazz);
      }
      return null;
   }

   public boolean isInterfaceType() { return fieldtype().isInterfaceType(); }
   public boolean isAbstract() { return fieldtype().isAbstract(); }


   // TODO: make all these abstract and override in child classes
   // TODO: create a ChoiceField type and remove if statements
   public boolean isAtomic()
   {
      return (this instanceof AtomicField);
   }
   public boolean isAggregate()
   {
      return (this instanceof AggregateField);
   }
   public boolean isChoice()
   {
      return Choice.class.isAssignableFrom(_clazz);
   }
   public boolean isIndexed()
   {
      return (this instanceof IndexedField);
   }
   public boolean isComposite()
   {
      return (this instanceof CompositeField);
   }
   public boolean isAssociation()
   {
      return (this instanceof AssociationField);
   }


   private boolean _searchable = true;
   public boolean isSearchable()
   {
      // hacked patch (temporary):
      return ( Searchable.class.isAssignableFrom(_clazz) &&
            !(EOCommand.class.isAssignableFrom(_clazz)) &&
            _searchable );
   }
   public void setSearchable(boolean value) { _searchable = false; }


   protected String _sortPropertyName;
   protected boolean _sortable;

   public String getSortPropertyName()
   {
      if (isChoice())
      {
         return getCleanPath() + ".code";
      }
      return getCleanPath() + "." + fieldtype().sortBy();
   }
   public boolean isSortable()
   {
      return isChoice() || fieldtype().isSortable();
   }


   public Class getJavaClass() { return _clazz; }

   public String getPath() { return _path; }
   public String getCleanPath() { return _cleanPath; }
   public String getNaturalPath() { return _naturalPath; }
   // sample fullPath:  com.u2d.clinic.Patient#name.first
   public String fullPath() { return _fullPath.stringValue(); }


   public StringEO getFullPath() { return _fullPath; }

   private void computeFieldPaths()
   {
      StringBuffer path = new StringBuffer(name());
      FieldParent parent = parent();
      while (parent instanceof Field)
      {
         path.insert(0, parent.name() + ".");
         parent = parent.parent();
      }

      ComplexType root = (ComplexType) parent;

      _path = parent.name() + "." + path.toString();

      String fullPath = root.getQualifiedName() + "#" + path.toString(); // full path
            // is necessary for Field's implementation of UserType!
      _fullPath.setValue(fullPath);

      _cleanPath = path.toString();

      setNaturalPath();
   }

   public boolean isEmpty(ComplexEObject parent)
   {
      EObject value = get(parent);
      return (value == null) || (value.isEmpty());
   }

   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (obj == this) return true;
      if (! (obj instanceof Field) ) return false;
      Field fld = (Field) obj;
      return getPath().equals(fld.getPath());
   }

   public String toString() { return _naturalPath; }


   // employer.contact.address.addressLine1
   //    becomes
   // Employer contact address's Address Line 1
   private void setNaturalPath()
   {
      String text = _path.trim();
      StringBuffer sb = new StringBuffer();
      sb.append(Character.toUpperCase(text.charAt(0)));
      for (int i=1; i<text.length(); i++)
      {
         if (text.charAt(i) == '.')
         {
            String remainder = text.substring(i+1);
            if (remainder.indexOf(".") < 0) // last dot
               sb.append(ComplexType.localeLookupStatic("s")+" ");
            else
               sb.append(' ');
            sb.append(Character.toUpperCase(text.charAt(i+1)));
            i++;
         }
         else
         {
            if (Character.isUpperCase(text.charAt(i)))
               sb.append(' ');
            sb.append(text.charAt(i));
         }
      }
      _naturalPath = sb.toString();
      _title = new Title(_naturalPath);
   }


   /* ***** */

   public Field copy() throws IntrospectionException
   {
      if (this instanceof AggregateField)
      {
         return new AggregateField(_parent, name());
      }
      else if (this instanceof AtomicField)
      {
         return new AtomicField(_parent, name());
      }
      else if (this instanceof AssociationField)
      {
         return new AssociationField(_parent, name());
      }
      else if (this instanceof IndexedField)
      {
         return new IndexedField(_parent, name());
      }
      return null;
   }

   public static Field forPath(String fieldPath)
   {
      if (fieldPath == null) return null;
      ClassLoader loader = Thread.currentThread().getContextClassLoader();

      try
      {
         String[] parts = fieldPath.split("#");  // split on fullpath's # separator
         Class cls = loader.loadClass(parts[0]);
         ComplexType type = ComplexType.forClass(cls);
         parts = parts[1].split("\\."); // split the fields
         Field field = type.field(parts[0]);
         for (int i=1; i<parts.length; i++)
            field = field.field(parts[i]);
         return field;
      }
      catch (ClassNotFoundException ex)
      {
         System.err.println("ClassNotFoundException: "+ex.getMessage());
         ex.printStackTrace();
      }
      return null;
   }

   // ==

   public Title title() { return _title; }

   protected boolean _readOnly = false;
   public boolean isReadOnly() { return _readOnly || restrictReadOnly(); }
   public void setReadOnly(boolean readOnly) { _readOnly = readOnly; }

   protected final BooleanEO _hidden = new BooleanEO(false);
   public BooleanEO getHidden() { return _hidden; }
   public boolean hidden()
   {
       return _hidden.booleanValue() || restrictHidden();
   }
   public void hide() { _hidden.setValue(true); }
   
   // Restriction-related..
   protected FieldRestriction _restriction = null;
   public void applyRestriction(Restriction restriction)
   {
      if (!(restriction instanceof FieldRestriction))
         throw new IllegalArgumentException("Restriction must be a field restriction");

      _restriction = (FieldRestriction) restriction;
   }
   public void liftRestriction() { _restriction = null; }
   
   public boolean restrictHidden() { return (_restriction != null) && (_restriction.hidden()); }
   public boolean restrictReadOnly() { return (_restriction != null) && (_restriction.readOnly()); }
   
   public int hashCode()
   {
      return fullPath().hashCode();
   }


   private final BooleanEO _required = new BooleanEO(false);
   public BooleanEO getRequired() { return _required; }
   public boolean required() { return _required.booleanValue(); }
   
   private final IntEO _colsize = new IntEO();
   public IntEO getColsize() { return _colsize; }
   public int colsize() { return _colsize.intValue(); }

   private final StringEO _colname = new StringEO();
   public StringEO getColname() { return _colname; }
   public String colname() { return _colname.stringValue(); }

   private final IntEO _displaysize = new IntEO();
   public IntEO getDisplaysize() { return _displaysize; }
   public int displaysize() { return _displaysize.intValue(); }
   
   private final StringEO _format = new StringEO();
   public StringEO getFormat() { return _format; }
   public String format() { return _format.stringValue(); }

   private final BooleanEO _persist = new BooleanEO(true);
   public BooleanEO getPersist() { return _persist; }
   public boolean persist() { return _persist.booleanValue(); }

   private transient Method _requiredMethod;
   public void setRequiredMethod(Method method) { _requiredMethod = method; }

   public Required getRequired(ComplexEObject parent)
   {
      if (_requiredMethod != null)
      {
         try
         {
            Object returnValue = _requiredMethod.invoke(parent);
            return ((Required) returnValue);
         }
         catch (Exception ex)
         {
            ex.printStackTrace();
            // let it fall through..
         }
      }
      return new Required(required());
   }
   
   private boolean _tabView = false;
   public boolean isTabView() { return _tabView; }
   public void setTabView(boolean isTabView) { _tabView = isTabView; }
   
   public void applyMetadata()
   {
      if (_getter.isAnnotationPresent(Fld.class))
      {
         Fld fat = (Fld) _getter.getAnnotation(Fld.class);
         _mnemonic.setValue(fat.mnemonic());
         _persist.setValue(fat.persist());
         _hidden.setValue(fat.hidden());
         
         if (!StringEO.isEmpty(fat.label()))
            getLabel().setValue(fat.label());
         
         if (!StringEO.isEmpty(fat.description()))
            getDescription().setValue(fat.description());
         
         if (!StringEO.isEmpty(fat.colname()))
            getColname().setValue(fat.colname());
         
         if (fat.colsize() > 0)
            getColsize().setValue(fat.colsize());
         
         if (fat.displaysize() > 0)
            getDisplaysize().setValue(fat.displaysize());
         
         if (!StringEO.isEmpty(fat.format()))
            getFormat().setValue(fat.format());
         
         // can't figure out how to mark an annotation member as optional
         // default null does not work.
         if (!Class.class.equals(fat.optionsEnum()))
         {
            setValueOptions(fat.optionsEnum().getEnumConstants());
         }
      }
   }
   
   private List<String> valueOptions; 
   private void setValueOptions(Object[] options)
   {
      valueOptions = new ArrayList<String>();
      for (Object option : options)
      {
         valueOptions.add(option.toString());
      }
   }
   public boolean hasValueOptions()
   {
      return (valueOptions!=null) && !(valueOptions.isEmpty());
   }
   public List<String> valueOptions() { return valueOptions; }

}
