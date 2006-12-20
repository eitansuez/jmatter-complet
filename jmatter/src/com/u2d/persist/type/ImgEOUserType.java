package com.u2d.persist.type;

import com.u2d.type.atom.ImgEO;
import org.hibernate.HibernateException;
import org.hibernate.Hibernate;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.H2Dialect;

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

   private static int[] TYPES = { java.sql.Types.VARBINARY };
   // h2 defaults to producing a varbinary(255) which is too small;  longvarbinary on 
   // the other hand works fine.  on the other hand, postgres fails (doesn't support type -4)
   // with longvarbinary so..
   private static int[] H2_TYPES = { java.sql.Types.LONGVARBINARY };
   
   public int[] sqlTypes()
   {
      return (Dialect.getDialect() instanceof H2Dialect) ? H2_TYPES : TYPES;
   }

}
