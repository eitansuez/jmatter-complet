package com.u2d.view.swing.map;

import org.jdesktop.swingx.JXMapKit;
import org.jdesktop.swingx.JXMapViewer;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 20, 2007
 * Time: 3:43:25 PM
 */
public class MapLayout implements LayoutManager2
{
   private JXMapKit _kit;
   
   private java.util.List _mappables = new ArrayList();
   private java.util.List _components = new ArrayList();

   public MapLayout(JXMapKit kit)
   {
      _kit = kit;
   }
   
   public void layoutContainer(Container parent)
   {
      synchronized (parent.getTreeLock())
      {
         _kit.setBounds(parent.getBounds());

         JXMapViewer map = _kit.getMainMap();
         
         // The following, taken from the implementation of WaypointPainter in swingx-ws..
         int zoom = map.getZoom();
         Dimension sizeInTiles = map.getTileFactory().getMapSize(zoom);
         int tileSize = map.getTileFactory().getTileSize(zoom);
         Dimension sizeInPixels = new Dimension(sizeInTiles.width*tileSize, sizeInTiles.height*tileSize);

         Rectangle viewportBounds = map.getViewportBounds();
         double vpx = viewportBounds.getX();
         // normalize the left edge of the viewport to be positive
         while(vpx < 0) {
             vpx += sizeInPixels.getWidth();
         }
         // normalize the left edge of the viewport to no wrap around the world
         vpx %= sizeInPixels.getWidth();
        
         // create two new viewports next to each other
         Rectangle2D vp2 = 
               new Rectangle2D.Double(vpx, viewportBounds.getY(),
                                      viewportBounds.getWidth(), viewportBounds.getHeight());
         
         Rectangle2D vp3 = 
               new Rectangle2D.Double(vpx-sizeInPixels.getWidth(), viewportBounds.getY(),
                                      viewportBounds.getWidth(), viewportBounds.getHeight());
         
         for (int i=0; i<_components.size(); i++)
         {
            Component comp = (Component) _components.get(i);
            Mappable mappable = (Mappable) _mappables.get(i);
            
            Point2D point = map.getTileFactory().geoToPixel(mappable.getPosition(), map.getZoom());
            if (vp2.contains(point))
            {
               positionWithRespectToViewport(vp2, comp, point);
            }
            else if (vp3.contains(point))
            {
               positionWithRespectToViewport(vp3, comp, point);
            }
            else
            {  // point is outside viewport.
               if (comp.isVisible())
                  comp.setVisible(false);
            }

         }
         
      }
   }

   private void positionWithRespectToViewport(Rectangle2D viewport, Component comp, Point2D point)
   {
      if (!comp.isVisible())
         comp.setVisible(true);
      int x = (int) (point.getX() - viewport.getX());
      int y = (int) (point.getY() - viewport.getY());
      Point location = new Point(x, y);
      if (comp instanceof Pointy)
      {
         Dimension delta = ((Pointy) comp).endPosition();
         location.translate(-delta.width, -delta.height);
      }
      comp.setBounds(new Rectangle(location, comp.getPreferredSize()));
   }


   public void addLayoutComponent(Component comp, Object constraints)
   {
      synchronized (comp.getTreeLock())
      {
         if (!(constraints instanceof Mappable))
         {
            throw new IllegalArgumentException("cannot add to layout:  contstraint must be a Mappable");
         }
         Mappable mappable = (Mappable) constraints;
         _mappables.add(mappable);
         _components.add(comp);
      }
   }

   public void removeLayoutComponent(Component comp)
   {
      synchronized (comp.getTreeLock())
      {
         for (int i=0; i<_components.size(); i++)
         {
            if (_components.get(i) == comp)
            {
               _components.remove(i);
               _mappables.remove(i);
            }
         }
      }
   }

   public Dimension minimumLayoutSize(Container parent)
   {
      synchronized (parent.getTreeLock())
      {
         return _kit.getMinimumSize();
      }
   }

   public Dimension preferredLayoutSize(Container parent)
   {
      synchronized (parent.getTreeLock())
      {
         return _kit.getPreferredSize();
      }
   }

   public Dimension maximumLayoutSize(Container target)
   {
      synchronized (target.getTreeLock())
      {
         return _kit.getMaximumSize();
      }
   }

   public void addLayoutComponent(String name, Component comp)
   {
      throw new IllegalArgumentException("use addLayoutComponent(comp, constraints) instead");
   }

   public float getLayoutAlignmentX(Container target) { return 0.5f; }
   public float getLayoutAlignmentY(Container target) { return 0.5f; }

   public void invalidateLayout(Container target)
   {  // noop
   }

}