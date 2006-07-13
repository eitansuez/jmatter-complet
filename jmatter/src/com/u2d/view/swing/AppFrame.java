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
import com.u2d.ui.*;
import com.u2d.ui.desktop.Positioning;
import com.u2d.ui.lf.*;
import com.u2d.view.*;
import com.u2d.view.swing.dnd.*;
import com.u2d.view.swing.find.FindView;
import com.u2d.view.swing.list.ListEOFrame;
import com.u2d.model.AbstractListEO;
import com.u2d.model.ComplexEObject;
import com.u2d.model.ComplexType;
import com.u2d.model.EObject;
import com.u2d.app.*;
import com.u2d.pubsub.*;
import com.u2d.view.swing.calendar.*;
import com.u2d.calendar.*;
import com.u2d.element.Command;
import com.u2d.type.composite.Folder;
import com.u2d.persist.HBMSingleSession;

/**
 * @author Eitan Suez
 */
public class AppFrame extends JFrame
{
   private Application _app;
   private JMenuBar _menuBar;
   private JMenu _userMenu;
   private JPanel _centerPane;
   private LookAndFeelSupport _lfSupport;
   private JComponent _classesView;
   private EODesktopPane _desktopPane;
   private MessagePanel _msgPnl;

   public AppFrame(Application app)
   {
      _app = app;

      setTitle(_app.getName());
      setAppIcon();

      JPanel contentPane = (JPanel) getContentPane();
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

      _lfSupport.setLF(_app.getLFName());
      setSize(800, 600);
      UIUtils.centerOnScreen(this);
      setupQuitHooks();

      makeUserMenu();
      listenForUserEvents();

//      _lfSupport.addLFChangeListener(
//            new LFChangeListener()
//            {
//               public void LFChanged(LFChangeEvent evt)
//               {
//                  Component[] items = _classBar.getComponents();
//                  for (int i=0; i<items.length; i++)
//                  {
//                     // how do i get from the component to its context menu???
//                     // SwingUtilities.updateComponentTreeUI(contextMenu);
//                  }
//               }
//            }
//            );

   }

   private void listenForUserEvents()
   {
      _app.addAppEventListener("LOGIN", new AppEventListener()
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
                  new Thread()
                  {
                     public void run()
                     {
                        restoreUserDesktop();
                     }
                  }.start();
               }
            });
         }
      });
      _app.addAppEventListener("LOGOUT", new AppEventListener()
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

   private void setAppIcon()
   {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
//      java.net.URL imgURL = loader.getResource("images/App16.png");
//      if (imgURL == null)
//         imgURL = loader.getResource("images/Objects16.png");
      
      java.net.URL imgURL = loader.getResource("images/App32.png");
      if (imgURL == null)
         imgURL = loader.getResource("images/Objects32.png");

      ImageIcon appIconLg = new ImageIcon(imgURL);
      setIconImage(appIconLg.getImage());
   }

   private void setupMenu()
   {
      JMenu fileMenu = new JMenu("File");
      fileMenu.setMnemonic('f');
      JMenuItem exitItem = new JMenuItem(new QuitAction());
      fileMenu.add(exitItem);

      Component[] topLevelContainers =
         new Component[] {this, _desktopPane.getContextMenu()};
      _lfSupport = new BasicLFSupport(topLevelContainers,
            new BasicLFSupport.SystemLFProvider(), _lfSupport);
      JMenu lookAndFeelMenu = _lfSupport.getMenu();

      _menuBar = new JMenuBar();
      _menuBar.add(fileMenu);
      _menuBar.add(lookAndFeelMenu);
      setJMenuBar(_menuBar);
   }

   /* ** public interface ** */
   public JInternalFrame addLoginDialog(JInternalFrame loginDialog)
   {
      addFrame(loginDialog, Positioning.CENTERED);
      loginDialog.setLayer(JLayeredPane.MODAL_LAYER);
      return loginDialog;
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


   //===
   private void makeClassBar()
   {
//      SimpleListEO classList = _app.getClassList();
      Folder classesFolder = _app.getClassesFolder();
      _classesView = new OutlookFolderView(classesFolder);
//      _classBar = (JToolBar) classList.getToolbarView("Class List");
   }
   private void showClassBar()
   {
      SwingUtilities.invokeLater( new Runnable()
         {
            public void run()
            {
               if (_classesView == null)
               {
                  makeClassBar();
               }
//               _centerPane.add(_classBar, BorderLayout.NORTH);
               _centerPane.add(_classesView, BorderLayout.WEST);
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
//               _centerPane.remove(_classBar);
               _centerPane.remove(_classesView);
               _centerPane.revalidate(); _centerPane.repaint();
            }
         });
   }
   //===
      private static String USERMENUGENERICLABEL = "User";

      private void makeUserMenu()
      {
         _userMenu = new JMenu(USERMENUGENERICLABEL);

         Action logoutAction = new AbstractAction("Logout")
         {
            {
               putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_G));
            }
            public void actionPerformed(ActionEvent evt)
            {
               new Thread()
               {
                  public void run()
                  {
                     _app.onLogout();
                  }
               }.start();
            }
         };

         Action chgPassAction = new AbstractAction("Change Password")
         {
            public void actionPerformed(ActionEvent evt)
            {
               User currentUser = _app.getUser();
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

         JMenuItem item = new JMenuItem(chgPassAction);
         _userMenu.add(item);
         item = new JMenuItem(logoutAction);
         _userMenu.add(item);
      }

      private void showUserMenu()
      {
         User currentUser = _app.getUser();
         _userMenu.setText(currentUser.toString());
         _menuBar.add(_userMenu);
         _menuBar.revalidate(); _menuBar.repaint();
      }

      private void hideUserMenu()
      {
         SwingUtilities.invokeLater( new Runnable()
         {
            public void run()
            {
               _menuBar.remove(_userMenu);
               _menuBar.revalidate(); _menuBar.repaint();
            }
         });
      }



   //===

   class QuitAction extends javax.swing.AbstractAction
   {
      public QuitAction()
      {
         super("Exit");

         putValue(javax.swing.Action.ACTION_COMMAND_KEY, "exit");
         putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_X));

         KeyStroke quitStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Q,
                                                       Platform.mask());
         putValue(javax.swing.Action.ACCELERATOR_KEY, quitStroke);
      }

      public void actionPerformed(ActionEvent evt)
      {
         Cursor WAITCURSOR =
            Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
         SwingViewMechanism.getInstance().setCursor(WAITCURSOR);

         SwingUtilities.invokeLater(new Runnable()
            {
               public void run()
               {
                  quit();
               }
            });
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
      if (_app.getUser() != null) _app.onLogout();
      System.exit(0);
   }


   public void onMessage(String msg) { _msgPnl.message(msg); }

   public void setCursor(Cursor cursor)
   {
      super.setCursor(cursor);
      _desktopPane.setCursor(cursor);
   }


   /*
   * methods related to saving and restoring the user desktop follow..
   */

   private void saveUserDesktop()
   {
      ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
      XMLEncoder enc = new XMLEncoder(baos);
      enc.writeObject(getBounds());  // 1. save main frame bounds
      enc.writeObject(_lfSupport.getCurrentLFName());  // 2. save look and feel
//      enc.writeObject(toolbarLocation());

      // ---

      JInternalFrame[] frames = _desktopPane.getAllFrames();
      java.util.List frameInfo = new java.util.ArrayList();
      for (int i=0; i<frames.length; i++)
      {
         if (!frames[i].isVisible() || frames[i].isIcon()) continue;
         if (frames[i] instanceof EOFrame)
         {
            EObject eo = ((EOFrame) frames[i]).getEObject();
            if (eo instanceof ComplexEObject)
            {
               ComplexEObject ceo = (ComplexEObject) eo;
               
               if (ceo.isTransientState()) continue;
               
               frameInfo.add(new FrameInfo("ceo", ceo.getID(),
                                           ceo.type().getJavaClass().getName(), frames[i].getBounds()));
            }
         }
         else if (frames[i] instanceof CalendarFrame)
         {
            Calendrier calendar = (Calendrier) ((EView) frames[i]).getEObject();
            Calendarable calable = calendar.calendarable();
            frameInfo.add(new FrameInfo("Calendar", calable.getID(),
                                        calable.type().getJavaClass().getName(), frames[i].getBounds()));
         }
         else if (frames[i] instanceof ListEOFrame)
         {
            EObject eo = ((ListEOFrame) frames[i]).getEObject();
            AbstractListEO leo = (AbstractListEO) eo;
            frameInfo.add(
              new FrameInfo("List", new Long(0), leo.type().getJavaClass().getName(),
                            frames[i].getBounds())
                    );
         }
         else if (frames[i] instanceof GenericFrame)
         {
            View view = ((GenericFrame) frames[i]).getView();
            if (view instanceof FindView)
            {
               ComplexType type = ((FindView) view).getType();
               frameInfo.add(new FrameInfo("Find", new Long(0),
                                           type.getJavaClass().getName(), frames[i].getBounds()));
            }
         }
      }
      enc.writeObject(new Integer(frameInfo.size()));
      java.util.Iterator itr = frameInfo.iterator();
      while (itr.hasNext())
      {
         FrameInfo finfo = (FrameInfo) itr.next();
         enc.writeObject(finfo.type);
         enc.writeObject(finfo.id);
         enc.writeObject(finfo.classname);
         enc.writeObject(finfo.bounds);
      }

      // ---

      enc.close();
      User currentUser = _app.getUser();
      currentUser.getDesktop().setValue(baos.toString());
      currentUser.save();

   }

   class FrameInfo
   {
      String type;
      Long id;  String classname;  Rectangle bounds;
      EObject _eo;

      FrameInfo(String type, Long id, String classname, Rectangle bounds)
      {
         this.type = type; this.id = id;
         this.classname = classname; this.bounds = bounds;
      }
      FrameInfo(String type, Long id, String classname, Rectangle bounds, boolean resolveNow)
      {
         this(type, id, classname, bounds);
         if (resolveNow)
         {
            try
            {
               PersistenceMechanism pmech = _app.getPersistenceMechanism();
               if ("Find".equals(type))
               {
                  _eo = ComplexType.forClass(ceoClass());
               }
               else if ("Calendar".equals(type))
               {
                  // needs to be fixed.. (disable for now)
                  ComplexEObject ceo = pmech.load(ceoClass(), id);
                  _eo = ((Calendarable) ceo).calendar();
               }
               else if ("List".equals(type))
               {
                  _eo = pmech.browse(ceoClass());
               }
               else if ("ceo".equals(type))
               {
                  _eo = pmech.load(ceoClass(), id);
               }
            }
            catch (ClassNotFoundException ex) {}
         }
      }
      EObject getEO() { return _eo; }
      Class ceoClass() throws ClassNotFoundException
      {
         return Class.forName(classname);
      }
   }

   private void restoreUserDesktop()
   {
      String desktop = _app.getUser().getDesktop().stringValue();
      if (desktop == null || desktop.length() <= 0) return;  // nothing saved
      XMLDecoder dec = new XMLDecoder(new ByteArrayInputStream(desktop.getBytes()));
      final Rectangle bounds = (Rectangle) dec.readObject();
      final String userLF = (String) dec.readObject();
//      final String toolbarLocation = (String) dec.readObject();

      int numframes = ((Integer) dec.readObject()).intValue();
      final java.util.List finfos = new java.util.ArrayList();
      for (int i=0; i<numframes; i++)
      {
         FrameInfo finfo = new FrameInfo((String) dec.readObject(),
                                         (Long) dec.readObject(), (String) dec.readObject(),
                                         (Rectangle) dec.readObject(), true);
         finfos.add(finfo);
      }

      dec.close();
      SwingUtilities.invokeLater( new Runnable()
            {
               public void run()
               {
                  if (bounds != null) setBounds(bounds);
                  if (userLF != null) _lfSupport.setLF(userLF);
//                  if (toolbarLocation != null) setToolbarLocation(toolbarLocation);

                  FrameInfo finfo = null;
                  for (int i=0; i<finfos.size(); i++)
                  {
                     finfo = (FrameInfo) finfos.get(i);
                     JInternalFrame f = null;
                     EObject eo = finfo.getEO();
                     if ("Find".equals(finfo.type))
                     {
                        f = new GenericFrame(new FindView((ComplexType) eo));
                     }
                     else if ("Calendar".equals(finfo.type))
                     {
                        f = new CalendarFrame(eo.getMainView());
                     }
                     else if ("List".equals(finfo.type))
                     {
                        f = new ListEOFrame(eo.getMainView());
                     }
                     else if ("ceo".equals(finfo.type))
                     {
                        f = new EOFrame(eo.getMainView());
                     }
                     addFrame(f, Positioning.NONE);
                     f.setBounds(finfo.bounds);
                  }

               }
            });
   }

//   private String toolbarLocation()
//   {
//      Rectangle window = getBounds();
//      Point point = _classBar.getLocation();
//      if (_classBar.getOrientation() == SwingConstants.VERTICAL)
//      {
//         int distleft = Math.abs(window.x - point.x);
//         int distright = Math.abs(window.x + window.width - point.x);
//         return (distleft > distright) ? BorderLayout.EAST : BorderLayout.WEST;
//      }
//      else
//      {
//         int disttop = Math.abs(window.y - point.y);
//         int distbottom = Math.abs(window.y + window.height - point.y);
//         return (disttop > distbottom) ? BorderLayout.SOUTH : BorderLayout.NORTH;
//      }
//   }
//   private void setToolbarLocation(String constraints)
//   {
//      _centerPane.remove(_classBar);
//      _centerPane.add(_classBar, constraints);
//      // braindead jtoolbar..
//      if (BorderLayout.NORTH.equals(constraints) || BorderLayout.SOUTH.equals(constraints))
//      {
//         _classBar.setOrientation(SwingConstants.HORIZONTAL);
//      }
//      else
//      {
//         _classBar.setOrientation(SwingConstants.VERTICAL);
//      }
//      _centerPane.revalidate(); _centerPane.repaint();
//   }

}
