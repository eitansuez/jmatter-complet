/*
 * Created on Dec 14, 2003
 */
package com.u2d.ui.desktop;

import com.u2d.ui.ContextMenu;
import com.u2d.ui.UIUtils;
import com.u2d.ui.Platform;
import com.u2d.ui.StripedRowListCellRenderer;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import fr.jayasoft.jexplose.Explosable;
import fr.jayasoft.jexplose.JExploseUtils;

/**
 * @author Eitan Suez
 */
public class EnhDesktopPane extends MyDesktopPane
{
   private Explosable explosable;

   private final int MARGIN = 10;
   private final int TILEDISTANCE = 20;
   private Point _nextChildPos = new Point(MARGIN, MARGIN);
   private Action _closeAllAction;
   private ContextMenu _contextMenu;
   private MouseTracker _mouseTracker;
   private MsgPnl _msgPnl;
   private MsgView _msgView;

   public EnhDesktopPane()
   {
      super();
      explosable = new Explosable(this);

      _closeAllAction = new CloseAllAction();

      JMenuItem[] menuItems = new JMenuItem[5];
      menuItems[0] = new JMenuItem(_closeAllAction);
      menuItems[1] = new JMenuItem(new MinimizeAllAction(true));
      menuItems[2] = new JMenuItem(new MinimizeAllAction(false));
      menuItems[3] = new JMenuItem(JExploseUtils.getLightningAction(explosable));
      menuItems[4] = new JMenuItem(new AbstractAction("Messages")
      {
         public void actionPerformed(ActionEvent e)
         {
            _msgView.setVisible(true);
         }
      });
      _contextMenu = new ContextMenu(menuItems, this);

      _mouseTracker = new MouseTracker();
      Toolkit.getDefaultToolkit().addAWTEventListener(_mouseTracker, AWTEvent.MOUSE_EVENT_MASK);
      
      _msgPnl = new MsgPnl(1000);
      add(_msgPnl, JLayeredPane.POPUP_LAYER);

      JExploseUtils.installLightningHotKey(explosable, KeyEvent.VK_F12);

      _msgView = new MsgView();
   }

   class BasicListModel extends AbstractListModel
   {
      private java.util.List<String> _items;
      BasicListModel(java.util.List<String> items) { _items = items; }
      public int getSize() { return _items.size(); }
      public Object getElementAt(int index) { return _items.get(index); }
      public void fireAdd(Object source)
      {
         fireIntervalAdded(source, _items.size()-1, _items.size());
      }
   }
   class MsgView extends CloseableJInternalFrame
   {
      private transient java.util.List<String> messages;
      private transient BasicListModel listModel;
      private transient JList msgView;

      MsgView()
      {
         super("Messages", true, true, false, false);
         setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);

         messages = new ArrayList<String>();
         listModel = new BasicListModel(messages);
         msgView = new JList(listModel);
         msgView.setCellRenderer(new StripedRowListCellRenderer());
         msgView.setPreferredSize(new Dimension(500,400));

         getContentPane().add(new JScrollPane(msgView));
         addFrame(this);
         setVisible(false);
      }
      void addMessage(String msg)
      {
         messages.add(msg);
         listModel.fireAdd(msgView);
      }
   }

   public void message(String msg)
   {
      _msgPnl.message(msg, this);
      _msgView.addMessage(msg);
   }


   class MouseTracker implements AWTEventListener
   {
      Point lastMouseClickLocation = new Point(0, 0);
      MouseEvent mouseEvent;

      public void eventDispatched(AWTEvent event)
      {
         if (event.getID() == MouseEvent.MOUSE_PRESSED)
         {
            mouseEvent = (MouseEvent) event;
            lastMouseClickLocation =
               SwingUtilities.convertPoint(
                     (Component) event.getSource(),
                     mouseEvent.getPoint(),
                     EnhDesktopPane.this );

            JInternalFrame[] frames = getAllFramesInLayer(JLayeredPane.DEFAULT_LAYER);
            JInternalFrame targetFrame = null;
            for (int i=0; i<frames.length; i++)
            {
               Point pt = SwingUtilities.convertPoint((Component) event.getSource(), mouseEvent.getPoint(), frames[i]);
               boolean candidate = frames[i].contains(pt);
               if (candidate)
               {
                  if ((targetFrame == null) || higherzorder(frames[i], targetFrame))
                  {
                     targetFrame = frames[i];
                  }
               }
            }
            if (targetFrame == null) return;
            if (!mouseEvent.isShiftDown()) return;

            mouseEvent.consume();
            enterFrameMoveMode(targetFrame, SwingUtilities.convertPoint((Component) event.getSource(),
                                                                         mouseEvent.getPoint(), targetFrame));
         }
      }
   }

   private boolean higherzorder(JInternalFrame frame, JInternalFrame frame2)
   {
      JLayeredPane lp = getLayeredPaneAbove(frame);
      return lp.getPosition(frame) < lp.getPosition(frame2);
   }

   private void enterFrameMoveMode(final JInternalFrame iframe, final Point startPt)
   {
      setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
      final AWTEventListener mouseMotionListener = new AWTEventListener()
      {
         public void eventDispatched(AWTEvent event)
         {
            MouseEvent mouseEvt = (MouseEvent) event;
            Point pt = SwingUtilities.convertPoint((Component) event.getSource(), mouseEvt.getPoint(), EnhDesktopPane.this);
            iframe.setLocation(pt.x - startPt.x,
                               pt.y - startPt.y);
         }
      };
      Toolkit.getDefaultToolkit().addAWTEventListener(mouseMotionListener, AWTEvent.MOUSE_MOTION_EVENT_MASK);

      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener()
            {
               public void eventDispatched(AWTEvent event)
               {
                  if (event.getID() == MouseEvent.MOUSE_RELEASED)
                  {
                     Toolkit.getDefaultToolkit().removeAWTEventListener(mouseMotionListener);
                     Toolkit.getDefaultToolkit().removeAWTEventListener(this);
                     setCursor(Cursor.getDefaultCursor());
                  }
               }
            }, AWTEvent.MOUSE_EVENT_MASK);
         }
      });

   }


   public void removeNotify()
   {
      super.removeNotify();
      Toolkit.getDefaultToolkit().removeAWTEventListener(_mouseTracker);
   }

   public void addFrame(JInternalFrame frame)
   {
      addFrame(frame, Positioning.NEARMOUSE);
   }
   public void addFrame(JInternalFrame frame, Positioning positioning)
   {
      if (_mouseTracker != null &&
          _mouseTracker.mouseEvent != null &&
          ( ( Platform.APPLE && _mouseTracker.mouseEvent.isMetaDown() ) ||
            ( !Platform.APPLE && _mouseTracker.mouseEvent.isControlDown() ) ))
      {
         placeHovering(frame, _mouseTracker.lastMouseClickLocation);
      }
      else
      {
         positionFrame(frame, positioning);
         addJIF(frame);
      }
   }

   public void positionFrame(JInternalFrame frame, Positioning positioning)
   {
      if (positioning == Positioning.NONE)
      {
         return;
      }
      if (positioning == Positioning.NEARMOUSE)
      {
         positionNearMouse(frame);
      }
      else if (positioning == Positioning.CENTERED)
      {
         UIUtils.center(this, frame);
      }
      else
      {
         frame.setLocation(nextFramePos(frame, positioning));
      }
   }

   private void positionNearMouse(JInternalFrame frame)
   {
      Point location = _mouseTracker.lastMouseClickLocation;
      // center frame at location
      location.x -= frame.getWidth() / 2;
      location.y -= frame.getHeight() / 2;
      // adjust for title bar out of bounds possibility
      location.y = Math.max(location.y, 0);
      location.x = Math.max(location.x, 0);
      frame.setLocation(location);
   }


   private synchronized Point nextFramePos(JComponent component, Positioning rule)
   {
      Point currentPos = _nextChildPos;
      if (rule == Positioning.TOTHERIGHT)
      {
         currentPos = overflowRight(component, currentPos);
         _nextChildPos = new Point(currentPos);
         _nextChildPos.x += component.getWidth() + MARGIN;
      }
      else if (rule == Positioning.BELOW)
      {
         currentPos = overflowBottom(component, currentPos);
         _nextChildPos = new Point(currentPos);
         _nextChildPos.y += component.getHeight() + MARGIN;
      }
      else if (rule == Positioning.TILE)
      {
         currentPos = overflowRight(component, currentPos);
         currentPos = overflowBottom(component, currentPos);
         _nextChildPos = new Point(currentPos);
         _nextChildPos.x = currentPos.x + TILEDISTANCE;
         _nextChildPos.y = currentPos.y + TILEDISTANCE;
      }
      else
      {
         throw new IllegalArgumentException("invalid rule "+rule);
      }
      //System.out.println("next child pos is: "+_nextChildPos);
      return currentPos;
   }

   private Point overflowRight(JComponent component, Point p)
   {
      if ( (component.getWidth() / getWidth() > 0.7) ||
           (component.getHeight() / getHeight() > 0.7) )
         return new Point(MARGIN, MARGIN);

      if (p.x + component.getWidth() > getWidth())
      {
         // p.x = ( p.x + component.getWidth() ) % getWidth();
         p.x = MARGIN;
         p.y += 100;
         p = overflowBottom(component, p);  // this is dangerous
      }
      return p;
   }
   private Point overflowBottom(JComponent component, Point p)
   {
      if ( (component.getWidth() / getWidth() > 0.7) ||
            (component.getHeight() / getHeight() > 0.7) )
          return new Point(MARGIN, MARGIN);

      if (p.y + component.getHeight() > getHeight())
      {
         // p.y = ( p.y + component.getHeight() ) % getHeight();
         p.y = MARGIN;
         p.x += 100;
         p = overflowRight(component, p);  // this is dangerous
      }
      return p;
   }
   
   
   public void popup(JPopupMenu menu)
   {
      menu.show(this, _mouseTracker.lastMouseClickLocation.x,  
                _mouseTracker.lastMouseClickLocation.y);
   }


   public void setCursor(Cursor cursor)
   {
      super.setCursor(cursor);
      JInternalFrame[] frames = getAllFrames();
      for (int i=0; i<frames.length; i++)
      {
         frames[i].setCursor(cursor);
      }
   }

   public void closeAllChildren()
   {
      _closeAllAction.actionPerformed(null);
   }


   /* *** desktop pane actions (close all, minimize all/restore all) *** */

   class CloseAllAction extends AbstractAction
   {
      public CloseAllAction()
      {
         super("Close All");
      }
      public void actionPerformed(ActionEvent evt)
      {
         JInternalFrame[] frames = getAllFrames();
         for (int i=0; i<frames.length; i++)
         {
            if (!frames[i].isVisible() && !frames[i].isIcon()) continue;

            remove(frames[i]);
            frames[i].dispose();
         }
         revalidate();
         repaint();
      }
   }

   class MinimizeAllAction extends AbstractAction
   {
      boolean _iconify = true;
      public MinimizeAllAction(boolean iconify)
      {
         super();
         _iconify = iconify;
         if (_iconify)
            putValue(Action.NAME, "Minimize All");
         else
            putValue(Action.NAME, "Restore All");
      }
      public void actionPerformed(ActionEvent evt)
      {
         JInternalFrame[] frames = getAllFrames();
         for (int i=0; i<frames.length; i++)
         {
            if (!frames[i].isVisible()) continue;

            if (_iconify && !frames[i].isIcon())
            {
               try {
                  frames[i].setIcon(true);
               } catch (PropertyVetoException e) {
                  e.printStackTrace();
               }
            }
            else if (!_iconify && frames[i].isIcon())
            {
               try {
                  frames[i].setIcon(false);
               } catch (PropertyVetoException e) {
                  e.printStackTrace();
               }
            }
         }
      }
   }

   public ContextMenu getContextMenu() { return _contextMenu; }
   public void setEnabled(boolean enabled)
   {
      super.setEnabled(enabled);
      _contextMenu.setEnabled(enabled);
   }


   private void placeHovering(final JInternalFrame iframe,
                              Point eventLocation)
   {
      final AWTEventListener mouseMotionListener = new AWTEventListener()
      {
         public void eventDispatched(AWTEvent event)
         {
            MouseEvent mouseEvt = (MouseEvent) event;
            Point pt = mouseEvt.getPoint();
            pt = SwingUtilities.convertPoint((Component) event.getSource(), pt, EnhDesktopPane.this);
            pt = offsetPoint(pt);
            iframe.setLocation(pt);
         }
      };
      Toolkit.getDefaultToolkit().addAWTEventListener(mouseMotionListener, AWTEvent.MOUSE_MOTION_EVENT_MASK);

      iframe.setLocation(offsetPoint(eventLocation));
      addJIF(iframe);

      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener()
            {
               public void eventDispatched(AWTEvent event)
               {
                  if (event.getID() == MouseEvent.MOUSE_CLICKED)
                  {
                     Toolkit.getDefaultToolkit().removeAWTEventListener(mouseMotionListener);
                     Toolkit.getDefaultToolkit().removeAWTEventListener(this);
                  }
               }
            }, AWTEvent.MOUSE_EVENT_MASK);
         }
      });

   }

   private static Dimension offsetHandle = new Dimension(120, 15);
   private Point offsetPoint(Point point)
   {
      return new Point(point.x - offsetHandle.width,
                       point.y - offsetHandle.height);
   }


   public void paint(Graphics g)
   {
      explosable.preparePaint(g);
      super.paint(g);
      explosable.finishPaint(g);
   }

   protected void paintChildren(Graphics g)
   {
      explosable.preparePaintChildren(g);
      super.paintChildren(g);
   }
   
}
