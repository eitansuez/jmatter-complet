/*
 * Created on Mar 30, 2005
 */
package com.u2d.xml;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import com.u2d.type.composite.USAddress;

/**
 * @author Eitan Suez
 */
public class MarshalAddressTest
{

   public static void main(String[] args) throws IOException
   {
      try
      {
         USAddress addr = new USAddress("9300 Axtellon Ct", "Austin", "TX", "78749");
         IBindingFactory bfact = BindingDirectory.getFactory(USAddress.class);
         IMarshallingContext ctxt = bfact.createMarshallingContext();
         ctxt.setIndent(3);
         ctxt.marshalDocument(addr, "UTF-8", null, new FileWriter("addr.xml"));
         
         IUnmarshallingContext uctxt = bfact.createUnmarshallingContext();
         USAddress reconstituted = (USAddress)
                     uctxt.unmarshalDocument(new FileReader("addr.xml"));
         System.out.println("unmarshalled address..");
         System.out.println(reconstituted);
      }
      catch (JiBXException ex)
      {
         System.err.println("JiBXException: "+ex.getMessage());
         ex.printStackTrace();
      }
   }

}
