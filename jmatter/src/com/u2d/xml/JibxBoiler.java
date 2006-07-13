/*
 * Created on Apr 29, 2004
 */
package com.u2d.xml;

import org.jibx.runtime.*;
import java.io.*;
import com.u2d.type.atom.*;

/**
 * @author Eitan Suez
 */
public class JibxBoiler
{
   IBindingFactory _bfact;
   
   public JibxBoiler(Class clazz) throws JiBXException
   {
      _bfact = BindingDirectory.getFactory(clazz);
   }
   
   public void marshal(Object obj) throws JiBXException, FileNotFoundException
   {
      marshal(obj, "marshalled.xml");
   }
   public void marshal(Object obj, String filename) throws JiBXException, FileNotFoundException
   {
      if (StringEO.isEmpty(filename))
         throw new IllegalArgumentException("Filename cannot be blank");
      
      IMarshallingContext mctx = _bfact.createMarshallingContext();
      mctx.setIndent(3);
      mctx.marshalDocument(obj, "UTF-8", null, new FileOutputStream(filename));
   }
   
   public Object unmarshal() throws JiBXException, FileNotFoundException
   {
      return unmarshal("marshalled.xml");
   }
   public Object unmarshal(String filename) throws JiBXException, FileNotFoundException
   {
      if (StringEO.isEmpty(filename))
         throw new IllegalArgumentException("Filename cannot be blank");
      
      IUnmarshallingContext uctx = _bfact.createUnmarshallingContext();
      return uctx.unmarshalDocument(new FileInputStream(filename), null);
   }
   
}
