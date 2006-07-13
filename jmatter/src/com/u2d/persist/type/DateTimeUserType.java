package com.u2d.persist.type;

import com.u2d.type.atom.DateTime;
import org.hibernate.HibernateException;
import org.hibernate.Hibernate;

import java.util.Date;

/**
 * Date: May 24, 2005
 * Time: 7:04:42 PM
 *
 * @author Eitan Suez
 */
public class DateTimeUserType extends BaseUserType
{
   public boolean equals(Object x, Object y)
   {
      if (x == null || (!(x instanceof DateTime))) return false;
      return ((DateTime) x).equals(y);
   }

   public Object nullSafeGet(java.sql.ResultSet rs, String[] names, Object owner)
      throws HibernateException, java.sql.SQLException
   {
      Date date = (Date) Hibernate.TIMESTAMP.nullSafeGet(rs, names[0]);
      return (date==null) ? null : new DateTime(date);
   }

   public void nullSafeSet(java.sql.PreparedStatement pstmt, Object value, int index)
      throws HibernateException, java.sql.SQLException
   {
      Date date = (value==null) ? null : ((DateTime) value).dateValue();
      Hibernate.TIMESTAMP.nullSafeSet(pstmt, date, index);
   }

   public Class returnedClass() { return DateTime.class; }

   private static final int[] TYPES = { java.sql.Types.TIMESTAMP };
   public int[] sqlTypes() { return TYPES; }
}
