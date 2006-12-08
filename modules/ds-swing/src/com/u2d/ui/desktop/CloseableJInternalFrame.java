/*
 * Created on Oct 16, 2003
 */
package com.u2d.ui.desktop;

import com.u2d.ui.Platform;
import java.awt.*;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.event.*;
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
      if (closeable)
         init();
   }

   static String CLOSEWINDOW_MAP_KEY = "CLOSE_WINDOW";
   static KeyStroke COMMAND_W = KeyStroke.getKeyStroke(KeyEvent.VK_W, Platform.mask());

   private void init()
   {
      getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(COMMAND_W, CLOSEWINDOW_MAP_KEY);
      getActionMap().put(CLOSEWINDOW_MAP_KEY, new AbstractAction()
      {
         public void actionPerformed(ActionEvent evt)
         {
            close();
         }
      });
   }

   public void close()    { closeFrame(this); }

   public void removeNotify()
   {
      super.removeNotify();
      getActionMap().remove(CLOSEWINDOW_MAP_KEY);
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
      closeFrame(jif);
   }

   public static void updateSize(Component comp)
   {
      JInternalFrame jif = (JInternalFrame) SwingUtilities.getAncestorOfClass(JInternalFrame.class, comp);
      if (jif == null) return;
      updateSize(jif);

      JDesktopPane desktopPane = jif.getDesktopPane();
      if (desktopPane == null)
      {
         // System.err.println("CloseableJInternalFrame::updateSize: desktop pane for jinternal frame is not set");
         return;
      }

      Point point = null;
      JViewport viewport = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, comp);
      if (viewport != null)
      {
         point = viewport.getViewPosition();
         //System.out.println("Saved view position: "+point);
      }
      jif.pack();  // pack appears to have side effect of obliterating viewport position

      adjustSize(jif, desktopPane);

      // preserve view position after pack
      if (viewport != null && point != null)
      {
         //System.out.println("Restoring view position: "+point);
         viewport.setViewPosition(point);
      }
   }

   // if necessary, trim height of window so it does not exceed that of the desktoppane that the
   // window is in
   private static void adjustSize(JInternalFrame jif, JDesktopPane desktopPane)
   {
      int desktopHeight = desktopPane.getHeight();
      int desktopWidth = desktopPane.getWidth();
      Dimension dim = jif.getSize();
      if (jif.getHeight() > desktopHeight)
         dim.height = desktopHeight - 10;
      if (jif.getWidth() > desktopWidth)
         dim.width = desktopWidth - 10;
      jif.setSize(dim);
   }

   public void updateSize()
   {
      pack();
      if (getDesktopPane() == null) return;
      adjustSize(this, getDesktopPane());
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
   
}
