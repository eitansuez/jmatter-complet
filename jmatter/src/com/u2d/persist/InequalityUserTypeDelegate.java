/*
 * Created on Apr 27, 2005
 */
package com.u2d.persist;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.Serializable;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;
import com.u2d.find.Inequality;
import com.u2d.find.inequalities.AbstractInequality;

/**
 * @author Eitan Suez
 */
public class InequalityUserTypeDelegate implements UserType
{
   public InequalityUserTypeDelegate() {}

   // == implementation of Hibernate UserType for Inequalities
   
   public Object deepCopy(Object value) throws HibernateException
   {
      if (value == null || (!(value instanceof Inequality))) return null;
      return ((Inequality) value).makeCopy();
   }
   public boolean equals(Object x, Object y) throws HibernateException
   {
      if (x == null || (!(x instanceof Inequality))) return false;
      return ((Inequality) x).equals(y);
   }
   public boolean isMutable() { return false; }
   public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
         throws HibernateException, SQLException
   {
      Class clazz = (Class) Hibernate.CLASS.nullSafeGet(rs, names[0]);
      if (clazz == null) return null;
      
      try
      {
         // factory for inequalities..
         return AbstractInequality.get(clazz);
      }
      catch (Exception ex)
      {
         System.err.println("Exception: "+ex.getMessage());
         ex.printStackTrace();
         return null;
      }
   }
   
   public void nullSafeSet(PreparedStatement st, Object value, int index)
         throws HibernateException, SQLException
   {
      Hibernate.CLASS.nullSafeSet(st, value.getClass(), index);
   }
   private static final int[] TYPES = { java.sql.Types.VARCHAR };
   public int[] sqlTypes() { return TYPES; }
   
   public Class returnedClass() { return Inequality.class; }

   public int hashCode(Object x) throws HibernateException
   {
      return x.toString().hashCode();
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
