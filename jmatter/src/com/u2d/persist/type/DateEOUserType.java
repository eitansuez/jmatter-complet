package com.u2d.persist.type;

import com.u2d.type.atom.DateEO;
import org.hibernate.HibernateException;
import org.hibernate.Hibernate;

import java.util.Date;

/**
 * Date: May 24, 2005
 * Time: 7:03:53 PM
 *
 * @author Eitan Suez
 */
public class DateEOUserType extends BaseUserType
{
   public boolean equals(Object x, Object y)
   {
      if (x == null || (!(x instanceof DateEO))) return false;
      return ((DateEO) x).equals(y);
   }

   public Object nullSafeGet(java.sql.ResultSet rs, String[] names, Object owner)
      throws HibernateException, java.sql.SQLException
   {
      Date value = (Date) Hibernate.DATE.nullSafeGet(rs, names[0]);
      return (value==null) ? null : new DateEO(value);
   }

   public void nullSafeSet(java.sql.PreparedStatement pstmt, Object value, int index)
      throws HibernateException, java.sql.SQLException
   {
      Date date = (value==null) ? null : ((DateEO) value).dateValue();
      Hibernate.DATE.nullSafeSet(pstmt, date, index);
   }

   public Class returnedClass() { return DateEO.class; }

   private static final int[] TYPES = { java.sql.Types.DATE };
   public int[] sqlTypes() { return TYPES; }

}
