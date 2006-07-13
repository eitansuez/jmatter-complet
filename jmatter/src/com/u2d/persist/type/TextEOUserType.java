package com.u2d.persist.type;

import com.u2d.type.atom.TextEO;
import org.hibernate.HibernateException;
import org.hibernate.Hibernate;

/**
 * Date: May 24, 2005
 * Time: 7:14:53 PM
 *
 * @author Eitan Suez
 */
public class TextEOUserType extends BaseUserType
{
   public boolean equals(Object x, Object y)
   {
      if (x == null || (!(x instanceof TextEO))) return false;
      return ((TextEO) x).equals(y);
   }

   public Object nullSafeGet(java.sql.ResultSet rs, String[] names, Object owner)
      throws HibernateException, java.sql.SQLException
   {
      String value = (String) Hibernate.STRING.nullSafeGet(rs, names[0]);
      return (value==null) ? null : new TextEO(value);
   }

   public void nullSafeSet(java.sql.PreparedStatement pstmt, Object value, int index)
      throws HibernateException, java.sql.SQLException
   {
      String text = "";
      if (value != null)
         text = (value instanceof String) ? (String) value : ((TextEO) value).stringValue();
      // was:
//      String text = (value==null) ? "" : ((TextEO) value).stringValue();
      Hibernate.STRING.nullSafeSet(pstmt, text, index);
   }

   public Class returnedClass() { return TextEO.class; }

   private static final int[] TYPES = { java.sql.Types.CLOB };
   public int[] sqlTypes() { return TYPES; }

}
