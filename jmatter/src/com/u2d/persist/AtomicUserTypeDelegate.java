/*
 * Created on Apr 26, 2005
 */
package com.u2d.persist;

import com.u2d.model.AtomicEObject;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

import java.io.Serializable;

/**
 * @author Eitan Suez
 */
public class AtomicUserTypeDelegate implements UserType
{
   public AtomicUserTypeDelegate() {}
   
   // === Hibernate generic UserType implementation for AtomicEObject === //
   
   public Object deepCopy(Object obj) throws HibernateException
   {
      if (obj == null || (!(obj instanceof AtomicEObject))) return null;
      return ((AtomicEObject) obj).makeCopy();
   }
   
   public boolean equals(Object x, Object y) throws HibernateException
   {
      if (x == null || (!(x instanceof AtomicEObject))) return false;
      return ((AtomicEObject) x).equals(y);
   }
   
   public Object nullSafeGet(java.sql.ResultSet rs, String[] names, Object owner)
      throws HibernateException, java.sql.SQLException
   {
      String classname = (String) Hibernate.STRING.nullSafeGet(rs, names[0]);
      String value = (String) Hibernate.STRING.nullSafeGet(rs, names[1]);
      AtomicEObject aeo = null;
      try
      {
         Class clazz = Class.forName(classname);
         // 1. instantiate it:
         aeo = (AtomicEObject) clazz.newInstance();
         
         // 2. set its value:
         if (value != null)
         {
            try
            {
               aeo.parseValue(value);
            }
            catch (java.text.ParseException ex)
            {
               System.err.println("ParseException: "+ex.getMessage());
               ex.printStackTrace();
            }
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
      return aeo;
   }

   public void nullSafeSet(java.sql.PreparedStatement pstmt, Object value, int index)
      throws HibernateException, java.sql.SQLException
   {
      String classname = (value==null) ? "" : value.getClass().getName();
      String stringValue = (value==null) ? "" : ((AtomicEObject) value).toString();
      Hibernate.STRING.nullSafeSet(pstmt, classname, index);
      Hibernate.STRING.nullSafeSet(pstmt, stringValue, index + 1);
   }
   
   public boolean isMutable() { return true; }
   public Class returnedClass() { return AtomicEObject.class; }
   
   private static final int[] TYPES = { java.sql.Types.VARCHAR, java.sql.Types.VARCHAR };
   public int[] sqlTypes() { return TYPES; }

   public static String[] COLUMNNAMES = {"type", "value"};

   public int hashCode(Object x) throws HibernateException
   {
      return ((AtomicEObject) x).hashCode();
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
