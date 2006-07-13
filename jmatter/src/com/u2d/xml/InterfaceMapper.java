package com.u2d.xml;

import org.jibx.runtime.IMarshallable;
import org.jibx.runtime.IMarshaller;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshaller;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

/*
 * A custom Marshaller for mapping interface types
 */
public class InterfaceMapper
               implements IMarshaller, IUnmarshaller
{
   public InterfaceMapper() {}
      
   public boolean isExtension(int index) { return false; }
   
   public void marshal(Object obj, IMarshallingContext ictx)
      throws JiBXException
   {
      ((IMarshallable) obj).marshal(ictx);
   }
   
   public Object unmarshal(Object obj, IUnmarshallingContext ictx)
      throws JiBXException
   {
      return ictx.unmarshalElement();
   }
   
   public boolean isPresent(IUnmarshallingContext ctx) throws JiBXException
   {
      return true;
   }
}
