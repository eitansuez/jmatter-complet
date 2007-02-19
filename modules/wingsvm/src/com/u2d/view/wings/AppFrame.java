package com.u2d.view.wings;

import org.wings.*;
import com.u2d.app.*;
import com.u2d.ui.desktop.Positioning;
import com.u2d.type.composite.Folder;
import com.u2d.element.Command;
import com.u2d.pubsub.AppEventListener;
import static com.u2d.pubsub.AppEventType.*;
import com.u2d.persist.HibernatePersistor;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.AbstractAction;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jun 22, 2006
 * Time: 8:43:04 PM
 */
public class AppFrame extends SFrame
{
   private SDesktopPane _desktopPane;
   private MessagePanel _msgPnl;

   private AppSession _appSession;
   private Application _app;
   
   private SMenuBar _menuBar;
   private SMenu _userMenu;

   private SPanel _centerPane;
   private SComponent _classesView;
   
   
   private transient Logger _tracer = Tracing.tracer();

   public AppFrame(AppSession appSession)
   {
      super();
      _appSession = appSession;
      _app = _appSession.getApp();
      
      setTitle(_app.getName());

      SContainer contentPane = getContentPane();
      contentPane.setLayout(new SBorderLayout());
      
      _centerPane = new SPanel(new SBorderLayout());
      _desktopPane = new SDesktopPane();
      _centerPane.add(_desktopPane, SBorderLayout.CENTER);

      _msgPnl = new MessagePanel();
      contentPane.add(_centerPane, SBorderLayout.CENTER);
      contentPane.add(_msgPnl, SBorderLayout.SOUTH);

      _menuBar = new SMenuBar();
      setMenuBar(_menuBar);
      makeUserMenu();
      listenForUserEvents();
   }

   private void setMenuBar(SMenuBar menuBar)
   {
      SToolBar toolbar = new SToolBar();
      toolbar.add(menuBar);
      _centerPane.add(toolbar, SBorderLayout.NORTH);
   }

   /* ** public interface ** */
   public SInternalFrame addLoginDialog(SInternalFrame loginDialog)
   {
      addFrame(loginDialog, Positioning.CENTERED);
//      loginDialog.setLayer(LayeredPane.MODAL_LAYER);
      return loginDialog;
   }

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


   // ==
   
   private void listenForUserEvents()
   {
      _appSession.addAppEventListener(LOGIN, new AppEventListener()
      {
         public void onEvent(com.u2d.pubsub.AppEvent evt)
         {
            showClassBar();
            showUserMenu();
            _desktopPane.setEnabled(true); // enable context menu
         }
      });
      _appSession.addAppEventListener(LOGOUT, new AppEventListener()
      {
         public void onEvent(com.u2d.pubsub.AppEvent evt)
         {
            closeAllDesktopPaneChildren();
            hideClassBar();
            hideUserMenu();
            _desktopPane.setEnabled(false); // disable context menu

            PersistenceMechanism pmech = _app.getPersistenceMechanism();
            if (pmech instanceof HibernatePersistor)
            {
               ((HibernatePersistor) pmech).newSession();
            }
         }
      });
   }

   private void closeAllDesktopPaneChildren()
   {
      for (int i=0; i<_desktopPane.getComponentCount(); i++)
      {
         if (_desktopPane.getComponent(i) instanceof SInternalFrame)
         {
            SInternalFrame f = (SInternalFrame) _desktopPane.getComponent(i);
            if (f.isVisible())
            {
               f.dispose();  // how nice!  wings does the right thing!
               // that is: if.dispose automatically does a desktoppane.remove
            }
         }
      }
   }

   private void makeClassBar()
   {
      Folder classesFolder = _appSession.getUser().getClassBar();
      _classesView = new OutlookFolderView(classesFolder);
   }
   private void showClassBar()
   {
      if (_classesView == null)
      {
         makeClassBar();
      }
      _centerPane.add(_classesView, SBorderLayout.WEST);
   }
   private void hideClassBar()
   {
      _centerPane.remove(_classesView);
   }

   private static String USERMENUGENERICLABEL = "User";

   private void makeUserMenu()
   {
      _userMenu = new SMenu(USERMENUGENERICLABEL);

      Action logoutAction = new AbstractAction("Logout")
      {
         public void actionPerformed(ActionEvent evt)
         {
            _appSession.onLogout();
         }
      };

      Action chgPassAction = new AbstractAction("Change Password")
      {
         public void actionPerformed(ActionEvent evt)
         {
            User currentUser = _appSession.getUser();
            Command cmd = currentUser.command("ChangePassword");
            try
            {
               cmd.execute(currentUser, null);
            }
            catch (java.lang.reflect.InvocationTargetException ex)
            {
               System.err.println("InvocationTargetException: "+ex.getMessage());
               ex.printStackTrace();
            }
         }
      };

      SMenuItem item = new SMenuItem(chgPassAction);
      _userMenu.add(item);
      item = new SMenuItem(logoutAction);
      _userMenu.add(item);
   }

   private void showUserMenu()
   {
      User currentUser = _appSession.getUser();
      _userMenu.setText(currentUser.toString());
      _menuBar.add(_userMenu);
   }

   private void hideUserMenu()
   {
      _menuBar.remove(_userMenu);
   }

}
