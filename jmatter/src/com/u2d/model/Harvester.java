/*
 * Created on Jan 31, 2004
 */
package com.u2d.model;

import java.util.*;
import java.lang.reflect.*;
import java.beans.*;
import com.u2d.element.*;
import com.u2d.element.Member;
import com.u2d.field.*;
import com.u2d.list.CompositeList;
import com.u2d.pattern.*;
import com.u2d.reflection.Reflector;

/**
 * @author Eitan Suez
 */
public class Harvester
{

   public static Map harvestCommands(Class clazz, ComplexType parent)
   {
      Onion commands = simpleHarvestCommands(clazz, new Onion(), false, parent);

      Set stateClasses = harvestStateClasses(clazz);
//      System.out.println("harvesting classes for "+clazz.getName()+" ("+stateClasses.size()+")");
      if (stateClasses.isEmpty())
      { // in other words, if class is simple, i.e. not stateful)
         Map commandMap = new HashMap();
         commandMap.put(clazz, commands);
         return commandMap;
      }
      // else..
      Class stateClass = null;
      Onion stateCommands = null;
      Map commandMap = new HashMap();
      for (Iterator itr = stateClasses.iterator(); itr.hasNext(); )
      {
         stateClass = (Class) itr.next();
         Onion baseCommands = null;
         if (isEditableState(stateClass))
         {
            baseCommands = new Onion();
         }
         else
         {
            baseCommands = new Onion(commands);
         }
         stateCommands = simpleHarvestCommands(stateClass, baseCommands, false, parent);
         commandMap.put(stateClass, stateCommands);
      }
      return commandMap;
   }

   private static boolean isEditableState(Class clazz)
   {
      return (EditableState.class.isAssignableFrom(clazz));
   }

   public static Onion simpleHarvestCommands(Class clazz, Onion commands,
                                             boolean wantStaticMethods, ComplexType parent)
   {
      return simpleHarvestCommands(clazz, commands, wantStaticMethods,
                                   parent, false);
   }
   
   public static Onion simpleHarvestCommands(Class clazz, Onion commands,
                                             boolean wantStaticMethods, ComplexType parent, boolean shallow)
   {
      Reflector reflector = ComplexType.reflector();
      Method[] methods = clazz.getDeclaredMethods();

      Map cmdMap = new HashMap();
      for (int i=0; i<methods.length; i++)
      {
         if (reflector.isCommand(methods[i]))
         {
            boolean methodIsStatic = Modifier.isStatic(methods[i].getModifiers());

            EOCommand cmd = reflector.reflectCommand(methods[i], clazz, parent);
            if (wantStaticMethods == methodIsStatic)
            {
               if (cmdMap.get(cmd.name()) == null)
               {
                  cmdMap.put(cmd.name(), cmd);
               }
               else  // if list has a command with same name
               {
                  EOCommand firstCmd = (EOCommand) cmdMap.get(cmd.name());
                  OverloadedEOCmd overloaded = new OverloadedEOCmd(cmd, firstCmd);
                  cmdMap.put(cmd.name(), overloaded);
               }
            }
         }
      }
      List cmdList = new ArrayList(cmdMap.values());
      commands.addAll(sort(cmdList, clazz, "commandOrder"));

      commands = commands.reduce();  // don't add an extraneous layer..

      Class superClass = clazz.getSuperclass();

      if (shallow || clazz.isInterface() ||
            superClass.isAssignableFrom(Object.class) )
      {
         return commands;
      }
      else
      {
         Onion outerLayer = new Onion(commands);
         return simpleHarvestCommands(superClass, outerLayer, wantStaticMethods,
                                      parent);
      }
   }

   public static Set harvestStateClasses(Class clazz)
   {
      Class[] classes = clazz.getClasses();
      //System.out.println("Number of classes in class "+clazz.getName()+" is "+classes.length);
      Set states = new HashSet();
      for (int i=0; i<classes.length; i++)
      {
         //System.out.println("Class Name is: "+classes[i]);
         if ( classes[i].getName().endsWith("State") &&
              State.class.isAssignableFrom(classes[i]) &&
              ! Modifier.isAbstract(classes[i].getModifiers()) )
            states.add(classes[i]);
      }
      return states;
   }

   // This mechanism isn't very efficient because getPropertyDescriptors returns 
   //  a ton of properties that don't apply (they're filtered out).
   // TODO: try different algorithms to optimize..
   // why not just go with annotations.  and why isn't this code in Field?
   public static List harvestFields(FieldParent parent) throws IntrospectionException
   {
      Class clazz = parent.getJavaClass();
      List tabViewList = introspectArrayField(clazz, "tabViews");
      List identities = introspectArrayField(clazz, "identities");
      List readOnly = introspectArrayField(clazz, "readOnly");
      List flattenIntoParent = introspectArrayField(clazz, "flattenIntoParent");

      BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
      PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
      //System.out.println("Number of descriptors for class "+clazz.getName()+" is "+descriptors.length);

      List fields = new ArrayList();
      com.u2d.element.Field field = null;
      for (int i=0; i<descriptors.length; i++)
      {
         if (omit(descriptors[i])) continue;

         //System.out.println("harvesting descriptor: "+descriptors[i].getName());
         boolean atomic = isAtomic(descriptors[i]);
         boolean indexed = isIndexed(descriptors[i]);
         boolean aggregate = isAggregate(descriptors[i]);
         if (atomic || aggregate || indexed)
         {
            if (atomic)
            {
               field = new AtomicField(parent, descriptors[i]);
            }
            else if (aggregate)
            {
               field = new AggregateField(parent, descriptors[i]);
            }
            else if (indexed)
            {
               boolean composite = isCompositeIndexed(descriptors[i]);
               field = (composite) ? new CompositeIndexedField(parent, descriptors[i]) :
                                     new IndexedField(parent, descriptors[i]);
            }

            if (tabViewList.contains(descriptors[i].getName()))
               field.setTabView(true);
            if (identities.contains(descriptors[i].getName()))
               ((CompositeField)field).setIdentity(true);
            if (readOnly.contains(descriptors[i].getName()))
               ((CompositeField)field).setReadOnly(true);
            if (flattenIntoParent.contains(descriptors[i].getName())
                  && field instanceof AggregateField)
            {
               ((AggregateField) field).getFlattenIntoParent().setValue(true);
            }
            
            if ( "deleted".equals(descriptors[i].getName()) ||
                 "deletedOn".equals(descriptors[i].getName()) )
            {
               field.setHidden(true);
               field.setSearchable(false);
            }
         }
         else
         {
            field = new AssociationField(parent, descriptors[i]);
         }

         field.applyMetadata();
            
         try
         {
            String requiredMethodName = field.getName()+"Required";
            Method requiredMethod = clazz.getMethod(requiredMethodName, null);
            field.setRequiredMethod(requiredMethod);
         }
         catch (NoSuchMethodException ex) {}
         catch (SecurityException ex) {}

         Class declaringClass = descriptors[i].getReadMethod().getDeclaringClass();
         field.setInherited(!declaringClass.equals(clazz));

         fields.add(field);
      }

      return sort(fields, clazz, "fieldOrder");
   }

   private static List introspectArrayField(Class clazz, String fieldName)
   {
      List list = new ArrayList();
      try
      {
         java.lang.reflect.Field f = clazz.getField(fieldName);
         String[] ra = (String[]) f.get(clazz);
         list = Arrays.asList(ra);
      }
      catch (NoSuchFieldException ex) {}
      catch (IllegalAccessException ex) {}

      return list;
   }
   public static Object introspectField(Class clazz, String fieldName)
   {
      return introspectField(clazz, fieldName, null);
   }
   public static Object introspectField(Class clazz, String fieldName, Object defaultValue)
   {
      try
      {
         java.lang.reflect.Field f = clazz.getField(fieldName);
         return f.get(clazz);
      }
      catch (NoSuchFieldException ex) {}
      catch (IllegalAccessException ex) {}
      return defaultValue;
   }

   private static boolean isAtomic(PropertyDescriptor descriptor)
   {
      return (AtomicEObject.class.isAssignableFrom(descriptor.getPropertyType()));
   }

   private static boolean isIndexed(PropertyDescriptor descriptor)
   {
      return (AbstractListEO.class.isAssignableFrom(descriptor.getPropertyType()));
   }
   private static boolean isCompositeIndexed(PropertyDescriptor descriptor)
   {
      return (CompositeList.class.isAssignableFrom(descriptor.getPropertyType()));
   }

   private static boolean isAggregate(PropertyDescriptor descriptor)
   {
      if (isAtomic(descriptor)) return false;
      if (isIndexed(descriptor)) return false;
      return (descriptor.getWriteMethod() == null);
   }

   private static boolean omit(PropertyDescriptor descriptor)
   {
      if (descriptor.getReadMethod() == null) return true;
      Class clazz = descriptor.getReadMethod().getDeclaringClass();

      boolean omit = ( clazz.isAssignableFrom(AtomicEObject.class) ||
            clazz.equals(ComplexEObject.class) || clazz.equals(Object.class) );

      if (omit) return true;

      Class returnType = descriptor.getReadMethod().getReturnType();
      return (! EObject.class.isAssignableFrom(returnType));
   }


   private static List sort(List members, Class clazz, String fieldName)
   {
      try
      {
         Class dclass = clazz.getDeclaringClass();
         Class theclass = (dclass == null) ? clazz : dclass;
         if (dclass != null)
         {
            fieldName += clazz.getName().substring(clazz.getName().lastIndexOf("$")+1);
         }
         java.lang.reflect.Field f = theclass.getField(fieldName);
         String[] memberOrder = (String[]) f.get(null);
         Comparator comparator = Member.nameComparator(memberOrder);
         Collections.sort(members, comparator);
      }
      catch (NoSuchFieldException ex)
      {
         // not an aggregate
      }
      catch (IllegalAccessException ex)
      {
         // can't access
      }

      return members;
   }


   public static String makeGetterName(String fieldName)
   {
        return "get" + capitalize(fieldName);
   }
   public static String capitalize(String fieldName)
   {
        if (fieldName.length() == 0)
           return fieldName;
        char chars[] = fieldName.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
   }

}
