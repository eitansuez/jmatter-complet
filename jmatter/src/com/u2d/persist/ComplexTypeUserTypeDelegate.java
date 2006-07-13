/*
 * Created on Apr 26, 2005
 */
package com.u2d.persist;

import com.u2d.model.ComplexType;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

import java.io.Serializable;

/**
 * @author Eitan Suez
 */
public class ComplexTypeUserTypeDelegate implements UserType
{
   public ComplexTypeUserTypeDelegate() {}
   
   // ====== complextypes should be persisted by their classname ===== //
   
   /*
    * used by Query objects (CompositeQuery and SimpleQuery)
    */
   
   public Object deepCopy(Object obj)
   {
      if (obj == null || (!(obj instanceof ComplexType))) return null;
      return obj;
   }
   
   public boolean equals(Object x, Object y)
   {
      if (x == null || (!(x instanceof ComplexType))) return false;
      return ((ComplexType) x).equals(y);
   }
   
   public Object nullSafeGet(java.sql.ResultSet rs, String[] names, Object owner)
      throws HibernateException, java.sql.SQLException
   {
      String className = (String) Hibernate.STRING.nullSafeGet(rs, names[0]);
      if (className == null) return null;
      
      try
      {
         Class targetClass = Class.forName(className);
         return ComplexType.forClass(targetClass);
      }
      catch (ClassNotFoundException ex)
      {
         System.err.println("ClassNotFoundException: "+ex.getMessage());
         ex.printStackTrace();
         return null;
      }
   }

   public void nullSafeSet(java.sql.PreparedStatement pstmt, Object value, int index)
      throws HibernateException, java.sql.SQLException
   {
      ComplexType type = (ComplexType) value;
      if (type == null || type.getJavaClass() == null )
      {
         Hibernate.STRING.nullSafeSet(pstmt, null, index);
         return;
      }
      Class clazz = type.getJavaClass();
      String className = clazz.getName();
      Hibernate.STRING.nullSafeSet(pstmt, className, index);
   }
   
   
   public boolean isMutable() { return false; }
   public Class returnedClass() { return ComplexType.class; }
   
   private static final int[] TYPES = { java.sql.Types.VARCHAR };
   public int[] sqlTypes() { return TYPES; }

   public int hashCode(Object x) throws HibernateException
   {
      return ((ComplexType) x).hashCode();
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
