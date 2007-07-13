/*
 * Created on Apr 14, 2004
 */
package com.u2d.view.swing.simplecal;

import com.u2d.calendar.*;
import com.u2d.view.swing.dnd.EOTransferHandler;
import javax.swing.*;
import java.awt.*;
import java.beans.*;
import java.text.SimpleDateFormat;
import com.u2d.model.EObject;
import com.u2d.ui.*;
import com.u2d.view.*;
import com.u2d.view.swing.list.CommandsContextMenuView;
import org.jdesktop.swingx.JXPanel;

/**
 * @author Eitan Suez
 */
public class CalEventView extends JXPanel
      implements ComplexEView
{
   private CalEvent _event;
   private transient CommandsContextMenuView _cmdsView;

   private Header _header;
   private FancyLabel _body;

   public CalEventView(CalEvent event)
   {
      _event = event;
      _event.addChangeListener(this);
      _event.addPropertyChangeListener(this);

      setLayout(new BorderLayout());
      _header = new Header();
      _body = new FancyLabel();
      
      setupColor();
      
      add(_header, BorderLayout.NORTH);
      add(_body, BorderLayout.CENTER);

      _cmdsView = new CommandsContextMenuView();
      _cmdsView.bind(_event, this);

      setTransferHandler(new EOTransferHandler(this));

      stateChanged(null);
   }
   
   private void setupColor()
   {
      final Color backgroundColor = _event.type().colorCode();
      _header.setBackground(backgroundColor);
      _body.setupColor(backgroundColor);
   }

   public void propertyChange(final PropertyChangeEvent evt)
   {
      if ("icon".equals(evt.getPropertyName()))
      {
         _header.setIcon(_event.iconSm());
      }
   }

   public void stateChanged(javax.swing.event.ChangeEvent evt)
   {
      SwingUtilities.invokeLater(
         new Runnable()
         {
            public void run()
            {
               _header.updateText();
               _body.setText(_event.calTitle().toString());
               _header.setFont(_body.getFont());
               
               revalidate(); repaint();  // picks up timespan changes
            }
         });
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

   class Header extends JLabel
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

      private void updateText()
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
