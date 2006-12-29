package com.u2d.view.echo2;

import nextapp.echo2.app.Window;
import nextapp.echo2.app.ContentPane;
import nextapp.echo2.app.Label;
import com.u2d.app.AppSession;
import com.u2d.app.Application;

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
   
   public AppFrame(AppSession appSession)
   {
      super();
      _appSession = appSession;
      _app = _appSession.getApp();

      setTitle(_app.getName());
//         setAppIcon();

      ContentPane contentPane = new ContentPane();
      setContent(contentPane);
      /*
         _centerPane = new JPanel(new BorderLayout());

         _desktopPane = new EODesktopPane();
         _desktopPane.getContextMenu().addSeparator();
         _desktopPane.getContextMenu().add(new QuitAction());
         _desktopPane.setEnabled(false);
         _centerPane.add(_desktopPane, BorderLayout.CENTER);
         setupMenu();

         _msgPnl = new MessagePanel();
         contentPane.add(_msgPnl, BorderLayout.SOUTH);

         contentPane.add(_centerPane, BorderLayout.CENTER);

         setSize(800, 600);
         UIUtils.centerOnScreen(this);
         setupQuitHooks();

         listenForUserEvents();
      */

   }
   
   private void forNow()
   {
      setTitle("Hello, world!");

      ContentPane contentPane = new ContentPane();
      setContent(contentPane);

      contentPane.add(new Label("Hello, world!"));
   }
}
