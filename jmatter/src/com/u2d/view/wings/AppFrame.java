package com.u2d.view.wings;

import org.wings.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.Jdk14Logger;
import com.u2d.app.Application;
import com.u2d.app.Tracing;
import com.u2d.ui.desktop.Positioning;
import com.u2d.type.composite.Folder;
import java.awt.Cursor;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jun 22, 2006
 * Time: 8:43:04 PM
 */
public class AppFrame extends SFrame
{
   private Application _app;
   private SPanel _centerPane;
   private SDesktopPane _desktopPane;
   private MessagePanel _msgPnl;
   private SComponent _classesView;

   private transient Logger _tracer = Tracing.tracer();

   public AppFrame(Application app)
   {
      super();
      _app = app;
      setTitle(_app.getName());

      SContainer contentPane = getContentPane();
      _centerPane = new SPanel(new SBorderLayout());
      _desktopPane = new SDesktopPane();
      _centerPane.add(_desktopPane, SBorderLayout.CENTER);

      Folder classesFolder = _app.getClassesFolder();
      _classesView = new OutlookFolderView(classesFolder);
      _centerPane.add(_classesView, SBorderLayout.WEST);

      _msgPnl = new MessagePanel();
      contentPane.add(_msgPnl, SBorderLayout.SOUTH);
      contentPane.add(_centerPane, SBorderLayout.CENTER);
   }


   /* ** public interface ** */

   public SInternalFrame addFrame(SInternalFrame frame)
   {
      _tracer.fine("inappframe.addframe: about to add internal frame: "+frame.getTitle());
      frame.setVisible(true);
      _desktopPane.add(frame);
      _tracer.fine("frame added to desktop");
      return frame;
   }
   public SInternalFrame addFrame(SInternalFrame frame, Positioning positioning)
   {
      addFrame(frame);
      return frame;
   }

   public void centerFrame(SInternalFrame frame)
   {
      // i need to learn the wings api in more detail to be able to answer
      // many of these questions..
//      _desktopPane.positionFrame(frame, Positioning.CENTERED);
   }

   public void onMessage(String msg)
   {
      _msgPnl.message(msg);
   }

   public void setCursor(Cursor cursor)
   {
      // is there a notion of a cursor in wings?  probably not.
   }
   public SInternalFrame getSelectedFrame()
   {
      // is there a notion of focus on a given internalframe in wings?
      return (SInternalFrame) _desktopPane.getComponent(0);
   }



}
