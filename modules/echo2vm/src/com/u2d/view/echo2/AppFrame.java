package com.u2d.view.echo2;

import nextapp.echo2.app.*;
import com.u2d.app.AppSession;
import com.u2d.app.Application;
import com.u2d.app.PersistenceMechanism;
import com.u2d.app.User;
import com.u2d.pubsub.AppEventListener;
import static com.u2d.pubsub.AppEventType.*;
import com.u2d.persist.HibernatePersistor;
import com.u2d.ui.desktop.Positioning;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Dec 28, 2006
 * Time: 4:09:16 PM
 */
public class AppFrame extends Window
{
   private AppSession _appSession;
   private Application _app;
   private DesktopPane _desktopPane;
   private SplitPane _split;
   private ContentPane _placeHolder = new ContentPane();
   
//   private CommandsMenuView _userMenu = new CommandsMenuView(new Filter()
//   {
//      String[] validCmds = {"ResetClassBar", "EditPreferences", "LogOut", 
//                            "ChangePassword", "Open", "Edit"};
//      public boolean exclude(Object item)
//      {
//         Command cmd = (Command) item;
//         for (String validCmd : validCmds)
//         {
//            if (validCmd.equals(cmd.name()))
//               return false;
//         }
//         return true;
//      }
//   });

   private OutlookFolderView _classBar = new OutlookFolderView();

   public AppFrame(AppSession appSession)
   {
      super();
      _appSession = appSession;
      _app = _appSession.getApp();

      setTitle(_app.getName());

      ContentPane contentPane = new ContentPane();
      setContent(contentPane);
      
      _desktopPane = new DesktopPane();
//      _desktopPane.setEnabled(false);
      
      _split = new SplitPane(SplitPane.ORIENTATION_HORIZONTAL_LEADING_TRAILING);
      _split.setSeparatorWidth(new Extent(10));
      _split.setResizable(true);
      
      _split.add(_placeHolder);
      _split.add(_desktopPane);
      contentPane.add(_split);
      
      /*
         _desktopPane = new EODesktopPane();
         _desktopPane.getContextMenu().addSeparator();
         _desktopPane.getContextMenu().add(new QuitAction());
         setupMenu();

         _msgPnl = new MessagePanel();
         contentPane.add(_msgPnl, BorderLayout.SOUTH);

         setupQuitHooks();
      */

      listenForUserEvents();
   }
   
   private void listenForUserEvents()
   {
      _appSession.addAppEventListener(LOGIN, new AppEventListener()
      {
         public void onEvent(com.u2d.pubsub.AppEvent evt)
         {
            showClassBar();
//            showUserMenu();
//            _desktopPane.setEnabled(true); // enable context menu
         }
      });
      _appSession.addAppEventListener(LOGOUT, new AppEventListener()
      {
         public void onEvent(com.u2d.pubsub.AppEvent evt)
         {
            _desktopPane.removeAll();
            hideClassBar();
//            hideUserMenu();
//            _desktopPane.setEnabled(false); // disable context menu

            PersistenceMechanism pmech = _app.getPersistenceMechanism();
            if (pmech instanceof HibernatePersistor)
            {
               ((HibernatePersistor) pmech).newSession();
            }
         }
      });
   }
   
   private void showClassBar()
   {
      User currentUser = _appSession.getUser();
      _classBar.bind(currentUser.getClassBar());
      
      _split.remove(0);
      _split.add(_classBar, 0);
   }
   private void hideClassBar()
   {
      _classBar.detach();
      _split.remove(_classBar);
      _split.add(_placeHolder, 0);
   }
   
   
   public WindowPane addLoginDialog(LoginDialog loginDialog)
   {
      addFrame(loginDialog, Positioning.CENTERED);
      loginDialog.setModal(true);
      return loginDialog;
   }
   public WindowPane addFrame(WindowPane frame)
   {
      _desktopPane.addFrame(frame);
      return frame;
   }
   public WindowPane addFrame(WindowPane frame, Positioning positioning)
   {
      _desktopPane.addFrame(frame, positioning);
      return frame;
   }
   public void centerFrame(WindowPane frame) { _desktopPane.positionFrame(frame, Positioning.CENTERED); }
   
}
