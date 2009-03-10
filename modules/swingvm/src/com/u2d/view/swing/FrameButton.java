package com.u2d.view.swing;

import com.u2d.ui.BaseToggleButton;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Aug 8, 2008
 * Time: 1:22:05 PM
 *
 * For lack of a better name. This is the button used in AppFrame's window button bar
 */
public class FrameButton extends BaseToggleButton implements ActionListener, PropertyChangeListener
{
   private JInternalFrame myframe;
   private JDesktopPane desktopPane;

   FrameButton(JInternalFrame frame, JDesktopPane desktopPane)
   {
      myframe = frame;
      this.desktopPane = desktopPane;
      setText(frame.getTitle());
      setIcon(frame.getFrameIcon());
      frame.addPropertyChangeListener("title", this);
      frame.addPropertyChangeListener("frameIcon", this);
      addActionListener(this);
   }

   public void propertyChange(PropertyChangeEvent evt)
   {
      if ("title".equals(evt.getPropertyName()))
      {
         setText(myframe.getTitle());
      }
      else if ("frameIcon".equals(evt.getPropertyName()))
      {
         setIcon(myframe.getFrameIcon());
      }
   }

   public void cleanup()
   {
      myframe.removePropertyChangeListener("title", this);
      myframe.removePropertyChangeListener("frameIcon", this);
      myframe = null;
      desktopPane = null;
   }

   public JInternalFrame getFrame()
   {
      return myframe;
   }

   public void actionPerformed(ActionEvent e)
   {
      desktopPane.getDesktopManager().activateFrame(myframe);
   }
}

