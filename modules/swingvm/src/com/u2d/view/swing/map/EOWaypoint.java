package com.u2d.view.swing.map;

import com.u2d.model.ComplexEObject;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.Waypoint;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 20, 2007
 * Time: 11:06:45 PM
 */
public class EOWaypoint extends Waypoint implements Mappable
{
   ComplexEObject _ceo;
   public EOWaypoint(GeoPosition geoPos, ComplexEObject ceo)
   {
      super(geoPos);
      _ceo = ceo;
   }
   
   public ComplexEObject getEObject() { return _ceo; }
}
