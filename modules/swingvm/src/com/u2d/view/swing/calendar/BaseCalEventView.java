package com.u2d.view.swing.calendar;

import org.jdesktop.swingx.JXPanel;
import com.u2d.view.ComplexEView;
import com.u2d.view.swing.list.CommandsContextMenuView;
import com.u2d.view.swing.dnd.EOTransferHandler;
import com.u2d.calendar.CalEvent;
import com.u2d.ui.FancyLabel;
import com.u2d.model.EObject;
import com.u2d.type.atom.TimeInterval;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author Eitan Suez
 */
public abstract class BaseCalEventView
      extends JXPanel
      implements ComplexEView
{
   protected CalEvent _event;
   protected transient CommandsContextMenuView _cmdsView;

   protected BaseCalEventView.Header _header;
   protected FancyLabel _body;

   public BaseCalEventView(CalEvent event)
   {
      _event = event;
      _event.addChangeListener(this);
      _event.addPropertyChangeListener(this);

      setLayout(new BorderLayout());
      _header = new BaseCalEventView.Header();
      _body = new FancyLabel();
      
      add(_header, BorderLayout.NORTH);
      add(_body, BorderLayout.CENTER);

      _cmdsView = new CommandsContextMenuView();
      _cmdsView.bind(_event, this);

      setTransferHandler(new EOTransferHandler(this));

      stateChanged(null);
      setupExtendSpan();
   }

   private void setupExtendSpan()
   {
      EventSpanAdjuster eventSpanAdjuster = new EventSpanAdjuster(this);
      _body.addMouseListener(eventSpanAdjuster);
      _body.addMouseMotionListener(eventSpanAdjuster);
   }

   public void propertyChange(final PropertyChangeEvent evt)
   {
      if ("icon".equals(evt.getPropertyName()))
      {
         _header.setIcon(_event.iconSm());
      }
   }

   public EObject getEObject() { return _event; }
   public boolean isMinimized() { return true; }

   public void detach()
   {
      _event.removePropertyChangeListener(this);
      _event.removeChangeListener(this);
      _cmdsView.detach();
   }

   static SimpleDateFormat fmt = new SimpleDateFormat("h:mm a");

   public class Header extends JLabel
   {
      public Header()
      {
         setOpaque(true);
         setHorizontalAlignment(JLabel.LEFT);
         setVerticalAlignment(JLabel.CENTER);
         setHorizontalTextPosition(JLabel.RIGHT);
         updateText();
         setIcon(_event.iconSm());
      }

      public void updateText()
      {
         java.util.Date startDate = _event.timeSpan().startDate();
         setText(fmt.format(startDate));
      }

//      private Insets _insets = new Insets(2, 5, 2, 8);
//      public Insets getInsets() { return _insets; }

      public Dimension getMinimumSize() { return getPreferredSize(); }
      public Dimension getMaximumSize() { return getPreferredSize(); }
      public Dimension getPreferredSize()
      {
         Dimension d = super.getPreferredSize();
         d.width += getInsets().left + getInsets().right;
         d.height += getInsets().top + getInsets().bottom;
         return d;
      }

   }

   public void setBounds(Rectangle bounds)
   {
      super.setBounds(bounds);
      getLayout().layoutContainer(this);
   }

   
   class EventSpanAdjuster
         implements MouseMotionListener, MouseListener
   {
      private Component component;
      private Rectangle bottomRegion;
      private int regionThickness = 5;
      private Point lastPoint = null;
      private Cursor defaultCursor;
      private Cursor extendCursor = Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
      
      public EventSpanAdjuster(Component component)
      {
         this.component = component;
         defaultCursor = component.getCursor();
      }
      
      private Point translate(Point pt)
      {
         return new Point(pt.x + _body.getLocation().x ,  pt.y + _body.getLocation().y);
      }

      public void mouseMoved(MouseEvent e)
      {
         if (disableCursorUpdate) return;
         bottomRegion = new Rectangle(0, component.getHeight() - regionThickness,
                                      component.getWidth(), regionThickness);
         
         Point pt = translate(e.getPoint());
      
         if (enteredBottomRegion(pt))
         {
            setExtendCursor();
         }
         else if (exitedBottomRegion(pt))
         {
            restoreDefaultCursor();
         }
         lastPoint = pt;
      }
      
      public void mouseEntered(MouseEvent e)
      {
         if (disableCursorUpdate) return;
         bottomRegion = new Rectangle(0, component.getHeight() - regionThickness,
                                      component.getWidth(), regionThickness);

         Point pt = translate(e.getPoint());

         if (enteredBottomRegion(pt))
         {
            setExtendCursor();
         }
         lastPoint = pt;
      }

      public void mouseExited(MouseEvent e)
      {
         if (disableCursorUpdate) return;
         lastPoint = null;
         restoreDefaultCursor();
      }

      private boolean enteredBottomRegion(Point pt)
      {
         return bottomRegion.contains(pt) && (lastPoint == null || !bottomRegion.contains(lastPoint));
      }
      private boolean exitedBottomRegion(Point pt)
      {
         if (lastPoint == null) return false;
         return bottomRegion.contains(lastPoint) && !bottomRegion.contains(pt);
      }

      private void setExtendCursor()
      {
         component.setCursor(extendCursor);
         _body.setCursor(extendCursor);
      }

      private void restoreDefaultCursor()
      {
         component.setCursor(defaultCursor);
         _body.setCursor(defaultCursor);
      }

      public void mouseDragged(MouseEvent e)
      {
         int diff = translate(e.getPoint()).y - anchorPt.y;
         component.setSize(component.getWidth(), actualHeight + diff);
         _body.setSize(_body.getWidth(), bodyHeight + diff);
         component.repaint();
      }

      public void mouseClicked(MouseEvent e)
      {
      }

      private Point anchorPt;
      private int actualHeight, bodyHeight;
      private boolean disableCursorUpdate = false;
      public void mousePressed(MouseEvent e)
      {
         disableCursorUpdate = true;
         actualHeight = component.getHeight();
         bodyHeight = _body.getHeight();
         anchorPt = translate(e.getPoint());
      }

      public void mouseReleased(MouseEvent e)
      {
         disableCursorUpdate = false;
         TimeInterval duration = _event.timeSpan().duration();
         long newDurationMilis = component.getHeight() * duration.getMilis() / actualHeight;
         // round duration to a five minute resolution.  e.g. 11:43 AM becomes 11:45 AM
         double newDurationFiveMinutes = newDurationMilis /  (double) 300000;
         int newDurationMinutes = 5 * (int) Math.round(newDurationFiveMinutes);
         TimeInterval newDuration = new TimeInterval(Calendar.MINUTE, newDurationMinutes);
         _event.timeSpan().setDuration(newDuration);
         _event.save();
      }

   }
   
}
