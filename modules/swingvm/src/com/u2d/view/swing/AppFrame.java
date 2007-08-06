/*
 * Created on Dec 15, 2003
 */
package com.u2d.view.swing;

import java.awt.*;
import java.awt.event.*;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import javax.swing.*;
import java.io.*;
import java.util.*;
import com.u2d.ui.*;
import com.u2d.ui.desktop.Positioning;
import com.u2d.ui.desktop.CloseableJInternalFrame;
import com.u2d.view.swing.dnd.*;
import com.u2d.view.swing.list.CommandsMenuView;
import com.u2d.view.swing.atom.URIRenderer;
import com.u2d.app.*;
import com.u2d.pubsub.*;
import static com.u2d.pubsub.AppEventType.*;
import com.u2d.persist.HBMSingleSession;
import com.u2d.pattern.Filter;
import com.u2d.element.Command;
import com.u2d.utils.Launcher;
import com.u2d.type.atom.URI;
import com.u2d.css4swing.style.ComponentStyle;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.builder.PanelBuilder;

/**
 * @author Eitan Suez
 */
public class AppFrame extends JFrame
{
   private AppSession _appSession;
   private Application _app;
   private JMenuBar _menuBar;
   private CommandsMenuView _userMenu = new CommandsMenuView(new Filter()
   {
      String[] validCmds = {"ResetClassBar", "EditPreferences", "LogOut", 
                            "ChangePassword", "Open", "Edit"};
      public boolean exclude(Object item)
      {
         Command cmd = (Command) item;
         for (String validCmd : validCmds)
         {
            if (validCmd.equals(cmd.name()))
               return false;
         }
         return true;
      }
   });
   
   private JPanel _centerPane;
   private OutlookFolderView _classBar = new OutlookFolderView();
   private ClassMenu _classMenu = new ClassMenu();

   private EODesktopPane _desktopPane;

   private ClassLoader _loader = Thread.currentThread().getContextClassLoader();
   private java.net.URL _imgURL = _loader.getResource("images/App32.png");
   private ImageIcon _appIcon = new ImageIcon(_imgURL);
   
   
   public AppFrame(AppSession appSession)
   {
      _appSession = appSession;
      _app = _appSession.getApp();

      setTitle(_app.getName());
      setIconImage(_appIcon.getImage());

      JPanel contentPane = (JPanel) getContentPane();
      _centerPane = new JPanel(new BorderLayout());

      _desktopPane = new EODesktopPane();
      _desktopPane.getContextMenu().addSeparator();
      _desktopPane.getContextMenu().add(new QuitAction());
      _desktopPane.setEnabled(false);
      _centerPane.add(_desktopPane, BorderLayout.CENTER);
      setupMenu();

      contentPane.add(_centerPane, BorderLayout.CENTER);

      setSize(800, 600);
      UIUtils.centerOnScreen(this);
      setupQuitHooks();

      listenForUserEvents();
      
      setupKeyboardShorcuts();
   }
   
   private void setupKeyboardShorcuts()
   {
      JPanel contentPane = (JPanel) getContentPane();
         
      contentPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
            put(KeyStroke.getKeyStroke("alt SLASH"), "focus-classbar");
      contentPane.getActionMap().put("focus-classbar", new AbstractAction()
         {
            public void actionPerformed(ActionEvent e)
            {
               _classBar.focusItem();
            }
         });
   }

   private void listenForUserEvents()
   {
      _appSession.addAppEventListener(LOGIN, new AppEventListener()
      {
         public void onEvent(com.u2d.pubsub.AppEvent evt)
         {
            SwingUtilities.invokeLater(new Runnable()
            {
               public void run()
               {
                  showClassBar();
                  showUserMenu();
                  _desktopPane.setEnabled(true); // enable context menu
                  restoreUserDesktop();
               }
            });
         }
      });
      _appSession.addAppEventListener(LOGOUT, new AppEventListener()
      {
         public void onEvent(com.u2d.pubsub.AppEvent evt)
         {
            saveUserDesktop();
            SwingUtilities.invokeLater(new Runnable()
            {
               public void run()
               {
                  _desktopPane.closeAllChildren();
                  hideClassBar();
                  hideUserMenu();
                  _desktopPane.setEnabled(false); // disable context menu

                  new Thread()
                  {
                     public void run()
                     {
                        PersistenceMechanism pmech = _app.getPersistenceMechanism();
                        if (pmech instanceof HBMSingleSession)
                        {
                           ((HBMSingleSession) pmech).newSession();
                        }
                     }
                  }.start();
               }
            });
         }
      });
   }

   private void setupMenu()
   {
      _menuBar = new JMenuBar();
      _menuBar.add(fileMenu());
      _menuBar.add(helpMenu());
      setJMenuBar(_menuBar);
   }
   private JMenu fileMenu()
   {
      JMenu fileMenu = configMenu("menubar.file");
      fileMenu.add(new QuitAction());
      return fileMenu;
   }
   private JMenu helpMenu()
   {
      JMenu helpMenu = configMenu("menubar.help");
      helpMenu.add(new AboutAction());
      helpMenu.add(new HelpContentsAction());
      return helpMenu;
   }
   private JMenu configMenu(String key)
   {
      TextWithMnemonic twm = TextWithMnemonic.lookup(key);
      JMenu menu = new JMenu(twm.text());
      if (twm.hasMnemonic())
      {
         menu.setMnemonic(twm.mnemonic());
      }
      return menu;
   }

   /* ** public interface ** */
   public void addLoginDialog(LoginDialog loginDialog)
   {
      _desktopPane.add(loginDialog, JLayeredPane.MODAL_LAYER);
      UIUtils.center(_desktopPane, loginDialog);
   }

   public JInternalFrame addFrame(JInternalFrame frame)
   {
      _desktopPane.addFrame(frame);
      return frame;
   }
   public JInternalFrame addFrame(JInternalFrame frame, Positioning positioning)
   {
      _desktopPane.addFrame(frame, positioning);
      return frame;
   }
   public void centerFrame(JInternalFrame frame) { _desktopPane.positionFrame(frame, Positioning.CENTERED); }
   public JInternalFrame getSelectedFrame() { return _desktopPane.getSelectedFrame(); }

   
   public void popup(JPopupMenu menu)
   {
      _desktopPane.popup(menu);
   }

   //===
   private void showClassBar()
   {
      SwingUtilities.invokeLater( new Runnable()
         {
            public void run()
            {
               User currentUser = _appSession.getUser();
               _classBar.bind(currentUser.getClassBar());
               _classMenu.bind(currentUser.getClassBar(), _menuBar, _menuBar.getComponentCount() - 2);
               
               _centerPane.add(_classBar, BorderLayout.WEST);
               _classBar.focusItem();
               _centerPane.revalidate(); _centerPane.repaint();
            }
         });
   }
   private void hideClassBar()
   {
      SwingUtilities.invokeLater( new Runnable()
         {
            public void run()
            {
               _classBar.detach();
               _classMenu.detach();
               _centerPane.remove(_classBar);
               _centerPane.revalidate(); _centerPane.repaint();
            }
         });
   }
   //===

      private void showUserMenu()
      {
         User currentUser = _appSession.getUser();
         _userMenu.bind(currentUser, _menuBar, null);
         
         int usermenuIndex = _menuBar.getComponentCount() - 2;
         _menuBar.add(_userMenu, usermenuIndex);
         _menuBar.revalidate(); _menuBar.repaint();
      }

      private void hideUserMenu()
      {
         SwingUtilities.invokeLater( new Runnable()
         {
            public void run()
            {
               _userMenu.detach();
               _menuBar.revalidate(); _menuBar.repaint();
            }
         });
      }



   //===

   class QuitAction extends AbstractAction
   {
      public QuitAction()
      {
         putValue(javax.swing.Action.ACTION_COMMAND_KEY, "exit");
         configAction(this, "menubar.file.exit");

         KeyStroke quitStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Q,
                                                       Platform.mask());
         putValue(javax.swing.Action.ACCELERATOR_KEY, quitStroke);
      }

      public void actionPerformed(ActionEvent evt)
      {
         setWaitCursor();
         quit();
      }
   }
   
   private void configAction(Action action, String key)
   {
      TextWithMnemonic twm = TextWithMnemonic.lookup(key);
      action.putValue(javax.swing.Action.NAME, twm.text());
      if (twm.hasMnemonic())
      {
        action.putValue(Action.MNEMONIC_KEY, new Integer(twm.mnemonic()));
      }
   }
   
   
   private JDialog aboutDlg;
   
   class AboutAction extends AbstractAction
   {
      public AboutAction()
      {
         configAction(this, "menubar.help.about");
      }
      public void actionPerformed(ActionEvent e)
      {
         if (aboutDlg == null)
         {
            aboutDlg = new AboutDlg();
         }
         aboutDlg.setVisible(true);
      }
   }
   class AboutDlg extends JDialog implements ActionListener
   {
      JButton closeBtn;
      
      AboutDlg()
      {
         super(AppFrame.this, "About "+_app.getName(), true);
         setResizable(false);
         
         laymeout();
         
         pack();
         Point center = UIUtils.computeCenter(AppFrame.this, AboutDlg.this);
         setLocation(center);
         addComponentListener(new ComponentAdapter()
         {
            public void componentShown(ComponentEvent e)
            {
               SwingUtilities.invokeLater(new Runnable()
               {
                  public void run()
                  {
                     closeBtn.requestFocus();
                  }
               });
            }
         });
      }
      private void laymeout()
      {
         JPanel contentPane = (JPanel) getContentPane();
         contentPane.setLayout(new BorderLayout());
         
         FormLayout layout = new FormLayout("pref",  // columns 
               "pref, 3dlu, pref, 3dlu, pref, 3dlu, pref");           // rows
         
         PanelBuilder builder = new PanelBuilder(layout);
         CellConstraints cc = new CellConstraints();
         JLabel titleView = new JLabel(_app.title(), _appIcon, JLabel.LEFT);
         ComponentStyle.addClass(titleView, "title");
         builder.add(titleView, cc.xy(1, 1));
         
         JTextArea descriptionArea = new JTextArea(_app.getDescription(), 5, 40);
         descriptionArea.setEditable(false);
         descriptionArea.setOpaque(false);
         builder.add(new JScrollPane(descriptionArea), cc.xy(1, 3));
         
         URIRenderer link = new URIRenderer();
         link.render(new URI(_app.getHelpContentsUrl()));
         builder.add(link, cc.xy(1, 5));
         
         closeBtn = new JButton("OK");
         closeBtn.addActionListener(AboutDlg.this);
         builder.add(closeBtn, cc.xy(1, 7, "center, center"));
         
         JPanel mainArea = builder.getPanel();
         ComponentStyle.setIdent(mainArea, "aboutPnl");
         contentPane.add(mainArea, BorderLayout.CENTER);
      }

      public void actionPerformed(ActionEvent e)
      {
         AboutDlg.this.setVisible(false);
      }
   }
   
   class HelpContentsAction extends AbstractAction
   {
      public HelpContentsAction()
      {
         configAction(this, "menubar.help.contents");
      }
      public void actionPerformed(ActionEvent e)
      {
         Launcher.openInBrowser(_app.getHelpContentsUrl());
      }
   }

   private void setupQuitHooks()
   {
      addWindowListener(new WindowAdapter()
            {
               public void windowClosing(WindowEvent evt)
               {
                  quit();
               }
            });


      if (Platform.APPLE) new AppleQuitListener();
   }

   class AppleQuitListener extends com.apple.eawt.ApplicationAdapter
   {
      AppleQuitListener()
      {
         new com.apple.eawt.Application().addApplicationListener(this);
      }

      public void handleQuit(com.apple.eawt.ApplicationEvent evt)
      {
         evt.setHandled(false);
         quit();
      }
   }

   private void quit()
   {
      if (_appSession.getUser() != null) _appSession.onLogout();
      System.exit(0);
   }


   public void onMessage(String msg)
   {
      _desktopPane.message(msg);
   }


   private static final Cursor WAITCURSOR = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
   private static final Cursor DEFAULTCURSOR = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

   public void setWaitCursor()
   {
      getGlassPane().setCursor(WAITCURSOR);
      getGlassPane().setVisible(true);
   }
   public void setDefaultCursor()
   {
      getGlassPane().setCursor(DEFAULTCURSOR);
      getGlassPane().setVisible(false);
   }

   /* ======
    *
    * methods related to saving and restoring the user desktop follow..
    * 
    * ------
    */

   private void saveUserDesktop()
   {
      ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
      XMLEncoder enc = new XMLEncoder(baos);
      serialize(enc);
      enc.close();
      User currentUser = _appSession.getUser();
      currentUser.getDesktop().setValue(baos.toString());
      currentUser.save();
   }

   private void restoreUserDesktop()
   {
      String desktop = _appSession.getUser().getDesktop().stringValue();
      if (desktop == null || desktop.length() <= 0) return;  // nothing saved
      XMLDecoder dec = new XMLDecoder(new ByteArrayInputStream(desktop.getBytes()));
      deserialize(dec);
   }
   
   private void serialize(XMLEncoder enc)
   {
      enc.writeObject(getBounds());

      JInternalFrame[] frames = _desktopPane.getAllFrames();
      java.util.List<CloseableJInternalFrame> framesToSave = new ArrayList<CloseableJInternalFrame>();
      
      for (JInternalFrame f : frames)
      {
         if (!f.isVisible() || f.isIcon()) continue;
         if (!(f instanceof CloseableJInternalFrame)) continue;
         CloseableJInternalFrame cjif = (CloseableJInternalFrame) f;
         framesToSave.add(cjif);
      }
      
      enc.writeObject(framesToSave.size());
      for (CloseableJInternalFrame f : framesToSave)
      {
         f.serialize(enc);
      }
      
/*
         else if (frames[i] instanceof GenericFrame)
         {
            View view = ((GenericFrame) frames[i]).getView();
            if (view instanceof FindView)
            {
               ComplexType type = ((FindView) view).getType();
               frameInfo.add(new FrameInfo(FrameInfo.FIND, new Long(0),
                                           type.getJavaClass().getName()));
            }
         }
      }
      */
   }
   private void deserialize(XMLDecoder dec)
   {
      Rectangle bounds = (Rectangle) dec.readObject();
      if (bounds != null) setBounds(bounds);

      int numFrames = (Integer) dec.readObject();
      for (int i=0; i<numFrames; i++)
      {
         Class viewType = (Class) dec.readObject();
         try
         {
            CloseableJInternalFrame f = (CloseableJInternalFrame) viewType.newInstance();
            f.deserialize(dec);
            if (!(f instanceof FlexiFrame) || !((FlexiFrame) f).isEmpty())
            {
               addFrame(f, Positioning.NONE);
            }
         }
         catch (InstantiationException ex) { ex.printStackTrace(); }
         catch (IllegalAccessException ex) { ex.printStackTrace(); }
      }
   }
   
}
