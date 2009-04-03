package com.u2d.persist.type;

import com.u2d.type.atom.ImgEO;
import org.hibernate.HibernateException;
import org.hibernate.Hibernate;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.H2Dialect;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

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
      return x.equals(y);
   }

   public Object nullSafeGet(java.sql.ResultSet rs, String[] names, Object owner)
      throws HibernateException, java.sql.SQLException
   {
      byte[] buf = (byte[]) Hibernate.BINARY.nullSafeGet(rs, names[0]);
      if (buf != null)
      {
         ByteArrayInputStream src = new ByteArrayInputStream(buf);
         try
         {
            BufferedImage img = ImageIO.read(src);
            if (img != null)
            {
               ImageIcon icon = new ImageIcon(img);
               return new ImgEO(icon);
            }
         }
         catch (IOException e)
         {
         }
      }
      return new ImgEO();
   }

   public void nullSafeSet(java.sql.PreparedStatement pstmt, Object value, int index)
      throws HibernateException, java.sql.SQLException
   {
      byte[] bytes = null;
      if (value != null)
      {
         ImgEO eo = (ImgEO) value;
         if (!eo.isEmpty())
         {
            BufferedImage img = eo.bufferedImageValue();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try
            {
               ImageIO.write(img, "png", baos);
               bytes = baos.toByteArray();
               baos.close();
            }
            catch (IOException e)
            {
               e.printStackTrace();
            }
         }
      }
      Hibernate.BINARY.nullSafeSet(pstmt, bytes, index);
   }

   public Class returnedClass() { return ImgEO.class; }

   public int[] sqlTypes()
   {
      Dialect dialect = Dialect.getDialect();
      if (dialect instanceof MySQLDialect || dialect.getClass().getName().startsWith("org.hibernate.dialect.Oracle"))
      {
         return new int[] { java.sql.Types.BLOB };
      }
      else if (dialect instanceof H2Dialect)
      {
         return new int[] { java.sql.Types.LONGVARBINARY };
      }
      else
      {
         return new int[] { java.sql.Types.VARBINARY };
      }
   }

}
