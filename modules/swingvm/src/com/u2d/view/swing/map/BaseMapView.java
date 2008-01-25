package com.u2d.view.swing.map;

import com.u2d.model.EObject;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;
import org.jdesktop.swingx.mapviewer.DefaultTileFactory;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jan 25, 2008
 * Time: 9:45:52 PM
 */
public abstract class BaseMapView extends MapView
{
   protected void init(EObject eo)
   {
      putClientProperty("title", eo.title());
      
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
      
      setupWaypoints();
      
      // a less than ideal way to address an issue with initial positioning of markers..
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run() { revalidateMarkerOverlay(); }
      });
   }
   
   public abstract void setupWaypoints();
}
