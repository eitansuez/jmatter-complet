/*
 * Created on Jan 27, 2004
 */
package com.u2d.view.swing;

import com.u2d.model.ComplexEObject;
import java.awt.*;

/**
 * An IconView suitable as a listcellrenderer or treecellrenderer
 * 
 * @author Eitan Suez
 */
public class IconViewAdapter extends IconView
{
   public IconViewAdapter()
   {
      super();
   }
   
   public void validate() {}
   public void revalidate() {}
   public void repaint(long tm, int x, int y, int width, int height) {}
   public void repaint(Rectangle r) {}
   
   protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) { }
   public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {}
   public void firePropertyChange(String propertyName, char oldValue, char newValue) {}
   public void firePropertyChange(String propertyName, short oldValue, short newValue) {}
   public void firePropertyChange(String propertyName, int oldValue, int newValue) {}
   public void firePropertyChange(String propertyName, long oldValue, long newValue) {}
   public void firePropertyChange(String propertyName, float oldValue, float newValue) {}
   public void firePropertyChange(String propertyName, double oldValue, double newValue) {}
   public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}
   
}
