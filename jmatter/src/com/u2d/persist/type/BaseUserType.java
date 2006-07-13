package com.u2d.persist.type;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

import java.io.Serializable;

import com.u2d.model.EObject;

/**
 * Date: May 24, 2005
 * Time: 6:56:34 PM
 *
 * @author Eitan Suez
 */
public abstract class BaseUserType implements UserType
{
   public int hashCode(Object x) throws HibernateException
   {
      return x.toString().hashCode();
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
      if (obj == null || (!(obj instanceof EObject))) return null;
      return ((EObject) obj).makeCopy();
   }

   public boolean isMutable() { return true; }
}
