/*
 * Created on Oct 16, 2003
 */
package com.u2d.ui.desktop;

import java.awt.*;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetDragEvent;
import javax.swing.*;
import java.beans.*;
import java.util.TooManyListenersException;

/**
 * The purpose of this guy is to graft some keyboard shortcuts on the JInternalFrame
 * so I can close it without using the mouse.
 * 
 * @author Eitan Suez
 */
public class CloseableJInternalFrame extends JInternalFrame
{
   public CloseableJInternalFrame()
   {
      super();
      init();
   }

   public CloseableJInternalFrame(String title, boolean resizable, boolean closeable, boolean maximizable, boolean iconifiable)
   {
      super(title, resizable, closeable, maximizable, iconifiable);
      init();
   }

   private void init()
   {
      setupToFocusOnDragEnter();
   }

   public void close()    { closeFrame(this); }

   public void removeNotify()
   {
      super.removeNotify();
   }

   public static void closeFrame(JInternalFrame jif)
   {
      JDesktopPane desktopPane = jif.getDesktopPane();
      if (desktopPane == null)
      {
         // System.err.println("CloseableJInternalFrame::closeFrame: jinternalframe's desktop pane is not set");
         return;
      }
   
      desktopPane.getDesktopManager().closeFrame(jif);
      // even though dpmgr.closeFrame closes and hides the frame,
      // the isClosed and isVisible properties are not set! (macosx)
      try { jif.setClosed(true); } catch (PropertyVetoException ex) { System.err.println("Vetoed"); }
      jif.setVisible(false);
   }

   public static void close(Component comp)
   {
      JInternalFrame jif = (JInternalFrame) SwingUtilities.getAncestorOfClass(JInternalFrame.class, comp);
      if (jif == null) return;
      if (jif instanceof CloseableJInternalFrame)
      {
         ((CloseableJInternalFrame) jif).close();
      }
      else
      {
         closeFrame(jif);
      }
   }

   public static void updateSize(Component comp)
   {
      CloseableJInternalFrame jif = (CloseableJInternalFrame)
               SwingUtilities.getAncestorOfClass(CloseableJInternalFrame.class,  comp);
      if (jif == null) return;

      Point point = null;
      JViewport viewport = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, comp);
      if (viewport != null)
      {
         point = viewport.getViewPosition();
      }

      jif.updateSize();

      // preserve view position after pack
      if (viewport != null && point != null)
      {
         viewport.setViewPosition(point);
      }
   }

   public void updateSize()
   {
      Dimension dim = new Dimension();
      dim.width = Math.max(getSize().width, getPreferredSize().width);
      dim.height = Math.max(getSize().height, getPreferredSize().height);
      setSize(dim);  validate();
//      pack();
      adjustSize(this, getDesktopPane());
   }

   // if necessary, trim height of window so it does not exceed that of the desktoppane that the
   // window is in
   private static void adjustSize(JInternalFrame jif, JDesktopPane desktopPane)
   {
      if (desktopPane == null) return;
      int desktopHeight = desktopPane.getHeight();
      int desktopWidth = desktopPane.getWidth();
      Dimension dim = jif.getSize();
      if (jif.getHeight() > desktopHeight)
         dim.height = desktopHeight - 10;
      if (jif.getWidth() > desktopWidth)
         dim.width = desktopWidth - 10;
      jif.setSize(dim);
   }

   protected void setupToFocusOnDragEnter()
   {
      DropTarget dropTarget = new DropTarget();
      try
      {
         dropTarget.addDropTargetListener(
               new DropTargetAdapter()
                   {
                      public void dragEnter(DropTargetDragEvent evt)
                      {
                         try {
                            CloseableJInternalFrame.this.setSelected(true);
                         } catch (PropertyVetoException ex) { }
                      }
                      public void drop(DropTargetDropEvent dtde)
                      {
                         dtde.rejectDrop();
                      }
                   }
                );
         setDropTarget(dropTarget);
      }
      catch (TooManyListenersException ex)
      {
        System.err.println("TooManyListenersException: "+ex.getMessage());
        ex.printStackTrace();
      }
   }
   
   public void serialize(XMLEncoder enc)
   {
      enc.writeObject(getBounds());
   }
   public void deserialize(XMLDecoder dec)
   {
      Rectangle bounds = (Rectangle) dec.readObject();
      setBounds(bounds);
   }
   
}
