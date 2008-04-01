package com.u2d.json;

import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;
import com.u2d.model.*;
import com.u2d.element.Field;
import com.u2d.list.PlainListEObject;
import java.util.List;
import java.util.Iterator;
import java.util.Arrays;
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
   public static JSONObject json(AbstractListEO leo) throws JSONException
   {
      return json(leo, new String[] {});
   }
   public static JSONObject json(AbstractListEO leo, String... includes) throws JSONException
   {
      JSONObject jso = new JSONObject();
      jso.put("item-type", leo.type().getJavaClass().getName());
      List list = leo.getItems();
      JSONArray ra = new JSONArray();
      for (int i=0; i<list.size(); i++)
      {
         ra.put(json((ComplexEObject) list.get(i), includes));
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
    */
   public static JSONObject json(ComplexEObject eo, String... includes) throws JSONException
   {
      JSONObject obj = new JSONObject();
      obj.put("type", eo.getClass().getName());
      for (Iterator itr = eo.childFields().iterator(); itr.hasNext(); )
      {
         Field field = (Field) itr.next();

         if ( "createdOn".equals(field.name()) ||
              "deleted".equals(field.name()) ||
              "deletedOn".equals(field.name()) )
         {
            continue;
         }

         if (field.isAtomic())
         {
            obj.put(field.name(), field.get(eo).toString());
         }
         else if (field.isIndexed())
         {
            obj.put(field.name(), json((AbstractListEO) field.get(eo)));
         }
         else if (field.isAggregate())
         {
            obj.put(field.name(), json((ComplexEObject) field.get(eo)));
         }
         else if (field.isAssociation() && contains(includes, field.name()))
         {
            obj.put(field.name(), json((ComplexEObject) field.get(eo)));
         }
      }
      obj.put("id", eo.getID());
      return obj;
   }

   private static boolean contains(String[] list, String value)
   {
      return Arrays.asList(list).contains(value);
   }

   public static AbstractListEO fromJsonList(JSONObject jso)
         throws JSONException, ClassNotFoundException, ParseException
   {
      String clsname = (String) jso.get("item-type");
      Class cls = classFor(clsname);
      AbstractListEO leo = new PlainListEObject(cls);
      JSONArray ra = jso.getJSONArray("items");

      for (int i=0; i<ra.length(); i++)
      {
         jso = ra.getJSONObject(i);
         leo.add(fromJson(jso, cls));
      }

      return leo;
   }

   public static ComplexEObject fromJson(JSONObject o)
         throws JSONException, ClassNotFoundException, ParseException
   {
      return fromJson(o, null);
   }
   private static Class classFor(String clsname) throws ClassNotFoundException
   {
      return Class.forName(AbstractComplexEObject.cleanCGLIBEnhancer(clsname));
   }
   
   public static ComplexEObject fromJson(JSONObject o, Class cls)
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
               field.set(eo, fromJsonList(valueObj));
            }
            else
            {
               field.set(eo, fromJson(valueObj));
            }
         }
         else
         {
            AtomicEObject fieldValue = (AtomicEObject) field.get(eo);
            fieldValue.parseValue((String) value);
         }
      }
      return eo;
   }

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
}
