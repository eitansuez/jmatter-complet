/*
 * Created on Apr 26, 2005
 */
package com.u2d.persist;

import com.u2d.app.PersistenceMechanism;
import com.u2d.app.Context;
import com.u2d.model.*;
import com.u2d.type.Choice;
import com.u2d.type.AbstractChoiceEO;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

import java.io.Serializable;

/**
 * @author Eitan Suez
 */
public class EObjectUserTypeDelegate implements UserType
{
   public EObjectUserTypeDelegate() {}
   
   // === Hibernate generic UserType implementation for EObject === //
   
   public Object deepCopy(Object obj) throws HibernateException
   {
      if (obj == null || (!(obj instanceof EObject))) return null;
      return ((EObject) obj).makeCopy();
   }
   
   public boolean equals(Object x, Object y) throws HibernateException
   {
      if (x == null || (!(x instanceof EObject))) return false;
      return x.equals(y);
   }
   
   public Object nullSafeGet(java.sql.ResultSet rs, String[] names, Object owner)
      throws HibernateException, java.sql.SQLException
   {
      String classname = (String) Hibernate.STRING.nullSafeGet(rs, names[0]);
      String value = (String) Hibernate.STRING.nullSafeGet(rs, names[1]);
      
      EObject eo = null;
      try
      {
         Class clazz = Thread.currentThread().getContextClassLoader().loadClass(classname);
         if (AtomicEObject.class.isAssignableFrom(clazz))
         {
            // 1. instantiate it:
            eo = (EObject) clazz.newInstance();
            // 2. set its value:
            if (value != null)
            {
               try
               {
                  ((AtomicEObject) eo).parseValue(value);
               }
               catch (java.text.ParseException ex)
               {
                  System.err.println("ParseException: "+ex.getMessage());
                  ex.printStackTrace();
               }
            }
         }
         else if (AbstractChoiceEO.class.isAssignableFrom(clazz))
         {
            if (value != null)
            {
               AbstractChoiceEO choice = (AbstractChoiceEO) clazz.newInstance();
               eo = choice.get(value);
            }
         }
         else
         {
            long id = Long.parseLong(value);
            PersistenceMechanism pmech = Context.getInstance().getPersistenceMechanism();
            eo = pmech.load(clazz, id);
         }
      }
      catch (IllegalAccessException ex)
      {
         System.err.println("IllegalAccessException: "+ex.getMessage());
         ex.printStackTrace();
      }
      catch (ClassNotFoundException ex)
      {
         System.err.println("ClassNotFoundException: "+ex.getMessage());
         ex.printStackTrace();
      }
      catch (InstantiationException ex)
      {
         System.err.println("InstantiationException: "+ex.getMessage());
         ex.printStackTrace();
      }
      return eo;
   }

   public void nullSafeSet(java.sql.PreparedStatement pstmt, Object value, int index)
      throws HibernateException, java.sql.SQLException
   {
      String classname = (value==null) ? "" : AbstractComplexEObject.cleanCGLibEnhancer(value);
      String stringValue;
      if (value instanceof AtomicEObject)
      {
         stringValue = ((AtomicEObject) value).marshal();
      }
      else if (value instanceof Choice)
      {
         stringValue = ((Choice) value).code();
      }
      else
      {
         stringValue = (value==null) ? "" : ("" + ((ComplexEObject) value).getID() );
      }
      Hibernate.STRING.nullSafeSet(pstmt, classname, index);
      Hibernate.STRING.nullSafeSet(pstmt, stringValue, index + 1);
   }
   
   public boolean isMutable() { return true; }
   public Class returnedClass() { return EObject.class; }
   
   private static final int[] TYPES = { java.sql.Types.VARCHAR, java.sql.Types.VARCHAR };
   public int[] sqlTypes() { return TYPES; }

   public static String[] COLUMNNAMES = {"type", "value"};

   public int hashCode(Object x) throws HibernateException
   {
      return x.hashCode();
   }

   public Serializable disassemble(Object value) throws HibernateException
   {
      return (Serializable) deepCopy(value);
   }

   public Object assemble(Serializable cached, Object owner) throws HibernateException
   {
      return deepCopy(cached);
   }

   public Object replace(Object original, Object target, Object owner) throws HibernateException
   {
      return deepCopy(original);
   }
}
