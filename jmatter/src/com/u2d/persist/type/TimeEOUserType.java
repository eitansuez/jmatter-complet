package com.u2d.persist.type;

import com.u2d.type.atom.TimeEO;
import org.hibernate.HibernateException;
import org.hibernate.Hibernate;
import java.util.Date;

/**
 * Date: May 24, 2005
 * Time: 7:15:50 PM
 *
 * @author Eitan Suez
 */
public class TimeEOUserType extends BaseUserType
{
   public boolean equals(Object x, Object y)
   {
      if (x == null || (!(x instanceof TimeEO))) return false;
      return ((TimeEO) x).equals(y);
   }

   public Object nullSafeGet(java.sql.ResultSet rs, String[] names, Object owner)
      throws HibernateException, java.sql.SQLException
   {
      Date value = (Date) Hibernate.TIME.nullSafeGet(rs, names[0]);
      return (value==null) ? null : new TimeEO(value.getTime());
   }

   public void nullSafeSet(java.sql.PreparedStatement pstmt, Object value, int index)
      throws HibernateException, java.sql.SQLException
   {
      Date date = (value==null) ? null : ((TimeEO) value).dateValue();
      Hibernate.TIME.nullSafeSet(pstmt, date, index);
   }

   public Class returnedClass() { return TimeEO.class; }

   private static final int[] TYPES = { java.sql.Types.TIME };
   public int[] sqlTypes() { return TYPES; }

}
