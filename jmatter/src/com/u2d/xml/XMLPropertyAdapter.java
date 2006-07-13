/*
 * Created on Apr 29, 2004
 */
package com.u2d.xml;

import com.u2d.model.AtomicEObject;
import com.u2d.type.atom.*;

/**
 * @author Eitan Suez
 */
public class XMLPropertyAdapter
{
   public static String serialize(StringEO aeo)
   {
      return generalSer(aeo);
   }
   public static StringEO deserializeString(String text)
   {
      return (StringEO) generalDeser(new StringEO(), text);
   }
   
   public static String serialize(TextEO aeo)
   {
      return generalSer(aeo);
   }
   public static TextEO deserializeText(String text)
   {
      return (TextEO) generalDeser(new TextEO(), text);
   }
   
   public static String serialize(USZipCode aeo)
   {
      return generalSer(aeo);
   }
   public static USZipCode deserializeUSZipCode(String text)
   {
      return (USZipCode) generalDeser(new USZipCode(), text);
   }
   
   public static String serialize(USPhone aeo)
   {
      return generalSer(aeo);
   }
   public static USPhone deserializeUSPhone(String text)
   {
      return (USPhone) generalDeser(new USPhone(), text);
   }

   public static String serialize(Email aeo)
   {
      return generalSer(aeo);
   }
   public static Email deserializeEmail(String text)
   {
      return (Email) generalDeser(new Email(), text);
   }

   public static String serialize(URI aeo)
   {
      return generalSer(aeo);
   }
   public static URI deserializeURI(String text)
   {
      return (URI) generalDeser(new URI(), text);
   }

   public static String serialize(USDollar aeo)
   {
      return generalSer(aeo);
   }
   public static USDollar deserializeUSDollar(String text)
   {
      return (USDollar) generalDeser(new USDollar(), text);
   }
   
   public static String serialize(BooleanEO aeo)
   {
      return generalSer(aeo);
   }
   public static BooleanEO deserializeBoolean(String text)
   {
      return (BooleanEO) generalDeser(new BooleanEO(), text);
   }
   
   public static String serialize(IntEO aeo)
   {
      return generalSer(aeo);
   }
   public static IntEO deserializeInt(String text)
   {
      return (IntEO) generalDeser(new IntEO(), text);
   }

   // ===
   
   private static String generalSer(AtomicEObject aeo)
   {
      return aeo.title().toString();
   }
   private static AtomicEObject generalDeser(AtomicEObject aeo, String text)
   {
      try
      {
         aeo.parseValue(text);
      }
      catch (java.text.ParseException ex)
      {
         System.err.println("Parse Exception: "+ex.getMessage());
      }
      return aeo;
   }
}
