package com.u2d.view.swing.map;

import com.u2d.model.MappableEO;
import com.u2d.model.AbstractListEO;
import com.u2d.type.composite.GeoPoint;
import org.jdesktop.swingx.mapviewer.GeoPosition;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jan 25, 2008
 * Time: 9:40:26 PM
 */
public class ListMapView extends BaseMapView
{
   private AbstractListEO _leo;
   
   public ListMapView(AbstractListEO leo)
   {
      _leo = leo;
      super.init(_leo);
   }

   public void setupWaypoints()
   {
      double minlon, minlat, maxlon, maxlat;
      minlon = minlat = Double.MAX_VALUE;
      maxlon = maxlat = -Double.MAX_VALUE;

      for (int i=0; i<_leo.getSize(); i++)
      {
         MappableEO eo = (MappableEO) _leo.get(i);
         GeoPoint pt = eo.geoPosition();
         GeoPosition geoPos = new GeoPosition(pt.getLatitude().degreesValue(), 
                                                   - pt.getLongitude().degreesValue());

         minlon = Math.min(geoPos.getLongitude(), minlon);
         maxlon = Math.max(geoPos.getLongitude(), maxlon);

         minlat = Math.min(geoPos.getLatitude(), minlat);
         maxlat = Math.max(geoPos.getLatitude(), maxlat);

         addWaypoint(new EOWaypoint(geoPos, eo));
      }
         
      double centerlon = (maxlon + minlon) / 2.0;
      double centerlat = (maxlat + minlat) / 2.0;
      _kit.setCenterPosition(new GeoPosition(centerlat, centerlon));
      
      // TODO: set the zoom to a proper value
//            setZoom(??);
      
   }
}