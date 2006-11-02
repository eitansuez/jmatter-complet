package com.u2d.persist.type;

import com.u2d.type.atom.IntEO;
import com.u2d.type.atom.LongEO;
import org.hibernate.HibernateException;
import org.hibernate.Hibernate;

/**
 * Date: May 24, 2005
 * Time: 7:08:08 PM
 *
 * Courtesy of oizbat
 * 
 * @author oizbat
 */
public class LongEOUserType
      extends BaseUserType
{
   public boolean equals(Object x, Object y)
   {
      if (x == null || (!(x instanceof LongEO))) return false;
      return ((LongEO) x).equals(y);
   }

   public Object nullSafeGet(java.sql.ResultSet rs, String[] names, Object owner)
      throws HibernateException, java.sql.SQLException
   {
      Long value = (Long) Hibernate.LONG.nullSafeGet(rs, names[0]);
      return (value==null) ? new LongEO() : new LongEO(value.longValue());
   }

   public void nullSafeSet(java.sql.PreparedStatement pstmt, Object value, int index)
      throws HibernateException, java.sql.SQLException
   {
      long longValue = (value==null) ? 0 : ((LongEO) value).longValue();
      Hibernate.LONG.nullSafeSet(pstmt, new Long(longValue), index);
   }

   public Class returnedClass() { return LongEO.class; }

   private static final int[] TYPES = { java.sql.Types.BIGINT };
   public int[] sqlTypes() { return TYPES; }

}
