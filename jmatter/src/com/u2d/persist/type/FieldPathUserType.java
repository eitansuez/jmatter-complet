package com.u2d.persist.type;

import org.hibernate.HibernateException;
import org.hibernate.Hibernate;
import com.u2d.find.FieldPath;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

/**
 * Date: May 24, 2005
 * Time: 7:35:34 PM
 *
 * @author Eitan Suez
 */
public class FieldPathUserType extends BaseUserType
{
   public boolean equals(Object x, Object y) throws HibernateException
   {
      if (x == null || (!(x instanceof FieldPath))) return false;
      return ((FieldPath) x).equals(y);
   }
   public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
         throws HibernateException, SQLException
   {
      String value = (String) Hibernate.STRING.nullSafeGet(rs, names[0]);
      return (value==null) ? null : new FieldPath(value);
   }
   public void nullSafeSet(PreparedStatement st, Object value, int index)
         throws HibernateException, SQLException
   {
      String text = "";
      if (value != null)
         text = (value instanceof String) ? (String) value : ((FieldPath) value).getPathString();
      Hibernate.STRING.nullSafeSet(st, text, index);
   }
   private static final int[] TYPES = { java.sql.Types.VARCHAR };
   public int[] sqlTypes() { return TYPES; }

   public Class returnedClass() { return FieldPath.class; }

}
