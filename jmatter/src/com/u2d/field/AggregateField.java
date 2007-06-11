/*
 * Created on Jan 20, 2004
 */
package com.u2d.field;

import java.util.*;
import java.beans.*;
import java.lang.reflect.Modifier;
import com.u2d.element.Field;
import com.u2d.list.RelationalList;
import com.u2d.model.*;
import com.u2d.pattern.*;
import com.u2d.type.atom.BooleanEO;
import com.u2d.view.*;

/**
 * @author Eitan Suez
 */
public class AggregateField extends CompositeField implements FieldParent
{
   private List _fields;
   private Map<String, Field> _fieldsMap;  // key is field name for fast retrieval of child
      // field by name

   protected final BooleanEO _flattenIntoParent = new BooleanEO(false);
   public BooleanEO getFlattenIntoParent() { return _flattenIntoParent; }
   public boolean flattenIntoParent() { return _flattenIntoParent.booleanValue(); }
   
   public AggregateField() {}
   
   public AggregateField(FieldParent parent, PropertyDescriptor descriptor)
			throws IntrospectionException
	{
		super(parent, descriptor);
      harvestFields();
	}
   
	public AggregateField(FieldParent parent, String name)
			throws IntrospectionException
	{
		super(parent, name);
      harvestFields();
	}
   
   private void harvestFields() throws IntrospectionException
   {
      _fields = Harvester.harvestFields(this);
      _fieldsMap = makeFieldMap(_fields);
      _subFields.setItems(_fields);
   }
   
   public List fields() { return _fields; }
   public Field field(String propName)
   {
      Field field = (Field) _fieldsMap.get(propName);
      if (field == null && !type().equals(this))
      {
         field = type().field(propName);
      }
      return field;
   }
   
   // note: was package private, forced to make public after package
   // restructuring
   public static Map<String, Field> makeFieldMap(Collection fields)
   {
      Map<String, Field> fieldMap = new HashMap<String, Field>();
      Field field = null;
      for (Iterator itr = fields.iterator(); itr.hasNext(); )
      {
         field = (Field) itr.next();
         fieldMap.put(field.name(), field);
      }
      return fieldMap;
   }
   public Map fieldsMap() { return _fieldsMap; }
   

   private final RelationalList _subFields = new RelationalList(Field.class);
   public static Class subFieldsType = Field.class;
   public RelationalList getSubFields() { return _subFields; }


   
   public EView getView(ComplexEObject parent)
   {
      ComplexEObject value = (ComplexEObject) get(parent);
      return vmech().getAggregateView(value);
   }
   
   public int validate(ComplexEObject parent)
   {
      int result = 0;
      Field field = null;
      ComplexEObject fieldValue = (ComplexEObject) get(parent);
      for (int i=0; i<_fields.size(); i++)
      {
         field = (Field) _fields.get(i);
         result += field.validate(fieldValue);
      }
      result += fieldValue.validate();
      return result;
   }
   
   public void setState(ComplexEObject parent, State state)
   {
      EObject value = get(parent);
      ((ComplexEObject) value).setState(state);
   }

   public boolean isInterfaceType() { return _clazz.isInterface(); }
   public boolean isAbstract()
   {
      return _clazz.isInterface() || 
            Modifier.isAbstract(_clazz.getModifiers());
   }
   public ComplexType fieldtype() { return ComplexType.forClass(_clazz); }


   
   public boolean hasFieldOfType(Class cls)
   {
      return firstFieldOfType(cls) != null;
   }
   public Field firstFieldOfType(Class cls)
   {
      Field fld;
      for (int i=0; i<_fields.size(); i++)
      {
         fld = (Field) _fields.get(i);
         if (fld.getJavaClass().equals(cls))
         {
            return fld;
         }
      }
      return null;
   }
   
}
