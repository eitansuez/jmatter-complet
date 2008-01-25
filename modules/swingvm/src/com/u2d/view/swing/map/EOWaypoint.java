package com.u2d.view.swing.map;

import com.u2d.model.EObject;
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
   EObject _eo;
   public EOWaypoint(GeoPosition geoPos, EObject eo)
   {
      super(geoPos);
      _eo = eo;
   }
   
   public EObject getEObject() { return _eo; }
}
