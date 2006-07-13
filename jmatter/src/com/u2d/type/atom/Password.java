/*
 * Created on Jan 20, 2004
 */
package com.u2d.type.atom;

import com.u2d.model.AbstractAtomicEO;
import com.u2d.model.EObject;
import com.u2d.model.Title;
import com.u2d.model.AtomicEditor;
import com.u2d.model.AtomicRenderer;
import java.security.*;

/**
 * @author Eitan Suez
 */
public class Password extends AbstractAtomicEO
{
   private String _hash;
   public static final int MINLENGTH = 5;
   
   public Password() {}
   public Password(String value, boolean isHash)
   {
      if (isHash)
         _hash = value;
      else
      {
         _hash = hashPassword(value);
      }
   }
   
   public String hashValue() { return _hash; }
   public void setValue(String value)
   {
      _hash = hashPassword(value);
      fireStateChanged();
   }
   public void setValue(EObject value)
   {
      if (!(value instanceof Password))
         throw new IllegalArgumentException("Invalid type on set;  must be Password");
      _hash = ((Password) value).hashValue();
      fireStateChanged();
   }
   
   public int validate()
   {
      return 0;
   }
   
   public Title title() { return new Title("******"); }
   public String toString() { return "******"; }
   
   public boolean isEmpty()
   {
      return isEmpty(_hash);
   }
   public static boolean isEmpty(String value)
   {
      return (value == null) || ("".equals(value.trim()));
   }
   
   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (obj == this) return true;
      if (!(obj instanceof Password)) return false;
      return _hash.equals(((Password) obj).hashValue());
   }

   public int hashCode() { return _hash.hashCode(); }

   public AtomicRenderer getRenderer() { return vmech().getPasswordRenderer(); }
   public AtomicEditor getEditor() { return vmech().getPasswordEditor(); }

   /* ** Commands ** */
   
   // possibly some kind of "reset password" command
   
   
   // ===
   
   public void parseValue(String stringValue)
   {
      if (stringValue == null) stringValue = "";
      setValue(stringValue);
   }
   
   public EObject makeCopy()
   {
      return new Password(_hash, true);
   }

   
   // =====
   
   /**
    * hash passwd password
    * @return hashed password
    */
   private static String hashPassword(String password)
   {
      StringBuffer hexrep = new StringBuffer("");
      try
      {
         MessageDigest digest = MessageDigest.getInstance("MD5");
         byte[] encoded = digest.digest(password.getBytes());
         String temp;
         for (int i = 0; i < encoded.length; i++)
         {
            temp = Integer.toHexString(encoded[i] & 0x00FF);
            hexrep.append(temp);
         }
      }
      catch (NoSuchAlgorithmException ex)
      {
         System.err.println("What?? No MD5 Digest Support?");
         ex.printStackTrace();
      }
      return hexrep.toString();
   }
   
   public static boolean match(String hash, String password)
   {
      String hash2 = hashPassword(password);
      return hash.equals(hash2);
   }



   
}
