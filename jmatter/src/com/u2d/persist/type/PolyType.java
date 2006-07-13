package com.u2d.persist.type;

import org.hibernate.HibernateException;
import org.hibernate.Hibernate;
import org.hibernate.usertype.UserType;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Sep 14, 2005
 * Time: 2:05:50 PM
 */
public class PolyType implements UserType
{
   public boolean equals(Object x, Object y)
   {
      if (x == null || (!(x instanceof String))) return false;
      return ((String) x).equals(y);
   }

   public Object nullSafeGet(java.sql.ResultSet rs, String[] names, Object owner)
      throws HibernateException, java.sql.SQLException
   {
      String value = (String) Hibernate.STRING.nullSafeGet(rs, names[0]);
      if (value == null) return null;
      try
      {
         long longValue = Long.parseLong(value);
         return new Long(longValue);
      }
      catch (NumberFormatException ex)
      {
         return value;
      }
   }

   public void nullSafeSet(java.sql.PreparedStatement pstmt, Object value, int index)
      throws HibernateException, java.sql.SQLException
   {
      String text = "";
      if (value != null)
      {
         if (value instanceof String)
            text = (String) value;
         else if (value instanceof Long)
            text = "" + ((Long) value).longValue();
      }
      Hibernate.STRING.nullSafeSet(pstmt, text, index);
   }

   public Class returnedClass() { return String.class; }

   private static final int[] TYPES = { java.sql.Types.VARCHAR };
   public int[] sqlTypes() { return TYPES; }

   
   
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

   public Object deepCopy(Object obj)
   {
      if (obj == null) return null;
      if (obj instanceof Long)
      {
         long value = ((Long) obj).longValue();
         return new Long(value);
      }
      else
      {
         return new String((String) obj);
      }
   }

   public boolean isMutable() { return false; }

}