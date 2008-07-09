package com.u2d.json;

import junit.framework.TestCase;
import com.u2d.domain.Shipment;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Jul 9, 2008
 * Time: 4:15:15 PM
 */
public class JSONTest extends TestCase
{
   public void testSimpleMarshaling() throws JSONException
   {
      Shipment shipment = new Shipment("my shipment", (float) 3.5);
      JSONObject obj = JSON.json(shipment);
      System.out.println(obj.toString(2));
   }
}
