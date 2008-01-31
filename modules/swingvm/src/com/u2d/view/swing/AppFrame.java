/*
 * Created on Dec 15, 2003
 */
package com.u2d.view.swing;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.u2d.app.*;
import com.u2d.css4swing.style.ComponentStyle;
import com.u2d.css4swing.CSSEngine;
import com.u2d.element.Command;
import com.u2d.model.ComplexType;
import com.u2d.pattern.Filter;
import com.u2d.pattern.Onion;
import com.u2d.persist.HBMSingleSession;
import com.u2d.pubsub.AppEventListener;
import static com.u2d.pubsub.AppEventType.LOGIN;
import static com.u2d.pubsub.AppEventType.LOGOUT;
import com.u2d.type.atom.URI;
import com.u2d.type.composite.Folder;
import com.u2d.ui.Platform;
import com.u2d.ui.UIUtils;
import com.u2d.ui.CardPanel;
import com.u2d.ui.desktop.CloseableJInternalFrame;
import com.u2d.ui.desktop.Positioning;
import com.u2d.utils.Launcher;
import com.u2d.view.swing.atom.URIRenderer;
import com.u2d.view.swing.dnd.EODesktopPane;
import com.u2d.view.swing.list.CommandsMenuView;
import com.u2d.interaction.Instruction;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * @author Eitan Suez
 */
public class AppFrame extends JFrame
{
   private AppSession _appSession;
   private Application _app;
   private JMenuBar _menuBar;
   private ImageIcon _appIcon;

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
   private CardPanel _cardPanel;

   public AppFrame(AppSession appSession)
   {
      setupUI();
      if (appSession != null)
      {
         setupApp(appSession);
      }
   }
   
   public void appLoaded(AppSession appSession)
   {
//      CSSEngine.getInstance().restyle((JPanel) getContentPane());
      setupApp(appSession);
   }
   public void appUnloaded()
   {
      _appSession.removeAppEventListener(LOGIN, _loginListener);
      _appSession.removeAppEventListener(LOGOUT, _logoutListener);
      _appSession = null;
      _cardPanel.show("app-off");
   }
   private void setupUI()
   {
      setTitle("JMatter");
      setupAppIcon();

      JPanel contentPane = (JPanel) getContentPane();

      _centerPane = new JPanel(new BorderLayout());

      _cardPanel = new CardPanel();
      _cardPanel.add(new AppLoaderPanel(), "app-off");
      _cardPanel.add(_centerPane, "app-on");
      _cardPanel.show("app-off");
      
      _desktopPane = new EODesktopPane();
      // TODO: re-enable these two lines:
//      _desktopPane.getContextMenu().addSeparator();
//      _desktopPane.getContextMenu().add(new QuitAction());
      _desktopPane.setEnabled(false);
      _centerPane.add(_desktopPane, BorderLayout.CENTER);
      contentPane.add(_cardPanel, BorderLayout.CENTER);
      
      setPreferredSize(new Dimension(800, 600));
      setSize(getPreferredSize());
      
      UIUtils.centerOnScreen(this);
      setupQuitHooks();
   }
   
   private void setupApp(AppSession appSession)
   {
      _appSession = appSession;
      _app = _appSession.getApp();

      setTitle(_app.getName());
      setupAppIcon();

      listenForUserEvents();
      setupInstructionView();  // TODO:  support multiple app loadings (unbind/bind instrview)

      // NEEDS WORK
      setupMenu();  // TODO:  needs work to update itself when app is loaded
      // in general:  create a JMatterApp application object and just bind the ui
      // to a different app object.
      
      _cardPanel.show("app-on");
   }
   
   private void setupAppIcon()
   {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      java.net.URL imgURL = loader.getResource("images/App32.png");
      if (imgURL != null)
      {
         _appIcon = new ImageIcon(imgURL);
         setIconImage(_appIcon.getImage());
      }
   }
   
   private void setupInstructionView() {
      InstructionView instructionView = new InstructionView(Instruction.getInstance());
      ComponentStyle.setIdent(instructionView, "command-panel");
      _desktopPane.add(instructionView, JLayeredPane.POPUP_LAYER);
      UIUtils.center(_desktopPane, instructionView);
   }
   
   private void setupKeyboardShorcuts()
   {
      bindKeyStroke("alt SLASH", "focus-classbar", new AbstractAction()
         {
            public void actionPerformed(ActionEvent e)
            {
               _classBar.focusItem();
            }
         });
      bindKeyStroke(KeyStroke.getKeyStroke('W', Platform.mask()), "close-window", new AbstractAction() {
         public void actionPerformed(ActionEvent e)
         {
            JInternalFrame jif = _desktopPane.getSelectedFrame();
            if (jif != null && jif instanceof CloseableJInternalFrame)
            {
               ((CloseableJInternalFrame) jif).close();
            }
         }
      });
      bindKeyStroke(KeyStroke.getKeyStroke('I', Platform.mask()), "invoke-instruction", new AbstractAction() {
         public void actionPerformed(ActionEvent e)
         {
            Instruction.getInstance().activate();
         }
      });
   }
   
   private void bindKeyStroke(String shortcut, String key, Action action)
   {
      bindKeyStroke(KeyStroke.getKeyStroke(shortcut), key, action);
   }
   private void bindKeyStroke(KeyStroke kstroke, String key, Action action)
   {
      JPanel contentPane = (JPanel) getContentPane();
      contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(kstroke, key);
      contentPane.getActionMap().put(key, action);
   }
   private void detachKeyStroke(String key)
   {
      JPanel contentPane = (JPanel) getContentPane();
      contentPane.getActionMap().remove(key);
   }
   private void detachKeystrokes()
   {
      detachKeyStroke("focus-classbar");
      detachKeyStroke("close-window");
      detachKeyStroke("invoke-instruction");
   }
   
   private Set<String> keybindings = new HashSet<String>();
   
   private void bindTypeKeyboardShortcuts(Folder userClassBar)
   {
      keybindings = new HashSet<String>();
      for (int i=0; i<userClassBar.size(); i++)
      {
         Object item = userClassBar.get(i);
         if (item instanceof Folder)
         {
            Folder folder = (Folder) item;
            for (int j=0; j<folder.size(); j++)
            {
               Object subItem = folder.get(j);
               if (subItem instanceof ComplexType)
               {
                  ComplexType type = (ComplexType) subItem;
                  bindTypeCommands(type);
               }
            }
         }
      }
   }

   private void bindTypeCommands(ComplexType type)
   {
      Onion typeCommands = type.commands();
      for (Iterator itr = typeCommands.deepIterator(); itr.hasNext(); )
      {
         Command cmd = (Command) itr.next();
         if (cmd.hasShortcut())
         {
            String key = keyFor(type, cmd);
            bindKeyStroke(cmd.shortcut(), key, new CommandAdapter(cmd, null));
            keybindings.add(key);
         }
      }
   }

   private void detachTypeKeyboardShortcuts()
   {
      for (String key : keybindings)
      {
         detachKeyStroke(key);
      }
   }
   
   private String keyFor(ComplexType type, Command cmd)
   {
      return String.format("%s-%s", type.name(), cmd.name());
   }

   AppEventListener _loginListener, _logoutListener;
   private void listenForUserEvents()
   {
      _loginListener = new AppEventListener()
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
                  setupKeyboardShorcuts();
                  restoreUserDesktop();
               }
            });
         }
      };
      _appSession.addAppEventListener(LOGIN, _loginListener);
      
      _logoutListener = new AppEventListener()
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
                  detachKeystrokes();
                  _desktopPane.setEnabled(false); // disable context menu

                  AppLoader.getInstance().newThread(new Runnable()
                  {
                     public void run()
                     {
                        PersistenceMechanism pmech = _app.getPersistenceMechanism();
                        if (pmech instanceof HBMSingleSession)
                        {
                           ((HBMSingleSession) pmech).newSession();
                        }
                     }
                  }).start();
               }
            });
         }
      };
      _appSession.addAppEventListener(LOGOUT, _logoutListener);
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
   public void addLoginDialog(final LoginDialog loginDialog)
   {
      _desktopPane.add(loginDialog, JLayeredPane.MODAL_LAYER);
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
               Folder userClassBar = currentUser.getClassBar();
               _classBar.bind(userClassBar);
               _classMenu.bind(userClassBar, _menuBar, _menuBar.getComponentCount() - 2);
               
               bindTypeKeyboardShortcuts(userClassBar);
               
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
               detachTypeKeyboardShortcuts();
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
         UIUtils.center(AppFrame.this, AboutDlg.this, true);
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
         builder.add(titleView, cc.rc(1, 1));
         
         JTextArea descriptionArea = new JTextArea(_app.getDescription(), 5, 40);
         descriptionArea.setEditable(false);
         descriptionArea.setOpaque(false);
         builder.add(new JScrollPane(descriptionArea), cc.rc(3, 1));
         
         URIRenderer link = new URIRenderer();
         link.render(new URI(_app.getHelpContentsUrl()));
         builder.add(link, cc.rc(5, 1));
         
         closeBtn = new JButton("OK");
         closeBtn.addActionListener(AboutDlg.this);
         builder.add(closeBtn, cc.rc(7, 1, "center, center"));
         
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
      if (_appSession != null && _appSession.getUser() != null) _appSession.onLogout();
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
      
      // currentuser lives for entire session.  if obtain a new session, must attach
      // object to new session;  this is not always the cause but a good precaution.
      // actually avoids an exception in certain circumstances.
      HBMPersistenceMechanism hbm = (HBMPersistenceMechanism) _app.getPersistenceMechanism();
      hbm.getSession().update(currentUser);
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
   
   
   class AppLoaderPanel extends JPanel
   {
      JTextField tf;
      
      public AppLoaderPanel()
      {
         setLayout(new FlowLayout(FlowLayout.CENTER));
         JLabel lbl = new JLabel("App URL:");
         tf = new JTextField("", 40);
         lbl.setLabelFor(tf);
         JButton loadBtn = new JButton("Load App");
         loadBtn.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               try
               {
                  URL url = new URL(tf.getText());
                  AppLoader.getInstance().loadApplication(url);
               }
               catch (MalformedURLException ex)
               {
                  ex.printStackTrace();
               }
            }
         });
            
         add(lbl);
         add(tf);
         add(loadBtn);
      }
      
   }
   
}
