package com.u2d.view.swing.map;

import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.JXMapKit;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Saved code to determine correspondence between zoom level and viewport's geodistance
 * 
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Feb 1, 2008
 * Time: 9:03:28 PM
 */
public class MapZoomTest
{
   public static void main(String[] args)
   {
      final JXMapKit kit = null;
      kit.getZoomSlider().addChangeListener(new ChangeListener()
      {
         public void stateChanged(ChangeEvent e)
         {
            int zoom = kit.getMainMap().getZoom();
            Rectangle bounds = kit.getMainMap().getViewportBounds();
            Point2D pixelcoord1 = new Point(bounds.x, bounds.y);
            Point2D pixelcoord2 = new Point(bounds.x + bounds.width, bounds.y);

            GeoPosition left = kit.getMainMap().getTileFactory().pixelToGeo(pixelcoord1, zoom);
            GeoPosition right = kit.getMainMap().getTileFactory().pixelToGeo(pixelcoord2, zoom);
            double distance = right.getLongitude() - left.getLongitude();
            System.out.println("for zoom level " + zoom + ", distance is: "+Math.abs(distance));
         }
      });
      
      
   }
}
