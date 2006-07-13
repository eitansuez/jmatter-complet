package com.u2d.persist.type;

import com.u2d.type.atom.FileEO;
import org.hibernate.HibernateException;
import org.hibernate.Hibernate;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Nov 28, 2005
 * Time: 11:04:26 PM
 */
public class FileEOUserType extends BaseUserType
{
   public boolean equals(Object x, Object y)
   {
      if (x == null || (!(x instanceof FileEO))) return false;
      return ((FileEO) x).equals(y);
   }

   public Object nullSafeGet(java.sql.ResultSet rs, String[] names, Object owner)
      throws HibernateException, java.sql.SQLException
   {
      String value = (String) Hibernate.STRING.nullSafeGet(rs, names[0]);
      return (value==null) ? null : new FileEO(value);
   }

   public void nullSafeSet(java.sql.PreparedStatement pstmt, Object value, int index)
      throws HibernateException, java.sql.SQLException
   {
      String text = "";
      if (value != null)
         text = (value instanceof String) ? (String) value : ((FileEO) value).stringValue();
      Hibernate.STRING.nullSafeSet(pstmt, text, index);
   }

   public Class returnedClass() { return FileEO.class; }

   private static final int[] TYPES = { java.sql.Types.VARCHAR };
   public int[] sqlTypes() { return TYPES; }
}
