package com.u2d.persist.type;

import com.u2d.type.atom.BooleanEO;
import org.hibernate.HibernateException;
import org.hibernate.Hibernate;

/**
 * Date: May 24, 2005
 * Time: 6:59:34 PM
 *
 * @author Eitan Suez
 */
public class BooleanEOUserType extends BaseUserType
{
   public boolean equals(Object x, Object y)
   {
      if (x == null || (!(x instanceof BooleanEO))) return false;
      return ((BooleanEO) x).equals(y);
   }

   public Object nullSafeGet(java.sql.ResultSet rs, String[] names, Object owner)
      throws HibernateException, java.sql.SQLException
   {
      Boolean value = (Boolean) Hibernate.BOOLEAN.nullSafeGet(rs, names[0]);
      return (value==null) ? new BooleanEO() : new BooleanEO(value.booleanValue());
   }

   public void nullSafeSet(java.sql.PreparedStatement pstmt, Object value, int index)
      throws HibernateException, java.sql.SQLException
   {
      boolean booleanValue = (value==null) ? false : ((BooleanEO) value).booleanValue();
      Hibernate.BOOLEAN.nullSafeSet(pstmt, new Boolean(booleanValue), index);
   }

   public Class returnedClass() { return BooleanEO.class; }

   private static final int[] TYPES = { java.sql.Types.BIT };
   public int[] sqlTypes() { return TYPES; }
}
