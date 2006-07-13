/*
 * Created on Apr 26, 2005
 */
package com.u2d.persist;

import java.beans.IntrospectionException;
import java.io.Serializable;

import com.u2d.element.Field;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

/**
 * @author Eitan Suez
 */
public class FieldUserType implements UserType
{
   public FieldUserType() {}
   
   // === Hibernate generic UserType implementation for Field's === //
   
   /*
    * used by restriction "api"
    */
   
   public Object deepCopy(Object obj)
   {
      if (obj == null || (!(obj instanceof Field))) return null;
      Field field = (Field) obj;
      try
      {
         return field.copy();
      }
      catch (IntrospectionException ex)
      {
         return null;
      }
   }
   
   public boolean equals(Object x, Object y)
   {
      if (x == null || (!(x instanceof Field))) return false;
      return ((Field) x).equals(y);
   }
   
   public Object nullSafeGet(java.sql.ResultSet rs, String[] names, Object owner)
      throws HibernateException, java.sql.SQLException
   {
      String fieldPath = (String) Hibernate.STRING.nullSafeGet(rs, names[0]);
      return Field.forPath(fieldPath);
   }
   
   public void nullSafeSet(java.sql.PreparedStatement pstmt, Object value, int index)
      throws HibernateException, java.sql.SQLException
   {
      Field field = (Field) value;
      if (field == null)
      {
         Hibernate.STRING.nullSafeSet(pstmt, null, index);
         return;
      }
      String fieldPath = field.getFullPath();
      Hibernate.STRING.nullSafeSet(pstmt, fieldPath, index);
   }
   
   public boolean isMutable() { return false; }
   public Class returnedClass() { return Field.class; }
   
   private static final int[] TYPES = { java.sql.Types.VARCHAR };
   public int[] sqlTypes() { return TYPES; }
   
   public static String[] COLUMNNAMES = {"fieldPath"};

   public int hashCode(Object x) throws HibernateException
   {
      return ((Field) x).hashCode();
   }

   public Serializable disassemble(Object value) throws HibernateException
   {
      return (Serializable) value;
   }

   public Object assemble(Serializable cached, Object owner) throws HibernateException
   {
      return cached;
   }

   public Object replace(Object original, Object target, Object owner) throws HibernateException
   {
      return original;
   }

}
