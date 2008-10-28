package com.u2d.type.atom;

import com.u2d.model.*;
import com.u2d.ui.LocatableIcon;
import javax.swing.*;
import java.awt.*;
import java.io.*;

import org.apache.commons.codec.binary.Base64;

/**
 * @author Eitan Suez
 */
public class ImgEO extends AbstractAtomicEO
{
   protected ImageIcon _value;

   public ImgEO() { _value = nullIcon(); }
   public ImgEO(ImageIcon value) { _value = value; }

   public Object getValue() { return _value; }
   public ImageIcon imageValue() { return _value; }

   public void setValue(ImageIcon value)
   {
      _value = value;
      fireStateChanged();
   }
   public void setValue(EObject value)
   {
      if (!(value instanceof ImgEO))
         throw new IllegalArgumentException("Invalid type on set;  must be ImgEO");
      if (this.equals(value)) return;
      setValue(((ImgEO) value).imageValue());
   }

   public Title title() {  return new Title(""); }

   public boolean isEmpty()
   {
      return (_value == null || _value == nullIcon());
   }

   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (this == obj) return true;
      if (!(obj instanceof ImgEO)) return false;
      ImgEO img = (ImgEO) obj;
      return (_value.equals(img.getValue()));
   }

   public int hashCode() { return _value.hashCode(); }

   public String toString()
   {
      if (isEmpty()) return "";
      try
      {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(baos);
         oos.writeObject(_value);
         byte[] bytes = baos.toByteArray();
         oos.close();
         baos.close();
         return new String(Base64.encodeBase64(bytes));
      }
      catch (IOException e)
      {
         e.printStackTrace();
         return "";
      }
   }

   public AtomicRenderer getRenderer() { return vmech().getImageRenderer(); }
   public AtomicEditor getEditor() { return vmech().getImageEditor(); }

   // attempt to resolve stringValue as a path or url
   public void parseValue(String stringValue)
   {
      if (StringEO.isEmpty(stringValue))
      {
         setValue(nullIcon());
         return;
      }

      try
      {
         java.net.URL url = new java.net.URL(stringValue);
         setValue(new ImageIcon(url));
      }
      catch (java.net.MalformedURLException ex)
      {
         // assume next that perhaps string is base64-encoding of serialized imageicon..
         try
         {
            byte[] buf = Base64.decodeBase64(stringValue.getBytes());
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(buf));
            ImageIcon icon = (ImageIcon) ois.readObject();
            setValue(icon);
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
         catch (ClassNotFoundException e)
         {
            e.printStackTrace();
         }
      }
   }

   public EObject makeCopy() { return new ImgEO(imageValue()); }

   public static LocatableIcon NULL_ICON = null;

   public synchronized LocatableIcon nullIcon()
   {
      if (NULL_ICON == null)
      {
         try
         {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            java.net.URL imgurl = loader.getResource(nullIconResourcePath());
            NULL_ICON = new LocatableIcon(imgurl);
         }
         catch (Exception ex)
         {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
         }
      }
      return NULL_ICON;
   }

   public String nullIconResourcePath() { return "images/logo_32.png"; }
   public String emptyCaption() { return "No image"; }
   public Image processRawIcon(ImageIcon icon)
   {
      return icon.getImage();  // noop
   }

}
   
