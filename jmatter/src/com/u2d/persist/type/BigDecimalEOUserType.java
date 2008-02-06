package com.u2d.persist.type;

import com.u2d.type.atom.BigDecimalEO;
import org.hibernate.HibernateException;
import org.hibernate.Hibernate;
import java.math.BigDecimal;

/**
 * Date: May 24, 2005
 * Time: 7:07:04 PM
 *
 * @author Eitan Suez
 */
public class BigDecimalEOUserType
      extends BaseUserType
{

   public boolean equals(Object x, Object y)
   {
      if (x == null || (!(x instanceof BigDecimalEO))) return false;
      return ((BigDecimalEO) x).equals(y);
   }

   public Object nullSafeGet(java.sql.ResultSet rs, String[] names, Object owner)
      throws HibernateException, java.sql.SQLException
   {
      BigDecimal value = (BigDecimal) Hibernate.BIG_DECIMAL.nullSafeGet(rs, names[0]);
      return (value==null) ? new BigDecimalEO() : new BigDecimalEO(value);
   }

   public void nullSafeSet(java.sql.PreparedStatement pstmt, Object value, int index)
      throws HibernateException, java.sql.SQLException
   {
      BigDecimal bdValue = (value==null) ? new BigDecimal(0) : ((BigDecimalEO) value).getValue();
      Hibernate.BIG_DECIMAL.nullSafeSet(pstmt, bdValue, index);
   }

   public Class returnedClass() { return BigDecimalEO.class; }

   private static final int[] TYPES = { java.sql.Types.DECIMAL };
   public int[] sqlTypes() { return TYPES; }
}