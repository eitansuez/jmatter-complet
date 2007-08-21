package com.u2d.view.swing.map;

import java.awt.*;

/**
 * Basically an interface for widgets whose "end point" is not their upper-left corner
 * So that when positioning them, say, on a map, the correct end point matches where it
 * should appear on the map.  Layout Manager will translate its position by the Dimension
 * returned by the method endPosition()
 */
public interface Pointy
{
   public Dimension endPosition();
}
