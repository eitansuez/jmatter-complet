package com.u2d.persist.type;

import com.u2d.type.atom.USPhone;
import org.hibernate.HibernateException;
import org.hibernate.Hibernate;

/**
 * Date: May 24, 2005
 * Time: 7:17:27 PM
 *
 * @author Eitan Suez
 */
public class USPhoneUserType extends BaseUserType
{
   public boolean equals(Object x, Object y)
   {
      if (x == null || (!(x instanceof USPhone))) return false;
      return ((USPhone) x).equals(y);
   }

   public Object nullSafeGet(java.sql.ResultSet rs, String[] names, Object owner)
      throws HibernateException, java.sql.SQLException
   {
      String value = (String) Hibernate.STRING.nullSafeGet(rs, names[0]);
      return (value==null) ? null : new USPhone(value);
   }

   public void nullSafeSet(java.sql.PreparedStatement pstmt, Object value, int index)
      throws HibernateException, java.sql.SQLException
   {
      String text = "";
      if (value != null)
         text = (value instanceof String) ? (String) value : ((USPhone) value).stringValue();
      // was:
//      String text = (value==null) ? "" : ((USPhone) value).stringValue();
      Hibernate.STRING.nullSafeSet(pstmt, text, index);
   }

   public Class returnedClass() { return USPhone.class; }

   private static final int[] TYPES = { java.sql.Types.VARCHAR };
   public int[] sqlTypes() { return TYPES; }

}
