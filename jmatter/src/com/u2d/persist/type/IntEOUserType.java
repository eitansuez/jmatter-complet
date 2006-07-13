package com.u2d.persist.type;

import com.u2d.type.atom.IntEO;
import org.hibernate.HibernateException;
import org.hibernate.Hibernate;

/**
 * Date: May 24, 2005
 * Time: 7:08:08 PM
 *
 * @author Eitan Suez
 */
public class IntEOUserType extends BaseUserType
{
   public boolean equals(Object x, Object y)
   {
      if (x == null || (!(x instanceof IntEO))) return false;
      return ((IntEO) x).equals(y);
   }

   public Object nullSafeGet(java.sql.ResultSet rs, String[] names, Object owner)
      throws HibernateException, java.sql.SQLException
   {
      Integer value = (Integer) Hibernate.INTEGER.nullSafeGet(rs, names[0]);
      return (value==null) ? new IntEO() : new IntEO(value.intValue());
   }

   public void nullSafeSet(java.sql.PreparedStatement pstmt, Object value, int index)
      throws HibernateException, java.sql.SQLException
   {
      int intValue = (value==null) ? 0 : ((IntEO) value).intValue();
      Hibernate.INTEGER.nullSafeSet(pstmt, new Integer(intValue), index);
   }

   public Class returnedClass() { return IntEO.class; }

   private static final int[] TYPES = { java.sql.Types.INTEGER };
   public int[] sqlTypes() { return TYPES; }

}
