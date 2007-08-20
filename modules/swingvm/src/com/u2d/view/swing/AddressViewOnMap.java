package com.u2d.view.swing;

import com.u2d.type.composite.USAddress;
import org.jdesktop.swingx.JXMapKit;
import org.jdesktop.swingx.mapviewer.*;
import org.jdesktop.swingx.mapviewer.util.GeoUtil;
import java.awt.*;
import java.io.IOException;

/**
 * Derived from Andres' blog entry..
 *
 * @author Andres Almiray
 * @see <a href="http://www.jroller.com/aalmiray/entry/embedding_googlemaps_into_jmatter">here</a>
 */
public class AddressViewOnMap extends JXMapKit
{
   private USAddress _address;
   private WaypointPainter _wpPainter;
   
   public AddressViewOnMap(USAddress addr)
   {
      _address = addr;
      putClientProperty("title", _address.title());
      
      String tileurl = "http://mt.google.com/mt?w=2.43";
      TileFactoryInfo tfi = new TileFactoryInfo(0, 20, 17, 256, true, true, tileurl, "x", "y", "zoom");
      setTileFactory(new DefaultTileFactory(tfi));

      // alternative, but unbearably slow and without street maps..
//      WMSService wms = new WMSService()
//      wms.setLayer("BMNG")
//      wms.setBaseUrl("http://wms.jpl.nasa.gov/wms.cgi?");
//      this.tileFactory = new WMSTileFactory(wms)

      _wpPainter = new WaypointPainter();
      getMainMap().setOverlayPainter(_wpPainter);
      setCenterPosition(new GeoPosition(0, 0));
      setZoom(2);
      setPreferredSize(new Dimension(400,400));
      
      new Thread()
      {
         public void run()
         {
            try
            {
               GeoPosition geoPosition = GeoUtil.getPositionForAddress(
                  _address.getAddressLine1().stringValue(), 
                     _address.getCity().stringValue(), 
                     _address.getStateCode().code()
               );
               _wpPainter.getWaypoints().add(new Waypoint(geoPosition));
               setCenterPosition(geoPosition);
            }
            catch (IOException e)
            {
               e.printStackTrace();
            }
         }
      }.start();
   }
   
}
