package com.u2d.persist.type;

import org.hibernate.HibernateException;
import org.hibernate.Hibernate;
import com.u2d.type.atom.StringEO;

/**
 * Date: May 24, 2005
 * Time: 6:35:48 PM
 *
 * @author Eitan Suez
 */
public class StringEOUserType extends BaseUserType
{

   public boolean equals(Object x, Object y)
   {
      if (x == null || (!(x instanceof StringEO))) return false;
      return ((StringEO) x).equals(y);
   }

   public Object nullSafeGet(java.sql.ResultSet rs, String[] names, Object owner)
      throws HibernateException, java.sql.SQLException
   {
      String value = (String) Hibernate.STRING.nullSafeGet(rs, names[0]);
      return (value==null) ? null : new StringEO(value);
   }

   public void nullSafeSet(java.sql.PreparedStatement pstmt, Object value, int index)
      throws HibernateException, java.sql.SQLException
   {
      String text = "";
      if (value != null)
         text = (value instanceof String) ? (String) value : ((StringEO) value).stringValue();
      Hibernate.STRING.nullSafeSet(pstmt, text, index);
   }

   public Class returnedClass() { return StringEO.class; }

   private static final int[] TYPES = { java.sql.Types.VARCHAR };
   public int[] sqlTypes() { return TYPES; }

}
