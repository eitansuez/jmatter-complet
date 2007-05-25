/*
 * Created on Sep 5, 2003
 */
package com.u2d.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.JTextComponent;

/**
 * @author Eitan Suez
 */
public class UIUtils
{

	public static void center(Container parent, Component child)
	{
      center(parent, child, false);
	}

   public static void center(Container parent, Component child, boolean relative)
   {
      Point p = computeCenter(parent.getSize(), child.getSize());
      if (relative)
      {
         p.x += parent.getX();
         p.y += parent.getY();
      }
      child.setLocation(p);
   }

   public static Point computeCenter(Container parent, Component child)
   {
      return computeCenter(parent.getSize(), child.getSize());
   }
	
	public static Point computeCenter(Dimension parentSize, Dimension childSize)
	{
		int xpos = (parentSize.width - childSize.width) / 2;
		int ypos = (parentSize.height - childSize.height) / 2;
      // prevent children larger than their parents from being positioned
      // outside parent's bounds
      if (xpos < 0) xpos = 0;
      if (ypos < 0) ypos = 0;
      return new Point(xpos, ypos);
	}

		
	public static Rectangle centerOnScreen()
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension size = new Dimension((int) (screenSize.width * 0.80), (int) (screenSize.height * 0.80));
		Point location = UIUtils.computeCenter(screenSize, size);
		return new Rectangle(location, size);
	}
   public static void centerOnScreen(Container container)
   {
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Point location = UIUtils.computeCenter(screenSize, container.getSize());
      container.setLocation(location);
   }

   public static void focusOnComponent(final JComponent component)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
//            int count = 0;
//            Component parent = component;
//            while (!component.isShowing() && count < 10 && parent!=null)
//            {
//               parent = parent.getParent();
//               if (parent instanceof com.u2d.views.ExpandableView)
//               {
//                  com.u2d.views.ExpandableView view = (com.u2d.views.ExpandableView) parent;
//                  if (!view.isExpanded())
//                  {
//                     view.toggle();
//                  }
//               }
//               count++;
//            }
            
            JViewport viewport = 
               (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, component);
            if (viewport != null)
            {   
               System.out.println("Found viewport");
               Rectangle rect = component.getBounds();
               viewport.scrollRectToVisible(rect);
            }
            component.requestFocusInWindow();
         }
      }
      );
   }

   
   /**
    * translating a doubleclick event into an action event
    *
    * @param actionListener specified action listener
    */
   public static MouseListener doubleClickActionListener(final ActionListener actionListener)
   {
      return (MouseListener) new MouseAdapter()
         {
            public void mouseClicked(MouseEvent evt)
            {
               if (SwingUtilities.isLeftMouseButton(evt) && evt.getClickCount() == 2)
                  actionListener.actionPerformed(new ActionEvent(evt.getSource(), evt.getID(), ""));
            }
         };
   }
   
    public static Color lighten(Color col)
    {
       int red = col.getRed();
       int green = col.getGreen();
       int blue = col.getBlue();
       
       float[] hsb = Color.RGBtoHSB(red, green, blue, null);
       return Color.getHSBColor(hsb[0], 0.3f, hsb[2]);
    }
    
    
    public static void selectOnFocus(final JTextComponent c)
    {
       c.addFocusListener(new FocusListener()
             {
                public void focusGained(FocusEvent evt)
                {
                   c.selectAll();
                }
                public void focusLost(FocusEvent evt) {}
             });
    }


   public static boolean focusFirstEditableField(Container container)
   {
      for (int i=0; i<container.getComponentCount(); i++)
      {
         Component c = container.getComponent(i);
         if (c instanceof JTextComponent)
         {
            JTextComponent textC = (JTextComponent) c;
//            System.out.println("Requesting focus for: "+textC.getClass().getName());
            textC.requestFocusInWindow();
            textC.selectAll();
            return true;
         }
         else if (c instanceof JTabbedPane)
         {
            JTabbedPane tp = (JTabbedPane) c;
            Container tabContents = (Container) tp.getSelectedComponent();
            return focusFirstEditableField(tabContents);
         }
         else if (c instanceof Container)
         {
            boolean done = focusFirstEditableField((Container) c);
            if (done) return done;
         }
      }
      return false;
   }

}
