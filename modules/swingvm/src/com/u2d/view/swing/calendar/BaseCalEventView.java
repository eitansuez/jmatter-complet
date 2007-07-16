package com.u2d.view.swing.calendar;

import org.jdesktop.swingx.JXPanel;
import com.u2d.view.ComplexEView;
import com.u2d.view.swing.list.CommandsContextMenuView;
import com.u2d.view.swing.dnd.EOTransferHandler;
import com.u2d.calendar.CalEvent;
import com.u2d.ui.FancyLabel;
import com.u2d.model.EObject;
import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.text.SimpleDateFormat;

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
   
}
