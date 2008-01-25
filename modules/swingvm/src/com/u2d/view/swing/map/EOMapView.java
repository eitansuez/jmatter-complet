package com.u2d.view.swing.map;

import com.u2d.type.composite.GeoPoint;
import com.u2d.model.MappableEO;
import com.u2d.view.EView;
import org.jdesktop.swingx.mapviewer.GeoPosition;

/**
 * Derived from Andres' blog entry..
 *
 * @author Andres Almiray
 * @see <a href="http://www.jroller.com/aalmiray/entry/embedding_googlemaps_into_jmatter">here</a>
 */
public class EOMapView extends BaseMapView
{
   private MappableEO _eo;
   
   public EOMapView(MappableEO eo)
   {
      _eo = eo;
      super.init(eo);
   }

   public void setupWaypoints()
   {
      GeoPoint pt = _eo.geoPosition();
      GeoPosition geoPosition = new GeoPosition(pt.getLatitude().degreesValue(), 
                                                - pt.getLongitude().degreesValue());
         
      _kit.setCenterPosition(geoPosition);
      addWaypoint(new EOWaypoint(geoPosition, _eo));
   }
}