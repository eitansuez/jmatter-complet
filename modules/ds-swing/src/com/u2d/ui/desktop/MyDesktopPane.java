package com.u2d.ui.desktop;

import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameAdapter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.beans.PropertyVetoException;

/**
 * Date: Jun 7, 2005
 * Time: 10:19:57 AM
 *
 * @author Eitan Suez
 */
public class MyDesktopPane extends JDesktopPane
{
   private LinkedList<JInternalFrame> jifs = new LinkedList<JInternalFrame>();

   public MyDesktopPane()
   {
      super();
      setDesktopManager(new BetterDesktopManager());
      
      getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
            put(KeyStroke.getKeyStroke("alt BACK_QUOTE"), "switch-next-frame");
      getActionMap().put("switch-next-frame", new AbstractAction()
         {
            public void actionPerformed(ActionEvent e)
            {
               rotateLeft();
            }
         });
      getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
            put(KeyStroke.getKeyStroke("shift alt BACK_QUOTE"), "switch-previous-frame");
      getActionMap().put("switch-previous-frame", new AbstractAction()
         {
            public void actionPerformed(ActionEvent e)
            {
               rotateRight();
            }
         });
   }

   public void addJIF(JInternalFrame jif)
   {
      jif.setVisible(true);
      add(jif);
      try {
         jif.setSelected(true);
      } catch (java.beans.PropertyVetoException ex) {}
      jif.addInternalFrameListener(new MyInternalFrameListener());
      
      // 1. add new jif to jifs list
      jifs.addFirst(jif);
      updatePositions();
   }
   private void rotateLeft()
   {
      jifs.addLast(jifs.removeFirst());
      while (!((JInternalFrame)jifs.getFirst()).isVisible())
         jifs.addLast(jifs.removeFirst());
      updatePositions();
      try {
         ((JInternalFrame) jifs.getFirst()).setSelected(true);
      } catch (PropertyVetoException e2) {}
   }
   private void rotateRight()
   {
      jifs.addFirst(jifs.removeLast());
      while (!((JInternalFrame)jifs.getFirst()).isVisible())
         jifs.addFirst(jifs.removeLast());
      updatePositions();
      try {
         ((JInternalFrame) jifs.getFirst()).setSelected(true);
      } catch (PropertyVetoException e2) {}
   }

   private void updatePositions()
   {
      JInternalFrame jif;
      for (int i=0; i<jifs.size(); i++)
      {
         jif = (JInternalFrame) jifs.get(i);
         setPosition(jif, i);
      }
   }

   class MyInternalFrameListener extends InternalFrameAdapter
   {
      public void internalFrameActivated(InternalFrameEvent e)
      {
         JInternalFrame f = e.getInternalFrame();
         jifs.remove(f);
         jifs.addFirst(f);
         updatePositions();
      }
      public void internalFrameDeiconified(InternalFrameEvent e)
      {
         JInternalFrame f = e.getInternalFrame();
         jifs.addFirst(f);
         updatePositions();
      }
   }


   // desktopmanager overridden in order to override activatenextframe
   // which cannot be overriden so i'm overriding the methods that call
   // activatenextframe
   class BetterDesktopManager extends DefaultDesktopManager
   {
      public void closeFrame(JInternalFrame f)
      {
         boolean findNext = f.isSelected();
         Container c = f.getParent();
         if (findNext)
         {
            try {
               f.setSelected(false);
            } catch (PropertyVetoException e2) {}
         }
         if (c != null)
         {
            c.remove(f);
            c.repaint(f.getX(), f.getY(), f.getWidth(), f.getHeight());
         }
         removeIconFor(f);
         if (f.getNormalBounds() != null)
            f.setNormalBounds(null);
         if (wasIcon(f))
            setWasIcon(f, null);

         jifs.remove(f);
         updatePositions();
         if (findNext) activateNextFrame();
      }

      private void activateNextFrame()
      {
         // delay activation slightly;  usually helps when requesting focus prematurely
         new Thread()
         {
            public void run()
            {
               SwingUtilities.invokeLater(new Runnable()
               {
                  public void run()
                  {
                     if (noNextFrame())
                     {
                        MyDesktopPane.this.requestFocusInWindow();
                        return;
                     }
                     JInternalFrame f = (JInternalFrame) jifs.getFirst();
                     while (!f.isVisible())
                     {
                        rotateLeft();
                        f = (JInternalFrame) jifs.getFirst();
                     }
                     try {
                        f.setSelected(true);
                     } catch (PropertyVetoException e2) {}
                  }
               });
            }
         }.start();
      }
      
      private boolean noNextFrame()
      {
         if (jifs.size() == 0) return true;
         
         for (int i=0; i<jifs.size(); i++)
         {
            if (((JInternalFrame) jifs.get(i)).isVisible())
               return false;
         }
         return true;
      }

      public void iconifyFrame(JInternalFrame f)
      {
         JInternalFrame.JDesktopIcon desktopIcon;
         Container c;
         boolean findNext = f.isSelected();

         desktopIcon = f.getDesktopIcon();
         if (!wasIcon(f))
         {
            Rectangle r = getBoundsForIconOf(f);
            desktopIcon.setBounds(r.x, r.y, r.width, r.height);
            setWasIcon(f, Boolean.TRUE);
         }

         c = f.getParent();

         if (c == null) return;

         if (c instanceof JLayeredPane)
         {
            JLayeredPane lp = (JLayeredPane) c;
            int layer = lp.getLayer((Component) f);
            JLayeredPane.putLayer(desktopIcon, layer);
         }
         if (f.isMaximum())
         {
            try
            {
               f.setMaximum(false);
            }
            catch (PropertyVetoException e2)
            {
            }
         }
         c.remove(f);
         c.add(desktopIcon);
         c.repaint(f.getX(), f.getY(), f.getWidth(), f.getHeight());
         try
         {
            f.setSelected(false);
         }
         catch (PropertyVetoException e2)
         {
         }

         jifs.remove(f);
         updatePositions();

         if (findNext) { activateNextFrame(); }
      }
   }

}
