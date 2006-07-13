package com.u2d.persist.type;

import com.u2d.element.EOCommand;
import com.u2d.model.ComplexType;
import org.hibernate.HibernateException;
import org.hibernate.Hibernate;
import org.hibernate.usertype.UserType;

import java.io.Serializable;

/**
 * Date: May 24, 2005
 * Time: 7:27:36 PM
 *
 * @author Eitan Suez
 */
public class EOCommandUserType implements UserType
{
   /* Implementation of Hibernate UserType */

   public Object deepCopy(Object obj)
   {
      if (obj == null || (!(obj instanceof EOCommand))) return null;
      EOCommand cmd = (EOCommand) obj;
      return cmd.copy();
   }

   public boolean equals(Object x, Object y)
   {
      if (x == null || (!(x instanceof EOCommand))) return false;
      return ((EOCommand) x).equals(y);
   }

   public Object nullSafeGet(java.sql.ResultSet rs, String[] names, Object owner)
      throws HibernateException, java.sql.SQLException
   {
      String className = (String) Hibernate.STRING.nullSafeGet(rs, names[0]);
      String commandName = (String) Hibernate.STRING.nullSafeGet(rs, names[1]);

      if ((className == null) || (commandName == null)) return null;

      try
      {
         Class cls = Class.forName(className);
         ComplexType type = ComplexType.forClass(cls);
         return (EOCommand) type.findCommand(commandName);
      }
      catch (ClassNotFoundException ex)
      {
         System.err.println("ClassNotFoundException: "+ex.getMessage());
         ex.printStackTrace();
      }
      return null;
   }

   public void nullSafeSet(java.sql.PreparedStatement pstmt, Object value, int index)
      throws HibernateException, java.sql.SQLException
   {
      EOCommand cmd = (EOCommand) value;
      if (cmd == null)
      {
         Hibernate.STRING.nullSafeSet(pstmt, null, index);
         Hibernate.STRING.nullSafeSet(pstmt, null, index+1);
         return;
      }
      String commandName = cmd.name();
      String className = cmd.parent().getJavaClass().getName();
      Hibernate.STRING.nullSafeSet(pstmt, className, index);
      Hibernate.STRING.nullSafeSet(pstmt, commandName, index+1);
   }

   public boolean isMutable() { return false; }
   public Class returnedClass() { return EOCommand.class; }

   private static final int[] TYPES = { java.sql.Types.VARCHAR, java.sql.Types.VARCHAR };
   public int[] sqlTypes() { return TYPES; }

   public int hashCode(Object x) throws HibernateException
   {
      return ((EOCommand) x).hashCode();
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

   public static String[] COLUMNNAMES = {"className", "commandName"};

}
