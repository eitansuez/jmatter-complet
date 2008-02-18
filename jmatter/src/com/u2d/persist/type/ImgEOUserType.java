package com.u2d.persist.type;

import com.u2d.type.atom.ImgEO;
import org.hibernate.HibernateException;
import org.hibernate.Hibernate;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.MySQLDialect;

import javax.swing.*;

/**
 * Date: May 24, 2005
 * Time: 7:08:55 PM
 *
 * @author Eitan Suez
 */
public class ImgEOUserType extends BaseUserType
{
   public boolean equals(Object x, Object y)
   {
      if (x == null || (!(x instanceof ImgEO))) return false;
      return ((ImgEO) x).equals(y);
   }

   public Object nullSafeGet(java.sql.ResultSet rs, String[] names, Object owner)
      throws HibernateException, java.sql.SQLException
   {
      ImageIcon value = (ImageIcon) Hibernate.SERIALIZABLE.nullSafeGet(rs, names[0]);
      return (value==null) ? new ImgEO() : new ImgEO(value);
   }

   public void nullSafeSet(java.sql.PreparedStatement pstmt, Object value, int index)
      throws HibernateException, java.sql.SQLException
   {
      ImageIcon imgValue = null;
      if (value != null)
      {
         ImgEO eo = (ImgEO) value;
         if (!eo.isEmpty())
            imgValue = eo.imageValue();
      }
      Hibernate.SERIALIZABLE.nullSafeSet(pstmt, imgValue, index);
   }

   public Class returnedClass() { return ImgEO.class; }

   // Types.BLOB works for h2 and mysql but not for other rdbms's, so...
   // h2 however produces a varbinary(255) which is too small;
   // mysql produces a tinyblob which also is too small
   private static int[] BLOB_TYPE = { java.sql.Types.BLOB };
   private static int[] TYPES = { java.sql.Types.VARBINARY };
   
   public int[] sqlTypes()
   {
      Dialect dialect = Dialect.getDialect();
      if (dialect instanceof H2Dialect || dialect instanceof MySQLDialect)
      {
         return BLOB_TYPE;
      }
      else
      {
         return TYPES;
      }
   }

}
