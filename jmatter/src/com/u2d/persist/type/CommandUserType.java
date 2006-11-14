package com.u2d.persist.type;

import com.u2d.element.EOCommand;
import com.u2d.element.Command;
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
public class CommandUserType
      implements UserType
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
      String commandPath = (String) Hibernate.STRING.nullSafeGet(rs, names[0]);
      return EOCommand.forPath(commandPath);
   }

   public void nullSafeSet(java.sql.PreparedStatement pstmt, Object value, int index)
      throws HibernateException, java.sql.SQLException
   {
      Command cmd = (Command) value;
      if (cmd == null)
      {
         Hibernate.STRING.nullSafeSet(pstmt, null, index);
         return;
      }
      Hibernate.STRING.nullSafeSet(pstmt, cmd.fullPath(), index);
   }

   public boolean isMutable() { return false; }
   public Class returnedClass() { return EOCommand.class; }

   private static final int[] TYPES = { java.sql.Types.VARCHAR };
   public int[] sqlTypes() { return TYPES; }

   public static String[] COLUMNNAMES = {"commandPath"};

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

}
