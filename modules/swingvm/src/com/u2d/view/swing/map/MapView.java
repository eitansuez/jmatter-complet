package com.u2d.view.swing.map;

import org.jdesktop.swingx.JXMapKit;
import org.jdesktop.swingx.JXPanel;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 20, 2007
 * Time: 3:49:40 PM
 */
public class MapView extends JLayeredPane
{
   protected JXMapKit _kit;
   protected JXPanel _waypointOverlay;
   protected JXPanel _bubbleOverlay;
   
   public MapView()
   {
      this(new JXMapKit());
   }
   
   public MapView(JXMapKit kit)
   {
      OverlayLayout overlay = new OverlayLayout(this);
      setLayout(overlay);
      
      _kit = kit;
      addLayer(_kit, JLayeredPane.DEFAULT_LAYER.intValue());
      
      _waypointOverlay = new GeoPanel();
      addLayer(_waypointOverlay, 50);
      
      _bubbleOverlay = new JXPanel();
      _bubbleOverlay.setOpaque(false);
      _bubbleOverlay.setLayout(null);
      addLayer(_bubbleOverlay, 55);
      
      _kit.getMainMap().addPropertyChangeListener("zoom", new PropertyChangeListener()
      {
         public void propertyChange(PropertyChangeEvent evt)
         {
            revalidateWaypointOverlay();
         }
      });
      _kit.getMainMap().addPropertyChangeListener("centerPosition", new PropertyChangeListener()
      {
         public void propertyChange(PropertyChangeEvent evt)
         {
            revalidateWaypointOverlay();
         }
      });
      
   }

   protected void revalidateWaypointOverlay()
   {
      _waypointOverlay.revalidate();  // gives layout manager a change to properly position waypoints
   }

   class GeoPanel extends JXPanel
   {
      public GeoPanel()
      {
         setLayout(new MapLayout(_kit));
         setOpaque(false);
      }
   }
   
   private void addLayer(JComponent panel, int layer)
   {
      add(panel);
      setLayer(panel, layer);
   }
   
   public void addWaypoint(JComponent component, Mappable constraints)
   {
      _waypointOverlay.add(component, constraints);
   }
   
   // convenience:  providing a default "renderer" for waypoint..
   public void addWaypoint(EOWaypoint waypoint)
   {
      JLabel marker = new WaypointMarker(waypoint);
      addWaypoint(marker, waypoint);
   }
   
   
   
   class WaypointMarker extends JLabel implements Pointy
   {
      private MappableView _view;
   
      public WaypointMarker(EOWaypoint waypoint)
      {
         super(new ImageIcon(MapView.markerImg));
         _view = new MappableView(waypoint.getEObject());
         _view.setSize(_view.getPreferredSize());
         
         this.addComponentListener(new ComponentAdapter()
         {
            public void componentMoved(ComponentEvent e)
            {
               Point viewLocation = e.getComponent().getLocation();
               // adjust for height of _view and add a slight vertical margin:
               viewLocation.translate(0, -(_view.getHeight()+5));
               _view.setLocation(viewLocation);
            }
         });
               
         _bubbleOverlay.add(_view);
      
         addMouseListener(new MouseAdapter()
         {
            public void mouseClicked(MouseEvent e)
            {
               _view.setVisible(true);
            }
         });
      }
   
      public Dimension endPosition()
      {
         return new Dimension(getWidth()/2, getHeight());
      }
   
   }
   
   static BufferedImage markerImg;
   static
   {
      try
      {
         ClassLoader loader = MapView.class.getClassLoader();
         URL url = loader.getResource("org/jdesktop/swingx/mapviewer/resources/standard_waypoint.png");
         markerImg = ImageIO.read(url);
      }
      catch (IOException ex)
      {
         ex.printStackTrace();
      }
   }
}
