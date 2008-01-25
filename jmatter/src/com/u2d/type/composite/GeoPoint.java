package com.u2d.type.composite;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.GeoValue;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jan 25, 2008
 * Time: 8:44:12 PM
 */
public class GeoPoint extends AbstractComplexEObject
{
   public static String[] fieldOrder = {"latitude", "longitude"};
   
   private final GeoValue _latitude = new GeoValue();
   private final GeoValue _longitude = new GeoValue();
   
   public GeoPoint() {}
   
   public GeoValue getLatitude() { return _latitude; }
   public GeoValue getLongitude() { return _longitude; }

   public String toString()
   {
      return String.format("(%s,%s)", _latitude, _longitude);
   }

   public Title title()
   {
      return new Title(toString());
   }
   
   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (obj == this) return true;
      if (!(obj instanceof GeoPoint)) return false;
      GeoPoint pt = (GeoPoint) obj;
      return _latitude.equals(pt.getLatitude()) && _longitude.equals(pt.getLongitude());
   }

   public int hashCode()
   {
      return _latitude.hashCode() + 31 * _longitude.hashCode();
   }
   
}
