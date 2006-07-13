/*
 * Created on Jan 26, 2004
 */
package com.u2d.view.swing;

import com.u2d.app.*;
import com.u2d.calendar.*;
import com.u2d.element.CommandInfo;
import com.u2d.element.EOCommand;
import com.u2d.element.Command;
import com.u2d.field.Association;
import com.u2d.find.CompositeQuery;
import com.u2d.ui.MsgDialog;
import com.u2d.ui.desktop.CloseableJInternalFrame;
import com.u2d.ui.desktop.Positioning;
import com.u2d.view.*;
import com.u2d.view.swing.atom.*;
import com.u2d.view.swing.calendar.*;
import com.u2d.view.swing.find.FindView;
import com.u2d.view.swing.find.FindView2;
import com.u2d.view.swing.find.QueryView;
import com.u2d.view.swing.list.*;
import com.u2d.wizard.ui.WizardPane;
import com.u2d.wizard.details.Wizard;
import com.u2d.type.AbstractChoiceEO;
import com.u2d.type.composite.Folder;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import javax.swing.*;
import com.u2d.list.RelationalList;
import com.u2d.list.CompositeList;
import com.u2d.model.*;
import com.u2d.reporting.*;
//import org.apache.commons.pool.impl.GenericKeyedObjectPool;
//import org.apache.commons.pool.impl.GenericKeyedObjectPoolFactory;
//import org.apache.commons.pool.*;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Eitan Suez
 */
public class SwingViewMechanism implements ViewMechanism
{
   private AppFrame _appFrame;
   private LoginDialog _loginDialog;
   private Application _app;
   private ReportingInterface _reportingInterface;
   
//   private KeyedObjectPool _pool;
   
   private static SwingViewMechanism _instance = null;
   
   public static SwingViewMechanism getInstance()
   {
      if (_instance == null)
         _instance = new SwingViewMechanism();
      return _instance;
   }

   private SwingViewMechanism()
   {
      setupAntiAliasing();
//      setupViewPool();
   }
   
   public static void setupAntiAliasing()
   {
      String antialising = "swing.aatext";
      if (null == System.getProperty(antialising))
         System.setProperty(antialising, "true");
   }

   
   private ReportingInterface reportingInterface()
   {
      if (_reportingInterface == null)
         _reportingInterface = new ReportingInterface();
      return _reportingInterface;
   }
   
   public void initReporting() { reportingInterface(); }

   /*
   private void setupViewPool()
   {
      GenericKeyedObjectPool.Config config = 
            new GenericKeyedObjectPool.Config();

      config.maxActive = 100;
      config.maxIdle = -1;
      //config.whenExhaustedAction = GenericKeyedObjectPool.WHEN_EXHAUSTED_FAIL;
      config.whenExhaustedAction = GenericKeyedObjectPool.WHEN_EXHAUSTED_GROW;
      
      KeyedPoolableObjectFactory poolRendererFactory = 
            new BaseKeyedPoolableObjectFactory()
      {
         public Object makeObject(Object key) throws Exception
         {
            Class cls = (Class) key;
            return cls.newInstance();
         }

         public void passivateObject(Object key, Object obj)
               throws Exception
         {
            if (obj instanceof AtomicRenderer)
            {
               ((AtomicRenderer) obj).passivate();
            }
         }
      };
      
      
      KeyedObjectPoolFactory poolFactory = 
            new GenericKeyedObjectPoolFactory(poolRendererFactory, config);
      _pool = poolFactory.createPool();
   }
   */

   private Object borrowObject(Object key)
   {
      try
      {
         return ((Class) key).newInstance();
      }
      catch (InstantiationException ex) {}
      catch (IllegalAccessException ex2) {}
      return null;
//      try
//      {
//         Object obj = _pool.borrowObject(key);
//         Class cls = ((Class) key);
//         tracePoolInfo(ExpandableView.class,  cls, false);
//         return obj;
//      }
//      catch (Exception ex)
//      {
//         throw new RuntimeException("Failed to borrow view object from view pool", ex);
//      }
   }
   public void returnObject(Object object)
   {
      /*
      try
      {
         _pool.returnObject(object.getClass(), object);
         Class cls = object.getClass();
         tracePoolInfo(ExpandableView.class,  cls, true);
      }
      catch (Exception ex)
      {
         throw new RuntimeException("Failed to return view object to view pool", ex);
      }
      */
   }
   
   /*
   private void tracePoolInfo(Class targetCls, Class cls, boolean returning)
   {
      if (targetCls.equals(cls))
      {
         int size = _pool.getNumActive(targetCls);
         String returningStr = (returning) ? "returning" : "borrowing";
         Tracing.tracer().fine(returningStr + " an object (type is: " 
               + cls.getName() + ")" + " (" + size + ")" );
      }
   }
   */

   public void launch()
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            _app = AppFactory.getInstance().getApp();
            _appFrame = new AppFrame(_app);
            _appFrame.setVisible(true);
         }
      });
   }
   
   public void showLogin()
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            if (_loginDialog == null)
            {
               _loginDialog = new LoginDialog(_app);
               _appFrame.addLoginDialog(_loginDialog);
            }

            _appFrame.centerFrame(_loginDialog);
            _loginDialog.clear();
         }
      });
   }
   public void dismissLogin() { _loginDialog.setVisible(false); }
   public void loginInvalid() { _loginDialog.loginInvalid(); }
   public void userLocked() { _loginDialog.userLocked(); }


   public void displayViewFor(Object value, EView source, Positioning positioningHint)
   {
      if (value == null) return;

      if (value instanceof Viewable)
      {
         EView view = ((Viewable) value).getMainView();

         if (value instanceof ComplexEObject)
         {
            ComplexEObject ceo = (ComplexEObject) value;
            if (ceo.isEditableState() && view instanceof Editor)
            {
               ceo.setEditor((Editor) view);
            }
         }

         displayView(view);
      }
      else if (value instanceof EView)
      {
         displayView((EView) value);
      }
      else if (value instanceof View)
      {
         View view = (View) value;
         displayView(view, positioningHint);
      }
      else if (value instanceof String)
      {
         showMsgDlg((String) value, source);
      }
      else if (value instanceof Reportable)
      {
         displayReport((Reportable) value);
      }
      else if (value instanceof Wizard)
      {
         displayWizard((Wizard) value);
      }
   }



   public void displayView(final EView view)
   {
      SwingUtilities.invokeLater(new Runnable()
            {
               public void run()
               {
                  _appFrame.addFrame(frameFor(view));
               }
            });
   }
   
   private JInternalFrame frameFor(EView view)
   {
      if (view instanceof JInternalFrame)
         return (JInternalFrame) view;
      if (view instanceof ListEView)
         return new ListEOFrame((ListEView) view);
      else if (view instanceof CalendarView)
         return new CalendarFrame((CalendarView) view);
      else if (view instanceof ScheduleView)
         return new CalendarFrame((ScheduleView) view);
      else if (view instanceof ComplexEView)
         return new EOFrame((ComplexEView) view);

      throw new IllegalArgumentException(
            "Don't know how to make a frame for view: "+view);
   }
   
   public void displayView(final View view, final Positioning positioning)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            if (view instanceof JInternalFrame)
            {
               _appFrame.addFrame((JInternalFrame) view, positioning);
            }
            else
            {
               GenericFrame frame = new GenericFrame(view);
               _appFrame.addFrame(frame, positioning);
            }
         }
      });
   }

   public void displayWizard(final Wizard wizard)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            JInternalFrame frame = new CloseableJInternalFrame(wizard.title(),
                                     true, true, true, true);
            WizardPane wizPane = new WizardPane(wizard);
            frame.getContentPane().add(wizPane);
            frame.setTitle(wizard.compositeTitle());
            frame.pack();
            _appFrame.addFrame(frame);
         }
      });
   }


   public void dismiss(final EView view)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            CloseableJInternalFrame.close((JComponent) view);
         }
      });
   }
   
   public void displayReport(Reportable reportable)
   {
      reportingInterface().displayReport(reportable);
   }


   public void showMsgDlg(String msg)
   {
      MsgDialog.showMsgDlg(_appFrame, msg, "");
   }
   public void showMsgDlg(String msg, EView source)
   {
      JComponent component = (JComponent) source;
      MsgDialog.showMsgDlg(component, msg, "");
   }
   public void showMsgDlg(String msg, View source)
   {
      JComponent component = (JComponent) source;
      MsgDialog.showMsgDlg(component, msg, "");
   }

   public void onMessage(final String msg)
   {
      if (SwingUtilities.isEventDispatchThread())
      {
         if (_appFrame != null && _appFrame.isVisible())
            _appFrame.onMessage(msg);
      }
      else
      {
         SwingUtilities.invokeLater(new Runnable() {
            public void run()
            {
               if (_appFrame != null && _appFrame.isVisible())
                  _appFrame.onMessage(msg);
            } });
      }
   }

   public void setCursor(Cursor cursor)
   {
      _appFrame.setCursor(cursor);
   }
   
   /* package private */ void displayFrame(final JInternalFrame frame)
   {
      displayFrame(frame, com.u2d.ui.desktop.Positioning.TOTHERIGHT);
   }
   /* package private */ void displayFrame(final JInternalFrame frame, 
                                           final Positioning positioning)
   {
      SwingUtilities.invokeLater(new Runnable()
            {
               public void run()
               {
                  _appFrame.addFrame(frame, positioning);
               }
            });
   }
   
   // ================================
   // ComplexEObject Views:
   // ================================
   
   public synchronized ComplexEView getIconView(ComplexEObject ceo)
   {
      checkState(ceo);
      IconView view = (IconView) borrowObject(IconView.class);
      view.bind(ceo);
      return view;
   }
   public ComplexEView getCollapsedView(ComplexEObject ceo)
   {
      checkState(ceo);
      return new NullView(ceo);
   }
   public ComplexEView getIconViewAdapter(ComplexEObject ceo)
   {
      checkState(ceo);
      IconViewAdapter view = (IconViewAdapter) 
            borrowObject(IconViewAdapter.class);
      view.bind(ceo);
      return view;
   }
   public ComplexEView getListItemView(ComplexEObject ceo)
   {
      checkState(ceo);
      
      ListItemView view = (ListItemView) borrowObject(ListItemView.class);
      view.bind(ceo);
      return view;
   }
   private void checkState(final ComplexEObject ceo)
   {
      // it's time to bring out the aop..[tbd]
      if (ceo.isNullState())
      {
         Thread t = new Thread()
         {
            public void run()
            {
               ceo.restoreState();
            }
         };
         t.start();
         try  // must wait for state update before producing the
               // view so the right commands show up in the ui
         {
            t.join();
         }
         catch (InterruptedException e)
         {
            e.printStackTrace();
         }
      }
   }
   public ComplexEView getListItemViewAdapter(ComplexEObject ceo)
   {
      checkState(ceo);
      
      ListItemViewAdapter view = (ListItemViewAdapter)
            borrowObject(ListItemViewAdapter.class);
      view.bind(ceo);
      return view;
   }
   public ComplexEView getExpandableView(ComplexEObject ceo)
   {
      checkState(ceo);

      ExpandableView view = (ExpandableView)
            borrowObject(ExpandableView.class);
      view.bind(ceo);
      return view;
   }
   public ComplexEView getExpandableView(ComplexEObject ceo,
                                         boolean expanded)
   {
      checkState(ceo);

      ExpandableView view = (ExpandableView)
            borrowObject(ExpandableView.class);
      view.bind(ceo, expanded);
      return view;
   }
   public ComplexEView getFormView(ComplexEObject ceo)
   {
      checkState(ceo);
      return new TopLevelFormView(ceo);
   }
   public ComplexEView getTreeView(ComplexEObject ceo)
   {
      checkState(ceo);
      return new JTreeView(ceo);
   }
   public ComplexEView getOmniView(ComplexEObject ceo)
   {
      checkState(ceo);
      return new OmniView(ceo);
   }
   public ComplexEView getTabBodyView(ComplexEObject ceo)
   {
      checkState(ceo);
      return new TabBodyView(ceo);
   }

   
   public ComplexEView getAssociationView(Association association)
   {
      return new AssociationView2(association);
   }
   /* the understanding here is that choices are also CEOs */
   public ComplexEView getChoiceView(AbstractChoiceEO choice)
   {
      return new ChoiceView(choice);
   }
   
   
   public ComplexEView getCalendarView(Calendrier calendar)
   {
      return new CalendarView(calendar);
   }
   public ComplexEView getScheduleView(Schedule schedule)
   {
      return new ScheduleView(schedule);
   }
   public ComplexEView getCalEventView(CalEvent event)
   {
      return new CalEventView(event);
   }
   public ComplexEView getCalEventView(CalEvent event, Schedule schedule)
   {
      return new CalEventView(event, schedule);
   }


   // ================================
   // AtomicEObject Views:
   // ================================

   public AtomicEView getAtomicView(AtomicEObject eo)
   {
      AtomicView view = (AtomicView) borrowObject(AtomicView.class);
      view.bind(eo);
      return view;
   }

   public AtomicRenderer getStringRenderer()
   {
      return (AtomicRenderer) borrowObject(StringRenderer.class);
   }

   public AtomicEditor getStringEditor()
   {
      return (AtomicEditor) borrowObject(StringEditor.class);
   }

   public AtomicRenderer getPasswordRenderer()
   {
      return (AtomicRenderer) borrowObject(PasswordRenderer.class);
   }

   public AtomicEditor getPasswordEditor()
   {
      return (AtomicEditor) borrowObject(PasswordEditor.class);
   }

   public AtomicRenderer getBooleanRenderer()
   {
      return (AtomicRenderer) borrowObject(BooleanRenderer.class);
   }

   public AtomicEditor getBooleanEditor()
   {
      return (AtomicEditor) borrowObject(BooleanRadioEditor.class);
   }

   public AtomicRenderer getTextRenderer()
   {
      TextEditor editor = (TextEditor) borrowObject(TextEditor.class);
      editor.setEditable(false);
      return editor;
   }

   public AtomicEditor getTextEditor()
   {
      TextEditor editor = (TextEditor) borrowObject(TextEditor.class);
      editor.setEditable(true);
      return editor;
   }

   public AtomicRenderer getCharRenderer()
   {
      return (AtomicRenderer) borrowObject(StringRenderer.class);
   }

   public AtomicEditor getCharEditor()
   {
      return (AtomicEditor) borrowObject(CharEditor.class);
   }

   public AtomicRenderer getIntRenderer()
   {
//      return (AtomicRenderer) borrowObject(StringRenderer.class);
      IntEditor editor = (IntEditor) borrowObject(IntEditor.class);
      editor.setEditable(false);
      return editor;
   }

   public AtomicEditor getIntEditor()
   {
      return (AtomicEditor) borrowObject(IntEditor.class);
   }

   public AtomicRenderer getLongRenderer()
   {
      return (AtomicRenderer) borrowObject(StringRenderer.class);
   }

   public AtomicEditor getLongEditor()
   {
      return (AtomicEditor) borrowObject(LongEditor.class);
   }

   public AtomicRenderer getFloatRenderer()
   {
//      return (AtomicRenderer) borrowObject(StringRenderer.class);
      FloatEditor editor = (FloatEditor) borrowObject(FloatEditor.class);
      editor.setEditable(false);
      return editor;
   }

   public AtomicEditor getFloatEditor()
   {
      return (AtomicEditor) borrowObject(FloatEditor.class);
   }

   public AtomicRenderer getPercentRenderer()
   {
      return (AtomicRenderer) borrowObject(StringRenderer.class);
   }

   public AtomicEditor getPercentEditor()
   {
      return (AtomicEditor) borrowObject(PercentEditor.class);
   }

   public AtomicRenderer getEmailRenderer()
   {
      return (AtomicRenderer) borrowObject(StringRenderer.class);
   }

   public AtomicEditor getEmailEditor()
   {
      return (AtomicEditor) borrowObject(EmailEditor.class);
   }

   public AtomicRenderer getURIRenderer()
   {
      return (AtomicRenderer) borrowObject(URIRenderer.class);
   }

   public AtomicEditor getURIEditor()
   {
      return (AtomicEditor) borrowObject(URIEditor.class);
   }

   public AtomicRenderer getUSDollarRenderer()
   {
      return (AtomicRenderer) borrowObject(StringRenderer.class);
   }

   public AtomicEditor getUSDollarEditor()
   {
      return (AtomicEditor) borrowObject(USDollarEditor.class);
   }

   public AtomicRenderer getUSZipRenderer()
   {
      return (AtomicRenderer) borrowObject(StringRenderer.class);
   }

   public AtomicEditor getUSZipEditor()
   {
      return (AtomicEditor) borrowObject(USZipEditor.class);
   }

   public AtomicRenderer getUSPhoneRenderer()
   {
      return (AtomicRenderer) borrowObject(StringRenderer.class);
   }

   public AtomicEditor getUSPhoneEditor()
   {
      return (AtomicEditor) borrowObject(USPhoneEditor.class);
   }

   public AtomicRenderer getSSNRenderer()
   {
      return (AtomicRenderer) borrowObject(StringRenderer.class);
   }

   public AtomicEditor getSSNEditor()
   {
      return (AtomicEditor) borrowObject(SSNEditor.class);
   }

   public AtomicRenderer getDateRenderer()
   {
      return (AtomicRenderer) borrowObject(DateRenderer.class);
   }

   public AtomicEditor getDateEditor()
   {
      return (AtomicEditor) borrowObject(DateEditor.class);
   }

   public AtomicRenderer getDateWithAgeRenderer()
   {
      return (AtomicRenderer) borrowObject(DateWithAgeRenderer.class);
   }

   public AtomicEditor getDateWithAgeEditor()
   {
      return (AtomicEditor) borrowObject(DateWithAgeEditor.class);
   }

   public AtomicRenderer getDateTimeRenderer()
   {
      return (AtomicRenderer) borrowObject(StringRenderer.class);
   }

   public AtomicEditor getDateTimeEditor()
   {
      return (AtomicEditor) borrowObject(DateTimeEditor.class);
   }

   public AtomicRenderer getTimeRenderer()
   {
      return (AtomicRenderer) borrowObject(StringRenderer.class);
   }

   public AtomicEditor getTimeEditor()
   {
      return (AtomicEditor) borrowObject(TimeSpinnerEditor.class);
   }

   public AtomicRenderer getTimeSpanRenderer()
   {
      return (AtomicRenderer) borrowObject(StringRenderer.class);
   }

   public AtomicEditor getTimeSpanEditor()
   {
      return (AtomicEditor) borrowObject(TimeSpanEditor.class);
   }

   public AtomicRenderer getChoiceEORenderer()
   {
      return (AtomicRenderer) borrowObject(StringRenderer.class);
   }

   public AtomicEditor getChoiceEOEditor()
   {
      return (AtomicEditor) borrowObject(ChoiceEOEditor.class);
   }

   public AtomicRenderer getTermsRenderer()
   {
      return (AtomicRenderer) borrowObject(BooleanRenderer.class);
   }

   public AtomicEditor getTermsEditor()
   {
      return (AtomicEditor) borrowObject(TermsEditor.class);
   }

   public AtomicRenderer getImageRenderer()
   {
      return (AtomicRenderer) borrowObject(ImagePicker.class);
   }

   public AtomicEditor getImageEditor()
   {
      ImagePicker imgPicker = 
            (ImagePicker) borrowObject(ImagePicker.class);
      imgPicker.putInEditMode();
      return imgPicker;
   }

   public AtomicRenderer getFileRenderer()
   {
      return new FilePicker();
   }
   public AtomicEditor getFileEditor()
   {
      return new FilePicker().putInEditMode();
   }

   // ================================
   // SimpleListEO Views:
   // ================================

   public ListEView getListView(AbstractListEO leo)
   {
      return new JListView(leo);
   }

   public ListEView getListViewAsTable(AbstractListEO leo) { return new TableView(leo); }
   public ListEView getListViewAsIcons(AbstractListEO leo) { return new IconListView(leo); }
   public ListEView getListViewAsTree(AbstractListEO leo) { return new MyListTreeView(leo); }
   public ListEView getOmniListView(AbstractListEO leo) { return new OmniListView(leo); }
   public ListEView getToolbarView(String name, AbstractListEO leo) { return new ToolbarView(name, leo); }

   public ListEView getPickView(AbstractListEO leo)
   {
      return new PickView(leo);
   }
   public View getMultiPickView(AbstractListEO leo)
   {
      return new MultiPickView(leo);
   }

   public ListEView getRelationalListView(RelationalList leo) { return new RelationalListView(leo); }
   public ListEView getListViewMinimized(AbstractListEO leo)
   {
      return new com.u2d.view.swing.list.ListItemView(leo);
   }
   public ListEView getExpandableListView(RelationalList leo)
   {
      return new com.u2d.view.swing.list.ExpandableView(leo);
   }

   public ListEView getMultiChoiceView(AbstractListEO leo)
   {
      return new MultiChoiceView(leo);
   }

   public ListEView getPaginableView(ListEView listeview)
   {
      return new PaginableView(listeview);
   }
   public ListEView getEditableListView(AbstractListEO leo)
   {
      if (leo instanceof CompositeList)
         return new CompositeTableView((CompositeList) leo);
      else if (leo instanceof RelationalList)
         return new EditableListView((RelationalList) leo);
      else
         throw new RuntimeException("getEditableListView at the moment works only in the context" +
               " of a compositelist or a relationallist");
   }

   
   // ================================
   // Custom Views:
   // ================================
   
   
   public View getParamListView(EOCommand cmd, Object value, CommandInfo cmdInfo)
   {
      return new ParamListView(cmd, value, cmdInfo);
   }
   public View getFindView(ComplexType type)
   {
      return new FindView(type);
   }
   public View getFindView2(ComplexType type)
   {
      return new FindView2(type);
   }

   
   public ComplexEView getFolderView(ComplexEObject ceo)
   {
      if (! (ceo instanceof Folder))
         throw new IllegalArgumentException("Object must be a folder type");
      
      Folder folder = (Folder) ceo;
      return new FolderView(folder);
   }
   
   public ComplexEView getOutlookView(ComplexEObject ceo)
   {
      if (! (ceo instanceof Folder))
         throw new IllegalArgumentException("Object must be a folder type");
      
      Folder folder = (Folder) ceo;
      return new OutlookFolderView(folder);
   }
   
   public ComplexEView getQueryView(CompositeQuery query)
   {
      return new QueryView(query);
   }
   
   
   public ListEView getAlternateListView(AbstractListEO leo, String[] viewNames)
   {
      return new AlternateListView(leo, viewNames);
   }
   
   public ComplexEView getAlternateView(ComplexEObject ceo, String[] viewNames)
   {
      return new AlternateView(ceo, viewNames);
   }


   // refactored code:
   
   public static void setupEnterKeyBinding(final Selectable component)
   {
      ((JComponent) component).addKeyListener(new KeyListener()
         {
            public void keyTyped(KeyEvent evt)
            {
               if (evt.getKeyChar() == '\r' || evt.getKeyChar() == '\n')
               {
                  evt.consume();
                  ComplexEObject selected = component.selectedEO();
                  if (selected == null)
                  {
                     SwingViewMechanism.getInstance().onMessage("No items selected");
                     return;
                  }
                  try
                  {
                     Command defaultCmd = selected.command(selected.defaultCommandName());
                     defaultCmd.execute(selected, null);
                  }
                  catch (InvocationTargetException ex)
                  {
                     System.err.println("strange..");
                     System.err.println("InvocationTargetException: "+ex.getMessage());
                     ex.printStackTrace();
                  }
               }
            }
            // done specifically to suppress invocation of frame's default action
            public void keyPressed(KeyEvent evt)
            {
               if (evt.getKeyChar() == '\r' || evt.getKeyChar() == '\n')
               {
                  evt.consume();
               }
            }

            public void keyReleased(KeyEvent evt)
            {
               if (evt.getKeyChar() == '\r' || evt.getKeyChar() == '\n')
               {
                  evt.consume();
               }
            }
         });
   }

   public void setEditable(final Editor editor, final boolean editable)
   {
      if (SwingUtilities.isEventDispatchThread())
      {
         editor.setEditable(editable);
      }
      else
      {
         SwingUtilities.invokeLater(new Runnable() {
            public void run()
            {
               editor.setEditable(editable);
            }
         });
      }
   }

}
