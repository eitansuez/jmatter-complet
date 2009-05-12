/*
 * Created on Mar 31, 2004
 */
package com.u2d.persist;

import com.u2d.element.Field;
import com.u2d.element.Member;
import com.u2d.field.AggregateField;
import com.u2d.field.AssociationField;
import com.u2d.field.AtomicField;
import com.u2d.field.IndexedField;
import com.u2d.find.Inequality;
import com.u2d.model.*;
import com.u2d.type.atom.TimeSpan;
import com.u2d.type.atom.ChoiceEO;
import com.u2d.type.atom.DateEO;
import com.u2d.type.atom.StringEO;
import com.u2d.type.AbstractChoiceEO;
import com.u2d.persist.type.ChoiceEOUserType;
import com.u2d.persist.type.DateEOUserType;
import com.u2d.calendar.CalEvent;
import com.u2d.calendar.ScheduleEO;
import com.u2d.calendar.CalendarEO;
import com.u2d.app.Role;
import org.dom4j.*;
import org.dom4j.io.*;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.usertype.CompositeUserType;
import java.io.*;
import java.util.*;

/**
 * Given a ComplexEObject Class, HBMMaker will produce a hibernate .hbm.xml file
 * 
 * @author Eitan Suez
 */
public class HBMMaker
{
   private ComplexType _type;
   private Document _doc;
   private boolean _joinedSubclass = false;
   
   public HBMMaker(Class clazz)
   {
      _type = ComplexType.forClass(clazz);
      Element rootElem = startHBMXMLDoc();
      Element classElem = produceClassElem(rootElem);
      
      if (clazz.isInterface())
      {
         produceInterfaceIDProperty(classElem);
      }
      else if (!_joinedSubclass)
      {
         produceIDProperty(classElem);
         produceVersionProperty(classElem);
      }
      
      produceFieldMapping(classElem, _type);
      if (!StringEO.isEmpty(_type.filterString()))
      {
         produceFilter(classElem, _type);
         produceFilterDef(rootElem);
      }
   }

   private void produceFilterDef(Element rootElem)
   {
      Element filterDef = rootElem.addElement("filter-def");
      filterDef.addAttribute("name", Role.AUTHFILTER_NAME);
      filterDef.addElement("filter-param")
            .addAttribute("name", "currentUser")
            .addAttribute("type", "long");
   }
   private void produceFilter(Element classElem, ComplexType type)
   {
      classElem.addElement("filter")
            .addAttribute("name", Role.AUTHFILTER_NAME)
            .addAttribute("condition", type.filterString());
   }
   
   public Document getDoc() { return _doc; }
   
   public void writeDocToFile() throws IOException
   {
      String name = _type.getQualifiedName().replace('.', File.separatorChar) + ".hbm.xml";
      writeDocToFile(name);
   }
   
   public void writeDocToFile(String filename) throws IOException
   {
      new File(filename).getParentFile().mkdirs();
      OutputFormat pretty = OutputFormat.createPrettyPrint();
      XMLWriter writer = new XMLWriter(new FileWriter(filename), pretty);
      writer.write(_doc);
      writer.close();
   }


   
   private void produceFieldMapping(Element parentElem, FieldParent fieldParent)
   {
      produceFieldMapping(parentElem, fieldParent, null);
   }
   
   private void produceFieldMapping(Element parentElem, FieldParent fieldParent, String prefix)
   {
      for (Iterator itr = fieldParent.fields().iterator(); itr.hasNext(); )
      {
         Field field = (Field) itr.next();
         
         if (_joinedSubclass && prefix == null && field.isInherited())
            continue;
         
         if ( prefix != null &&  // means we're in a component context
                ( "createdOn".equals(field.name()) ||
                  "deleted".equals(field.name()) ||
                  "deletedOn".equals(field.name()) )
            )
         {
            continue;
         }
         
         if (!field.persist())  // derived fields should be marked as such
            continue;
         
         if ( ComplexType.class.isAssignableFrom(field.getJavaClass()) || 
              EObject.class.equals(field.getJavaClass()) ||
              Inequality.class.equals(field.getJavaClass()) )
         {
            produceCustomProperty(parentElem, field, prefix);
         }
         else if (field.isAtomic())
         {
            if (!field.pk())
            {
               produceProperty(parentElem, (AtomicField) field, prefix);
            }
         }
         else if (field.isInterfaceType() && !field.isIndexed())
         {
            produceAnyType(parentElem, field, prefix);
         }
         else if (field.isAggregate())
         {
            if ( hasCustomUserType(field) )
            {
               Element elem = produceCustomProperty(parentElem, field, prefix);
               addAccessAttribute(elem);
            }
            else
            {
               produceComponent(parentElem, (AggregateField) field, prefix);
            }
         }
         else if (field.isAssociation())
         {  // this should not necessarily produce a many-to-one:  could be a one-to-one
            produceManyToOne(parentElem, (AssociationField) field, prefix);
         }
         else if (field.isIndexed())
         {
            IndexedField idxField = (IndexedField) field;
            if (idxField.isOrdered())
            {
               produceList(parentElem, idxField, fieldParent, prefix);
            }
            else
            {
               produceBag(parentElem, idxField, fieldParent, prefix);
            }
         }
//         else if (field.isMap())
//         {
//            produceMap(parentElem, (MapField) field, fieldParent);
//         }
      }
   }
   
   // possible alternative to using a bag.  not sure yet what this buys me
   private Element produceList(Element parentElem, IndexedField field, FieldParent parent, String prefix)
   {
      Element listElem = produceCollection("list", parentElem, field, parent, prefix);

      // hibernate:  cannot mark a bidi one-many association's indexed collection side inverse
      if (field.isBidirectionalRelationship())
      {
         if (!field.isManyToMany() || field.isInverse())
         {
            listElem.addAttribute("inverse", "false");
         }
      }
      
      return listElem;
   }
   private Element produceBag(Element parentElem, IndexedField field, FieldParent parent, String prefix)
   {
      // commenting out for now:
//      String listType = (field.isComposite()) ? "idbag" : "bag";
      String listType = "bag";
      Element bagElem = produceCollection(listType, parentElem, field, parent, prefix);

      if (field.isBidirectionalRelationship())
      {
         if (!field.isManyToMany() || field.isInverse())
         {
            bagElem.addAttribute("inverse", "true");
         }
      }

      return bagElem;
   }

   private String colname(String prefix, Field field)
   {
      String firsthalf = (prefix == null) ? "" : prefix + "_";
      return firsthalf + field.name().toLowerCase();
   }
   
   private Element produceCollection(String listType, 
                                     Element parentElem, IndexedField field, FieldParent parent, String prefix)
   {
      Element listElem = parentElem.addElement(listType);
      listElem.addAttribute("name", field.name());

      if (field.isComposite())
      {
         listElem.addAttribute("table", colname(prefix, field));
      }

      addAccessAttribute(listElem);

      if ("idbag".equals(listType))
      {
         Element collectionId = listElem.addElement("collection-id");
         collectionId.addAttribute("column", "ID").addAttribute("type", "long");
         collectionId.addElement("generator").addAttribute("class", "increment");
      }
      
      Element keyElem = listElem.addElement("key");
      if (field.isBidirectionalRelationship())
         keyElem.addAttribute("column", genIdName(field.getInverseFieldName()));
      else
         keyElem.addAttribute("column", genIdName(parent.name()));

      
      // dtd/schema constraint:  list-index must be specified right after key
      if ("list".equals(listType))
      {
         Element listIndex = listElem.addElement("list-index");
         listIndex.addAttribute("column", genName(field.name(), "_sortorder"));
      }
      
      if (field.ownsChildren())
      {
         listElem.addAttribute("cascade", "all,delete-orphan");
      }

      if (field.isManyToMany())
      {
         if (field.isInterfaceType())
         {
            String tableName = (field.isInverse() ? 
              field.name() + "_" + parent.name() : 
              parent.name() + "_" + field.name());
            listElem.addAttribute("table", tableName);
            Element manyToAnyElem = listElem.addElement("many-to-any");

            manyToAnyElem.addAttribute("id-type", "com.u2d.persist.type.PolyType");
            // ..so can polymorphically handle types whose id's are of type long
            // as well as types whose ids are of type string
            // was:
//            manyToAnyElem.addAttribute("id-type", "long");
            
            manyToAnyElem.addElement("column").addAttribute("name", field.name() + "_type");
            manyToAnyElem.addElement("column").addAttribute("name", field.name() + "_id");
         }
         else
         {
            String tableName = (parent.name() + "_" + field.name()).toLowerCase();
            if (field.isInverse())
            {
               Field inverseField = field.inverseField();
               tableName = (field.fieldtype().name() + "_" + inverseField.name()).toLowerCase();
            }
            
            listElem.addAttribute("table", tableName);
            Element manyToManyElem = listElem.addElement("many-to-many");
            manyToManyElem.addAttribute("column", genIdName(field.name()));
            manyToManyElem.addAttribute("class", field.fieldtype().getQualifiedName());
         }
      }
      else if (field.isComposite())
      {
         Element compElem = listElem.addElement("composite-element");
         compElem.addAttribute("class", field.fieldtype().getQualifiedName());
         produceFieldMapping(compElem, field.fieldtype());
      }
      else
      {
         // if field has a user type (TODO: or more generally if it is a value type), 
         // then need to map as an element, not a one-to-many
         if (hasCustomUserType(field.fieldtype().getJavaClass()))
         {
            Element elem = listElem.addElement("element");
            Class userTypeCls = getCustomUserType(field.fieldtype().getJavaClass());
            elem.addAttribute("type", userTypeCls.getName());
         }
         else
         {
            Element oneToManyElem = listElem.addElement("one-to-many");
            oneToManyElem.addAttribute("class", field.fieldtype().getQualifiedName());
         }
      }
      return listElem;
   }

//   private Element produceMap(Element parentElem, MapField field, FieldParent parent)
//   {
//      Element mapElem = parentElem.addElement("map");
//      mapElem.addAttribute("name", field.getName());
//      addAccessAttribute(mapElem);
//      Element keyElem = mapElem.addElement("key");
//      keyElem.addAttribute("column", genIdName(parent.getName()));
//      Element mmindex = mapElem.addElement("index-many-to-many");
//      //mmindex.addAttribute("column", ??);
//      mmindex.addAttribute("class", field.keyType().getQualifiedName());
//      Element mm = mapElem.addElement("many-to-many");
//      mm.addAttribute("column", ??);
//      mm.addAttribute("class", ?????)...
//
//   }

   private String genIdName(String base)
   {
      return genName(base, "_id");
   }
   private String genName(String base, String suffix)
   {
      int idx = base.lastIndexOf(".");
      if (idx >= 0) base = base.substring(idx+1);
      return base.toLowerCase() + suffix;
   }

   private Element produceManyToOne(Element parentElem, AssociationField field, String prefix)
   {
      Element elem = parentElem.addElement("many-to-one");
      elem.addAttribute("name", field.name());
      
      if (Member.class.isAssignableFrom(field.getJavaClass()))
      {
         elem.addAttribute("cascade", "all");
      }

      elem.addAttribute("column", colname(prefix, field) + "_id");

      if (field.required())
      {
         elem.addAttribute("not-null", "true");
      }


      addAccessAttribute(elem);

      return elem;
   }

   private Element produceCustomProperty(Element parentElem, Field field, String prefix)
   {
      Element propElem = parentElem.addElement("property");
      propElem.addAttribute("name", field.name());
      Class typeClass = getUserType(field);
      
      propElem.addAttribute("type", typeClass.getName());

      // for user types that map to > 1 field: (as in TimeSpan)
      try
      {
         java.lang.reflect.Field colField = typeClass.getDeclaredField("COLUMNNAMES");
         String[] colNames = (String[]) colField.get(null);
         Element column;

         for (String colName : colNames)
         {
            column = propElem.addElement("column");
            String colprefix = (prefix == null) ? "" : prefix + "_";
            column.addAttribute("name", colprefix + colName);
         }
      }
      catch (Exception ex)  // common case:
      {
         if (prefix == null)
         {
            if (!field.getColname().isEmpty())
            {
               propElem.addAttribute("column", field.colname());
            }
         }
         else
         {
            propElem.addAttribute("column", prefix + "_" + field.getName());
         }
      }

      return propElem;
   }
   
   private Class getUserType(Field field)
   {
      if (AtomicEObject.class.equals(field.getJavaClass()))
         return AtomicUserTypeDelegate.class;
      
      if (Inequality.class.equals(field.getJavaClass()))
         return InequalityUserTypeDelegate.class;
      
      if (EObject.class.equals(field.getJavaClass()))
         return EObjectUserTypeDelegate.class;

      if (ChoiceEO.class.isAssignableFrom(field.getJavaClass()))
         return ChoiceEOUserType.class;

      if (DateEO.class.isAssignableFrom(field.getJavaClass()))
         return DateEOUserType.class;

      if (hasCustomUserType(field))
      {
         return getCustomUserType(field);
      }
      else
      {
         // most common case..
         // return field.getJavaClass();

         // Establish a convention:
         String className = field.getJavaClass().getName();

         int index = className.lastIndexOf(".");
         String packageName = className.substring(0, index);
         className = className.substring(index+1);
         String jmatterClassName = "com.u2d.persist.type." + className + "UserType";

         try
         {
            return Class.forName(jmatterClassName);
         }
         catch (ClassNotFoundException e)
         {
            try
            {
               String userClassName = packageName + ".persist." + className + "UserType";
               return Class.forName(userClassName);
            }
            catch (ClassNotFoundException e2)
            {
               throw new RuntimeException("Failed to resolve user type for class "+field.getJavaClass().getName(), e2);
            }
         }
      }
   }
   
   private boolean hasCustomUserType(Field field)
   {
      return hasCustomUserType(field.getJavaClass());
   }
   private boolean hasCustomUserType(Class cls)
   {
      try
      {
         java.lang.reflect.Method customTypeGetter =
            cls.getDeclaredMethod("getCustomTypeImplementorClass");
         return (customTypeGetter != null);
      } catch (NoSuchMethodException e) {}
      return false;
   }
   private Class getCustomUserType(Field field)
   {
      return getCustomUserType(field.getJavaClass());
   }
   private Class getCustomUserType(Class cls)
   {
      try
      {
         java.lang.reflect.Method customTypeGetter = 
            cls.getDeclaredMethod("getCustomTypeImplementorClass");

         return (Class) customTypeGetter.invoke(null);
      }
      catch (Exception ex) { }
      return null;
   }
   
   private Element produceAnyType(Element parentElem, Field field, String prefix)
   {
      Element anyElem = parentElem.addElement("any");
      anyElem.addAttribute("name", field.name());
      anyElem.addAttribute("id-type", "long");
      
      if (prefix == null)
      {
         anyElem.addElement("column").addAttribute("name", field.name() + "_type");
         anyElem.addElement("column").addAttribute("name", field.name() + "_id");
      }
      else
      {
         anyElem.addElement("column").addAttribute("name", 
               prefix + "_" + field.name() + "_type");
         anyElem.addElement("column").addAttribute("name", 
               prefix + "_" + field.name() + "_id");
      }
      
      return anyElem;
   }
   
   private Element produceProperty(Element parentElem, AtomicField field, String prefix)
   {
      Element propElem = parentElem.addElement("property");
      propElem.addAttribute("name", field.name());
      Class typeClass = getUserType(field);
      propElem.addAttribute("type", typeClass.getName());
      
      if (field.isIdentity() && !(aggregated(field)))
         propElem.addAttribute("unique", "true");

      // update="false" not supported for type date, neither supported for properties of components
//      if ( ! (field.getJavaClass().equals(DateEO.class) || field.parent() instanceof AggregateField) )
//      {
//         if (field.isIdentity() || field.isReadOnly())
//            propElem.addAttribute("update", "false");
//      }
      // unfortunately cannot implement the above because although fields are
      // specified as readonly or identities, they do need to be manipulated and updated programmatically
      // afterwards, just not by the ui.
      
      if (field.isIdentity() || field.required())
         propElem.addAttribute("not-null", "true");
      

      if (field.isChoice())
      {
         ChoiceEOUserType.fillPropertyElement(propElem, field, prefix);
      }
      else
      {
         // TODO: this should be incapsulated in type and exposed via an interface (fix)
         boolean index = false;
         if (TimeSpan.class.isAssignableFrom(field.getJavaClass()))
            index = true;

         // for user types that map to > 1 field: (as in TimeSpan)
         try
         {
            java.lang.reflect.Field colField = typeClass.getDeclaredField("COLUMNNAMES");
            String[] colNames = (String[]) colField.get(null);
            Element column;
            for (String colName : colNames)
            {
               column = propElem.addElement("column");
               String colprefix = (prefix == null) ? "" : prefix + "_";

               if (CompositeUserType.class.isAssignableFrom(typeClass))
               {
                  colprefix += field.getName() + "_";
                  // e.g. type -> severity_type (where fieldname is 'severity')
                  // e.g. 2: type -> importance_type (where fieldname is 'importance')
               }

               column.addAttribute("name", colprefix + colName);

               if (index)
                  column.addAttribute("index", _type.name() + "_" + colprefix + colName + "_idx");
            }
         }
         catch (Exception ex)  // common case:
         {
            if (prefix == null)
            {
               if (!field.getColname().isEmpty())
               {
                  propElem.addAttribute("column", field.colname());
               }
            }
            else
            {
               propElem.addAttribute("column", prefix + "_" + field.name());
            }
         }

         if (field.colsize() > 0)
         {
            propElem.addAttribute("length", ""+field.colsize());
         }

         try
         {
            java.lang.reflect.Method method = field.getJavaClass().getMethod("getLength");
            if (method != null)
            {
               Integer length  = (Integer) method.invoke(null);
               propElem.addAttribute("length", length.toString());
            }
         }
         catch (Exception ex) {}
      }

      addAccessAttribute(propElem);
      return propElem;
   }

   private boolean aggregated(AtomicField field)
   {
      //return field.parent() instanceof AggregateField;
      return (field.parent() != null && field.parent().parent() != null);
   }

   private Element addAccessAttribute(Element elem)
   {
      elem.addAttribute("access", "com.u2d.persist.PropertyAccessorAdapter");
      return elem;
   }
   
   private Element produceComponent(Element parentElem, AggregateField field, String prefix)
   {
      Element compElem = parentElem.addElement("component");
      compElem.addAttribute("name", field.name());
      compElem.addAttribute("class", field.getJavaClass().getName());
      
      if (field.isIdentity())
      {
         compElem.addAttribute("unique", "true");
      }
      
      addAccessAttribute(compElem);
      
      produceFieldMapping(compElem, field, subPrefix(field, prefix));
      
      return compElem;
   }
   
   private String subPrefix(Field field, String prefix)
   {
      String name = field.name().toLowerCase();
      if (prefix == null) return concatenate(name);
      return prefix + "_" + concatenate(name);
   }
   
   private static final int CONCAT_LENGTH = 5;
   private String concatenate(String name)
   {
      if (name.length() <= CONCAT_LENGTH)
         return name;
      return name.substring(0, CONCAT_LENGTH);
   }
   
   private Element startHBMXMLDoc()
   {
      _doc = DocumentHelper.createDocument();
      
      _doc.addDocType("hibernate-mapping", 
            "-//Hibernate/Hibernate Mapping DTD 3.0//EN",
            "http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd");

      return _doc.addElement("hibernate-mapping");
   }
   
   private Element produceClassElem(Element rootElem)
   {
      Element classElem = rootElem.addElement("class");
      classElem.addAttribute("name", _type.getQualifiedName());
      
      String name = _type.name().toLowerCase();
      if (!StringEO.isEmpty(_type.tableName()))
      {
         classElem.addAttribute("table", _type.tableName());
      }
      else if (isReservedWord(name))
      {
         classElem.addAttribute("table", name+"s");
      }

      if (_type.getJavaClass().isInterface())
         return classElem;
      
      Class superclass = _type.getJavaClass().getSuperclass();
      if (!isBaseClass(superclass) &&
            ComplexEObject.class.isAssignableFrom(superclass))
      {
         _joinedSubclass = true;
         classElem.setName("joined-subclass");
         classElem.addAttribute("extends", superclass.getName());
         
         Element keyElem = classElem.addElement("key");
         keyElem.addAttribute("column", genIdName(superclass.getName()));
      }
      
      return classElem;
   }

   private boolean isBaseClass(Class cls)
   {
      return AbstractComplexEObject.class.equals(cls) ||
            AbstractChoiceEO.class.equals(cls) ||
            CalEvent.class.equals(cls) ||
            AbstractComplexMappableEObject.class.equals(cls) ||
            ScheduleEO.class.equals(cls) ||
            CalendarEO.class.equals(cls);
   }

   private String[] _reservedWords = {"order", "user", "authorization"};
   private boolean isReservedWord(String word)
   {
      for (String reservedWord : _reservedWords)
      {
         if (reservedWord.equalsIgnoreCase(word))
            return true;
      }
      return false;
   }

   private Element produceIDProperty(Element parentElem)
   {
      if (_type.specifiesPKField())
      {
         String pkfieldname = _type.pkFieldname();
         Field pkfield = _type.field(pkfieldname);
         pkfield.setPK(true);
         Element idElem = parentElem.addElement("id");
         idElem.addAttribute("name", pkfield.name());

         Class typeClass = getUserType(pkfield);
         idElem.addAttribute("type", typeClass.getName());
         addAccessAttribute(idElem);

         Element idGenElem = idElem.addElement("generator");
         if (_type.specifiesPKGenStrategy())
         {
            idGenElem.addAttribute("class", _type.pkGenStrategy().strategyName());
            // TODO: handle argument type for strategy.  e.g. sequence name.
         }
         else
         {
            idGenElem.addAttribute("class", "native");
         }
         return idElem;
      }
      else
      {
         return produceDefaultIDProperty(parentElem);
      }
   }
   private Element produceDefaultIDProperty(Element parentElem)
   {
      Element idElem = parentElem.addElement("id");
      idElem.addAttribute("name", "ID");
      idElem.addAttribute("type", "long");
      Element idGenElem = idElem.addElement("generator");
      
      if (Dialect.getDialect() instanceof PostgreSQLDialect)
      {
         idGenElem.addAttribute("class", "sequence");
         Element generatorParam = idGenElem.addElement("param");
         generatorParam.addAttribute("name", "sequence");
         generatorParam.addText("seq_"+_type.name());
      }
      else
      {
         idGenElem.addAttribute("class", "native");
      }

      return idElem;
   }
   private Element produceVersionProperty(Element parentElem)
   {
      Element idElem = parentElem.addElement("version");
      idElem.addAttribute("name", "version");
      idElem.addAttribute("type", "long");
      return idElem;
   }
   
   private Element produceInterfaceIDProperty(Element parentElem)
   {
      Element idElem = parentElem.addElement("id");
      idElem.addAttribute("type", "long");
      idElem.addElement("generator").addAttribute("class", "assigned");
      return idElem;
   }
   
   public static void main(String args[]) throws Exception
   {
      if (args.length == 0)
      {
         System.out.println("Requires argument: classname to process");
         return;
      }
      
      String className = args[0];
      Class clazz = Class.forName(className);
      
      HBMMaker hbmmaker = new HBMMaker(clazz);
      hbmmaker.writeDocToFile();
   }

}
