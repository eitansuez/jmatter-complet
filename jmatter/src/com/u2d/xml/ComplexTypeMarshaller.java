package com.u2d.xml;

import org.jibx.runtime.*;
import org.jibx.runtime.impl.MarshallingContext;
import org.jibx.runtime.impl.UnmarshallingContext;

import java.util.Map;
import java.util.Iterator;
import java.util.HashMap;

import com.u2d.model.ComplexType;

/*
 * A custom Marshaller for mapping interface types
 */
public class ComplexTypeMarshaller
               implements IMarshaller, IUnmarshaller, IAliasable
{
   private String _uri;
   private int _index;
   private String _name;

   public ComplexTypeMarshaller()
   {
      _uri = null;
      _index = 0;
      _name = "type";
   }
   
   public ComplexTypeMarshaller(String uri, int index, String name)
   {
      _uri = uri;
      _index = index;
      _name = name;
   }

   public boolean isExtension(int index) { return false; }

   public void marshal(Object obj, IMarshallingContext ictx)
         throws JiBXException
   {
      if (!(obj instanceof ComplexType))
      {
         throw new JiBXException("Invalid object type for marshaller");
      }
      else if (!(ictx instanceof MarshallingContext))
      {
         throw new JiBXException("Invalid object type for marshaller");
      }

      MarshallingContext ctx = (MarshallingContext) ictx;
      ComplexType type = (ComplexType) obj;

      ctx.startTagAttributes(_index, _name).closeStartContent();
      ctx.content(type.getJavaClass().getName());
      ctx.endTag(_index, _name);
   }

   public Object unmarshal(Object obj, IUnmarshallingContext ictx)
      throws JiBXException
   {
      UnmarshallingContext ctx = (UnmarshallingContext) ictx;
      if (!ctx.isAt(_uri, _name)) {
          ctx.throwStartTagNameError(_uri, _name);
      }
        
      ctx.parsePastStartTag(_uri, _name);
      String clsName = ctx.parseContentText();
      ctx.parsePastEndTag(_uri, _name);
      
      try
      {
         return ComplexType.forClass(Class.forName(clsName));
      }
      catch (ClassNotFoundException ex)
      {
         throw new JiBXException("ClassNotFound ("+clsName+")", ex);
      }
   }
   
   public boolean isPresent(IUnmarshallingContext ctx) throws JiBXException
   {
      return ctx.isAt(_uri, _name);
   }
}
