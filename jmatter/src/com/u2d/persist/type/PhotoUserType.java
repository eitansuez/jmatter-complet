package com.u2d.persist.type;

import com.u2d.type.atom.Photo;
import com.u2d.type.atom.ImgEO;
import org.hibernate.HibernateException;
import org.hibernate.Hibernate;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.H2Dialect;

import javax.swing.*;

/**
 * Date: May 24, 2005
 * Time: 7:12:31 PM
 *
 * @author Eitan Suez
 */
public class PhotoUserType extends ImgEOUserType
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

   public Class returnedClass() { return Photo.class; }

}
