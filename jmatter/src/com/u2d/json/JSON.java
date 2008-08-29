package com.u2d.json;

import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;
import org.hibernate.Session;
import com.u2d.model.*;
import com.u2d.element.Field;
import com.u2d.list.PlainListEObject;
import com.u2d.list.CompositeList;
import java.util.Iterator;
import java.text.ParseException;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Mar 26, 2006
 * Time: 12:26:25 PM
 */
public class JSON
{

   public static JSONObject json(AbstractListEO leo) throws JSONException
   {
      return json(leo, new String[] {});
   }
   public static JSONObject json(AbstractListEO leo, String... includes) throws JSONException
   {
      MarshalSpec spec = new MarshalSpec();
      spec.includeByValue(includes);
      return json(leo, spec);
   }
   public static JSONObject json(AbstractListEO leo, MarshalSpec spec) throws JSONException
   {
      JSONObject jso = new JSONObject();
      jso.put("item-type", leo.type().getJavaClass().getName());
      JSONArray ra = new JSONArray();
      for (int i=0; i<leo.getSize(); i++)
      {
         ra.put(json((ComplexEObject) leo.get(i), spec));
      }
      jso.put("items", ra);
      return jso;
   }

   public static JSONObject json(ComplexEObject eo)
         throws JSONException
   {
      return json(eo, new String[] {});
   }

   /**
    *
    * @param eo object to generate a json object for
    * @param includes a list of field names (applies only to to-one associations) to include in the serialization
    *        controls serialization depth to a certain extent
    * @return the serialized jsonobject
    * @throws org.json.JSONException
    */
   public static JSONObject json(ComplexEObject eo, String... includes) throws JSONException
   {
      MarshalSpec spec = new MarshalSpec();
      spec.includeByValue(includes);
      return json(eo, spec);
   }
   public static JSONObject json(ComplexEObject eo, MarshalSpec spec) throws JSONException
   {
      JSONObject obj = new JSONObject();
      obj.put("type", eo.getClass().getName());

      if (eo instanceof ComplexType)
      {
         ComplexType value = (ComplexType) eo;
         obj.put("value", value.getJavaClass().getName());
         return obj;
      }

      for (Iterator itr = eo.childFields().iterator(); itr.hasNext(); )
      {
         Field field = (Field) itr.next();

         if ( "createdOn".equals(field.name()) ||
              "deleted".equals(field.name()) ||
              "deletedOn".equals(field.name()) )
         {
            continue;
         }

         Object childObject = field.get(eo);
         if (field.isAtomic())
         {
            AtomicEObject atom = (AtomicEObject) childObject;
            obj.put(field.name(), atom.marshal());
         }
         else if (field.isIndexed())
         {
            if (field.isComposite())
            {
               CompositeList list = (CompositeList) childObject;
               assert spec != null;
               obj.put(field.name(), json(list, spec.specForComposite(field.name())));
            }
            else
            {
               AbstractListEO list = (AbstractListEO) childObject;
               assert spec != null;
               obj.put(field.name(), json(list, spec.specFor(field.name())));
            }
         }
         else if (field.isAggregate())
         {
            ComplexEObject child = (ComplexEObject) childObject;
            assert spec != null;
            obj.put(field.name(), json(child, spec.specForComposite(field.name())));
         }
         else if (field.isAssociation() && spec!=null)
         {
            ComplexEObject association = (ComplexEObject) childObject;
            if (spec.includes(field.name()))
            {
               obj.put(field.name(), json(association, spec.specFor(field.name())));
            }
            else if (spec.byRef(field.name()))
            {
               JSONObject child = new JSONObject();
               child.put("byRef", "true");
               child.put("type", association.getClass().getName());
               child.put("id", association.getID());
               obj.put(field.name(), child);
            }
         }
      }
      obj.put("id", eo.getID());
      obj.put("version", eo.getVersion());
      return obj;
   }

   public static AbstractListEO fromJsonList(JSONObject jso)
         throws JSONException, ClassNotFoundException, ParseException
   {
      return fromJsonList(jso, new EmptyDereferencer());
   }

   public static AbstractListEO fromJsonList(JSONObject jso, Session session)
         throws JSONException, ClassNotFoundException, ParseException
   {
      return fromJsonList(jso, new SessionDereferencer(session));
   }

   public static AbstractListEO fromJsonList(JSONObject jso, Dereferencer context)
         throws JSONException, ClassNotFoundException, ParseException
   {
      String clsname = (String) jso.get("item-type");
      Class cls = classFor(clsname);
      AbstractListEO leo = new PlainListEObject(cls);
      JSONArray ra = jso.getJSONArray("items");

      for (int i=0; i<ra.length(); i++)
      {
         jso = ra.getJSONObject(i);
         leo.add(fromJson(jso, cls, context));
      }

      return leo;
   }

   public static ComplexEObject fromJson(JSONObject o, Session session)
         throws JSONException, ClassNotFoundException, ParseException
   {
      return fromJson(o, new SessionDereferencer(session));
   }
   public static ComplexEObject fromJson(JSONObject o)
         throws JSONException, ClassNotFoundException, ParseException
   {
      return fromJson(o, new EmptyDereferencer());
   }
   public static ComplexEObject fromJson(JSONObject o, Dereferencer context)
         throws JSONException, ClassNotFoundException, ParseException
   {
      return fromJson(o, null, context);
   }
   private static Class classFor(String clsname) throws ClassNotFoundException
   {
      String clsName = AbstractComplexEObject.cleanCGLIBEnhancer(clsname);
      return Thread.currentThread().getContextClassLoader().loadClass(clsName);
   }

   public static ComplexEObject fromJson(JSONObject o, Class cls, Session session)
         throws JSONException, ClassNotFoundException, ParseException
   {
      return fromJson(o, cls, new SessionDereferencer(session));
   }
   public static ComplexEObject fromJson(JSONObject o, Class cls, Dereferencer context)
         throws JSONException, ClassNotFoundException, ParseException
   {
      Iterator itr = o.keys();
      if (o.has("type"))
      {
         String clsname = (String) o.get("type");
         cls = classFor(clsname);
      }
      // special case for unmarshalling complex type references..
      if (ComplexType.class.isAssignableFrom(cls))
      {
         String typeName = o.getString("value");
         return ComplexType.forClass(classFor(typeName));
      }

      if (o.optBoolean("byRef"))
      {
         Long id = o.getLong("id");
         return context.get(cls, id);
      }

      ComplexEObject eo = ComplexType.forClass(cls).instance();

      while (itr.hasNext())
      {
         String fldName = (String) itr.next();

         if ("type".equals(fldName)) continue;

         Field field = eo.field(fldName);
         Object value = o.get(fldName);

         if (field == null)  // custom handling of hibernate id properties..
         {
            if ("id".equals(fldName))
            {
               eo.setID(o.getLong(fldName));
            }
            else if ("version".equals(fldName))
            {
               eo.setVersion(o.getLong(fldName));
            }
            else
            {
               // ignore field
               System.err.println("warn:  json unmarshalling:  unmapped field name: "+fldName);
            }
         }
         else if (value instanceof JSONObject)
         {
            JSONObject valueObj = (JSONObject) value;
            if (valueObj.has("item-type"))
            {
               field.set(eo, fromJsonList(valueObj, context));
            }
            else
            {
               field.set(eo, fromJson(valueObj, context));
            }
         }
         else
         {
            AtomicEObject fieldValue = (AtomicEObject) field.get(eo);
            try
            {
               fieldValue.parseValue((String) value);
            }
            catch (ParseException ex)
            {
               String msg = String.format("Failed to parse value for field %s", field);
               System.err.println(msg);
               ex.printStackTrace();
               throw ex;
            }
         }
      }
      return eo;
   }

   // i/o related

   public static String readInputStream(InputStream is)
         throws IOException
   {
      StringBuffer sb = new StringBuffer(1024);
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));

      char[] chars = new char[1024];
      while (reader.read(chars) > -1)
      {
         sb.append(String.valueOf(chars));
      }

      reader.close();
      return sb.toString();
   }
   public static String readTextFile(String fullPathFilename)
         throws IOException
   {
      return readInputStream(new FileInputStream(fullPathFilename));
   }
   public static void writeTextFile(String data, String fullPathFilename)
         throws IOException
   {
      PrintWriter pw = new PrintWriter(new FileWriter(fullPathFilename));
      pw.print(data);
      pw.close();
   }

   public static void writeJson(File file, ComplexEObject ceo)
         throws JSONException, IOException
   {
      writeTextFile(json(ceo).toString(2), file.getAbsolutePath());
   }
   public static void writeJson(File file, AbstractListEO leo)
         throws JSONException, IOException
   {
      writeTextFile(json(leo).toString(2), file.getAbsolutePath());
   }

   public static Object readJsonFromResource(String resource) throws IOException, JSONException, ClassNotFoundException, ParseException
   {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      InputStream stream = loader.getResourceAsStream(resource);
      String jsonText = readInputStream(stream);
      return JSON.fromJson(new JSONObject(jsonText));
   }

}
