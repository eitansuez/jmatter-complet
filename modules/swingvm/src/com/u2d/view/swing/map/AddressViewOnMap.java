package com.u2d.view.swing.map;

import com.u2d.type.composite.USAddress;
import com.u2d.view.swing.SwingViewMechanism;
import com.u2d.view.swing.SwingAction;
import org.jdesktop.swingx.mapviewer.DefaultTileFactory;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;
import org.jdesktop.swingx.mapviewer.util.GeoUtil;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Derived from Andres' blog entry..
 *
 * @author Andres Almiray
 * @see <a href="http://www.jroller.com/aalmiray/entry/embedding_googlemaps_into_jmatter">here</a>
 */
public class AddressViewOnMap extends MapView
{
   private USAddress _address;
   
   public AddressViewOnMap(USAddress addr)
   {
      _address = addr;
      putClientProperty("title", _address.title());
      
      String tileurl = "http://mt.google.com/mt?w=2.43";
      TileFactoryInfo tfi = new TileFactoryInfo(0, 20, 17, 256, true, true, tileurl, "x", "y", "zoom");
      _kit.setTileFactory(new DefaultTileFactory(tfi));

      // alternative, but unbearably slow and without street maps..
//      WMSService wms = new WMSService()
//      wms.setLayer("BMNG")
//      wms.setBaseUrl("http://wms.jpl.nasa.gov/wms.cgi?");
//      this.tileFactory = new WMSTileFactory(wms)

      _kit.setCenterPosition(new GeoPosition(0, 0));
      _kit.setZoom(2);
      _kit.setPreferredSize(new Dimension(600,400));
      
      SwingViewMechanism.invokeSwingAction(new SwingAction()
      {
         GeoPosition geoPosition;
         
         public void offEDT()
         {
            try
            {
               geoPosition = GeoUtil.getPositionForAddress(
                        _address.getAddressLine1().stringValue(), 
                        _address.getCity().stringValue(), 
                        _address.getStateCode().code()
                  );
            }
            catch (IOException e)
            {
               e.printStackTrace();
            }
         }

         public void backOnEDT()
         {
            if (geoPosition == null) return;
            
            _kit.setCenterPosition(geoPosition);
            addWaypoint(new EOWaypoint(geoPosition, _address));
                     
            // a less than ideal way to address an issue with initial positioning of markers..
            SwingUtilities.invokeLater(new Runnable()
            {
               public void run() { revalidateMarkerOverlay(); }
            });
         }
      });
   }
   
}
