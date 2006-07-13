package com.u2d.persist.type;

import com.u2d.type.atom.Photo;
import com.u2d.type.atom.ImgEO;
import org.hibernate.HibernateException;
import org.hibernate.Hibernate;

import javax.swing.*;

/**
 * Date: May 24, 2005
 * Time: 7:12:31 PM
 *
 * @author Eitan Suez
 */
public class PhotoUserType extends BaseUserType
{
   public boolean equals(Object x, Object y)
   {
      if (x == null || (!(x instanceof Photo))) return false;
      return ((Photo) x).equals(y);
   }

   public Object nullSafeGet(java.sql.ResultSet rs, String[] names, Object owner)
      throws HibernateException, java.sql.SQLException
   {
      ImageIcon value = (ImageIcon) Hibernate.SERIALIZABLE.nullSafeGet(rs, names[0]);
      return (value==null) ? new Photo() : new Photo(value);
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

   public Class returnedClass() { return Photo.class; }

   private static final int[] TYPES = { java.sql.Types.VARBINARY };
   public int[] sqlTypes() { return TYPES; }

}
