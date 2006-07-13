package com.u2d.persist.type;

import com.u2d.type.atom.USDollar;
import org.hibernate.HibernateException;
import org.hibernate.Hibernate;

/**
 * Date: May 24, 2005
 * Time: 7:18:05 PM
 *
 * @author Eitan Suez
 */
public class USDollarUserType extends BaseUserType
{
   public boolean equals(Object x, Object y)
   {
      if (x == null || (!(x instanceof USDollar))) return false;
      return ((USDollar) x).equals(y);
   }

   public Object nullSafeGet(java.sql.ResultSet rs, String[] names, Object owner)
      throws HibernateException, java.sql.SQLException
   {
      Double value = (Double) Hibernate.DOUBLE.nullSafeGet(rs, names[0]);
      return (value==null) ? new USDollar() : new USDollar(value.doubleValue());
   }

   public void nullSafeSet(java.sql.PreparedStatement pstmt, Object value, int index)
      throws HibernateException, java.sql.SQLException
   {
      double dblValue = (value==null) ? 0 : ((USDollar) value).doubleValue();
      Hibernate.DOUBLE.nullSafeSet(pstmt, new Double(dblValue), index);
   }

   public Class returnedClass() { return USDollar.class; }

   private static final int[] TYPES = { java.sql.Types.DOUBLE };
   public int[] sqlTypes() { return TYPES; }

}
