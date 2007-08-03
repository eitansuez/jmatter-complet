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
import com.u2d.ui.desktop.CloseableJInternalFrame;
import com.u2d.ui.desktop.Positioning;
import com.u2d.view.*;
import com.u2d.view.swing.atom.*;
import com.u2d.view.swing.calendar.fancy.CalEventView;
import com.u2d.view.swing.calendar.fancy.CalendarView;
import com.u2d.view.swing.calendar.fancy.ScheduleView;
import com.u2d.view.swing.calendar.fancy.CalendarFrame;
import com.u2d.view.swing.calendar.simple.CalendarListView;
import com.u2d.view.swing.find.FindView;
import com.u2d.view.swing.find.FindView2;
import com.u2d.view.swing.find.QueryView;
import com.u2d.view.swing.list.*;
import com.u2d.view.swing.restrict.TypeRestrictionMgrUi;
import com.u2d.view.swing.restrict.RoleTypeRestrictionMgrUi;
import com.u2d.wizard.ui.WizardPane;
import com.u2d.wizard.details.Wizard;
import com.u2d.type.*;
import com.u2d.type.composite.Folder;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.u2d.list.RelationalList;
import com.u2d.list.CompositeList;
import com.u2d.model.*;
import com.u2d.reporting.*;
import com.u2d.css4swing.CSSEngine;
import java.lang.reflect.InvocationTargetException;

//import spin.over.CheckingRepaintManager;

/**
 * @author Eitan Suez
 */
public class SwingViewMechanism implements ViewMechanism
{
   private AppFrame _appFrame;
   private LoginDialog _loginDialog;
   private ReportingInterface _reportingInterface;

   private AppSession _appSession;
   private String _lfname;

   public SwingViewMechanism()
   {
      setupAntiAliasing();
      // Checks for EDT violations..
//      RepaintManager.setCurrentManager(new CheckingRepaintManager());
      CSSEngine.initialize();
      Toolkit.getDefaultToolkit().addAWTEventListener(_inputTracker, AWTEvent.MOUSE_EVENT_MASK);
   }
   
   InputTracker _inputTracker = new InputTracker();
   boolean _isShiftDown = false;
   
   class InputTracker implements AWTEventListener
   {
      public void eventDispatched(AWTEvent event)
      {
         if ((event.getID() == MouseEvent.MOUSE_PRESSED || event.getID() == KeyEvent.KEY_PRESSED))
         {
            _isShiftDown = ((InputEvent) event).isShiftDown();
         }
      }
   }

   public static void setupAntiAliasing()
   {
      String antialising = "swing.aatext";
      if (null == System.getProperty(antialising))
         System.setProperty(antialising, "true");
   }
   
   public void setAppSession(AppSession appSession) { _appSession = appSession; }

   public String getLfname() { return _lfname; }
   public void setLfname(String lfname) { _lfname = lfname; }
   
   private boolean labelEditorLayoutHorizontal = true;
   public void setLabelEditorLayoutHorizontal(boolean value)
   {
      labelEditorLayoutHorizontal = value;
   }
   public boolean isLabelEditorLayoutHorizontal() { return labelEditorLayoutHorizontal; }

   
   private ReportingInterface reportingInterface()
   {
      if (_reportingInterface == null)
         _reportingInterface = new ReportingInterface();
      return _reportingInterface;
   }
   public void initReporting() { reportingInterface(); }
   
   
   public void launch()
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            _appFrame = new AppFrame(_appSession, _lfname);
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
               _loginDialog = new LoginDialog(_appSession);
               _appFrame.addLoginDialog(_loginDialog);
            }

            _appFrame.centerFrame(_loginDialog);
            _loginDialog.clear();
         }
      });
   }

   public void dismissLogin()
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            _loginDialog.setVisible(false);
         }
      });
   }

   public void loginInvalid()
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            _loginDialog.loginInvalid();
         }
      });
   }

   public void userLocked()
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            _loginDialog.userLocked();
         }
      });
   }


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

         displayView(view, source);
      }
      else if (value instanceof EView)
      {
         displayView((EView) value, source);
      }
      else if (value instanceof View)
      {
         View view = (View) value;
         displayView(view, positioningHint);
      }
      else if (value instanceof String)
      {
         message((String) value);
      }
      else if (value instanceof Reportable)
      {
         displayReport((Reportable) value);
      }
      else if (value instanceof Wizard)
      {
         displayWizard((Wizard) value);
      }
      else if (value instanceof JComponent)
      {
         JComponent component = (JComponent) value;
         displayView(component, positioningHint);
      }
   }



   public void displayView(final EView view, final EView source)
   {
      SwingUtilities.invokeLater(new Runnable()
            {
               public void run()
               {
                  if (source == null)
                  {
                     _appFrame.addFrame(frameFor(view));
                     return;
                  }
                  
                  JComponent srcComp = (JComponent) source;
                  Container container = SwingUtilities.getAncestorOfClass(FlexiFrame.class, srcComp);
                  // since java 6, the above getAncestorOfClass call returns null
                  // this has to do with the discontinuity in contaiment hierarchy that components
                  // such as JList create.  here's a workaround:
                  if (container == null)
                  {
                     JComponent focusOwner = (JComponent)
                           KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                     if (focusOwner instanceof JList)
                     {
                        container = SwingUtilities.getAncestorOfClass(FlexiFrame.class, focusOwner);
                     }
                  }
                  final FlexiFrame existingFrame = (FlexiFrame) container;

                  User currentUser = Context.getInstance().getAppSession().getUser();
                  ViewOpenChoice choice = currentUser.getPreferences().getOpenNewViews();
                  
                  /* pseudo code..
                   check if shift is down on last mouse event..
                   if so..
                   popup a contextmenu view displaying each of the choices
                   on choice selection..
                   */
                  if (_isShiftDown)
                  {
                     JPopupMenu menu = new JPopupMenu();
                     ActionListener listener = new ActionListener()
                     {
                        public void actionPerformed(ActionEvent e)
                        {
                           String choice = e.getActionCommand();
                           SwingViewMechanism.this.displayView(view, existingFrame, choice);
                        }
                     };
                     addDisplayChoiceItem(menu, ViewOpenChoice.IN_NEWWINDOW, listener);
                     addDisplayChoiceItem(menu, ViewOpenChoice.IN_NEWTAB, listener);
                     addDisplayChoiceItem(menu, ViewOpenChoice.IN_PLACE, listener);
                     _appFrame.popup(menu);
                  }
                  else
                  {
                     SwingViewMechanism.this.displayView(view, existingFrame, choice.code());
                  }
               }
      });
   }

   private void addDisplayChoiceItem(JPopupMenu menu, String optionText, ActionListener listener)
   {
      JMenuItem item = new JMenuItem(optionText);
      item.setActionCommand(optionText);
      item.addActionListener(listener);
      menu.add(item);
   }
   
   private void displayView(EView view, FlexiFrame targetFrame, String choice)
   {
      if (ViewOpenChoice.IN_NEWWINDOW.equals(choice) || targetFrame == null)
      {
         _appFrame.addFrame(frameFor(view));
      }
      else if (ViewOpenChoice.IN_NEWTAB.equals(choice))
      {
         targetFrame.addView(wrapView(view));
      }
      else if (ViewOpenChoice.IN_PLACE.equals(choice))
      {
         targetFrame.replaceView(wrapView(view));
      }
   }

   private JComponent wrapView(EView view)
   {
      if (view instanceof ListEView)
      {
         return new ListEOPanel(view);
      }
      else if (view instanceof ComplexEView)
      {
         return new EOPanel(view);
      }
      else  // don't wrap it..
      {
         return (JComponent) view;
      }
   }
   
   private JInternalFrame frameFor(EView view)
   {
      if (view instanceof JInternalFrame)
      {
         return (JInternalFrame) view;
      }
      if (view instanceof ListEView)
      {
         return new FlexiFrame(new ListEOPanel((ListEView) view));
      }
      else if (view instanceof CalendarView)
      {
         return new CalendarFrame((CalendarView) view);
      }
      else if (view instanceof ScheduleView)
      {
         return new CalendarFrame((ScheduleView) view);
      }
      else if (view instanceof ComplexEView)
      {
         return new FlexiFrame(new EOPanel((ComplexEView) view));
      }

      throw new IllegalArgumentException(
            "Don't know how to make a frame for view: "+view);
   }
   
   public void displayView(final View view, final Positioning positioning)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            JInternalFrame frame;
            if (view instanceof JInternalFrame)
            {
               frame = (JInternalFrame) view;
            }
            else
            {
               frame = new GenericFrame(view);
            }
            _appFrame.addFrame(frame, positioning);
         }
      });
   }
   
   public void displayView(final JComponent component, final Positioning positioning)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            JInternalFrame frame = new CloseableJInternalFrame("", true, true, true, true);
            frame.setContentPane(component);
            frame.pack();
            _appFrame.addFrame(frame, positioning);
         }
      });
   }

   public void displayWizard(final Wizard wizard)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            JInternalFrame frame = new CloseableJInternalFrame(wizard.compositeTitle(),
                                     true, true, true, true);
            WizardPane wizPane = new WizardPane(wizard);
            frame.setContentPane(wizPane);
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


   public void message(final String msg)
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

   public void setWaitCursor() { _appFrame.setWaitCursor(); }
   public void setDefaultCursor() { _appFrame.setDefaultCursor(); }

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
   
   public ComplexEView getIconView(ComplexEObject ceo)
   {
      checkState(ceo);
      IconView view = new IconView();
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
      IconViewAdapter view = new IconViewAdapter();
      view.bind(ceo);
      return view;
   }
   public ComplexEView getListItemView(ComplexEObject ceo)
   {
      checkState(ceo);
      ListItemView view = new ListItemView();
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
      
      ListItemViewAdapter view = new ListItemViewAdapter();
      view.bind(ceo);
      return view;
   }
   public ComplexEView getExpandableView(ComplexEObject ceo)
   {
      checkState(ceo);

      ExpandableView2 view = new ExpandableView2();
      view.bind(ceo);
      return view;
   }
   public ComplexEView getExpandableView(ComplexEObject ceo,
                                         boolean expanded)
   {
      checkState(ceo);

      ExpandableView2 view = new ExpandableView2();
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

   
   public EView getAggregateView(ComplexEObject value)
   {
      if (value instanceof com.u2d.type.Choice)
      {
         return value.getView();
      }
      else if (value.type().getJavaClass().isAnnotationPresent(EditWithCombo.class))
      {
         return new StateCardPanel(getListItemView(value), new AggregateComboView(value));
      }
      else if (value.field().isTabView())
      {
         return getTabBodyView(value);
      }
      else
      {
         return getExpandableView(value);
      }
   }
   public ComplexEView getAssociationView(Association association)
   {
      if (association.type().getJavaClass().isAnnotationPresent(EditWithCombo.class))
      {
         return new StateCardPanel(new AssociationView2(association), new AssociationComboView(association));
      }
      else
      {
         return new AssociationView2(association);
      }
   }
   /* the understanding here is that choices are also CEOs */
   public ComplexEView getChoiceView(AbstractChoiceEO choice)
   {
      return new ChoiceView(choice);
   }
   
   public EView getTypePicker(ComplexType type) { return new TypePicker(type); }

   
   
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
      return new com.u2d.view.swing.calendar.simple.CalEventView(event);
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
      AtomicView view = new AtomicView();
      view.bind(eo);
      return view;
   }

   public AtomicRenderer getStringRenderer() { return new StringRenderer(); }
   public AtomicEditor getStringEditor() { return new StringEditor(); }

   public AtomicRenderer getPasswordRenderer() { return new PasswordRenderer(); }
   public AtomicEditor getPasswordEditor() { return new PasswordEditor(); }

   public AtomicRenderer getBooleanRenderer() { return new BooleanRenderer(); }
   public AtomicEditor getBooleanEditor() { return new BooleanRadioEditor(); }

   public AtomicRenderer getTextRenderer()
   {
      TextEditor editor = new TextEditor();
      editor.setEditable(false);
      return editor;
   }
   public AtomicEditor getTextEditor()
   {
      TextEditor editor = new TextEditor();
      editor.setEditable(true);
      return editor;
   }

   public AtomicRenderer getCharRenderer() { return new StringRenderer(); }
   public AtomicEditor getCharEditor() { return new CharEditor(); }

   public AtomicRenderer getIntRenderer()
   {
      IntEditor editor = new IntEditor();
      editor.setEditable(false);
      return editor;
   }
   public AtomicEditor getIntEditor() { return new IntEditor(); }
   
   public AtomicRenderer getLongRenderer() { return new StringRenderer(); }
   public AtomicEditor getLongEditor() { return new LongEditor(); } 
   
   public AtomicRenderer getFloatRenderer()
   {
      FloatEditor editor = new FloatEditor();
      editor.setEditable(false);
      return editor;
   }
   public AtomicEditor getFloatEditor() { return new FloatEditor(); }

   public AtomicRenderer getPercentRenderer() { return new StringRenderer(); }
   public AtomicEditor getPercentEditor() { return new PercentEditor(); }

   public AtomicRenderer getEmailRenderer() { return new StringRenderer(); }
   public AtomicEditor getEmailEditor() { return new EmailEditor(); }

   public AtomicRenderer getURIRenderer() { return new URIRenderer(); }
   public AtomicEditor getURIEditor() { return new URIEditor(); }

   public AtomicRenderer getUSDollarRenderer() { return new StringRenderer(); }
   public AtomicEditor getUSDollarEditor() { return new USDollarEditor(); }

   public AtomicRenderer getUSZipRenderer() { return new StringRenderer(); }
   public AtomicEditor getUSZipEditor() { return new USZipEditor(); }

   public AtomicRenderer getUSPhoneRenderer() { return new StringRenderer(); }
   public AtomicEditor getUSPhoneEditor() { return new USPhoneEditor(); }

   public AtomicRenderer getSSNRenderer() { return new StringRenderer(); }
   public AtomicEditor getSSNEditor() { return new SSNEditor(); }

   public AtomicRenderer getColorRenderer() { return new ColorPicker(false); }
   public AtomicEditor getColorEditor() { return new ColorPicker(); }

   public AtomicRenderer getDateRenderer() { return new DateRenderer(); }
   public AtomicEditor getDateEditor() { return new DateEditor(); }

   public AtomicRenderer getDateWithAgeRenderer() { return new DateWithAgeRenderer(); }
   public AtomicEditor getDateWithAgeEditor() { return new DateWithAgeEditor(); }

   public AtomicRenderer getDateTimeRenderer() { return new StringRenderer(); }
   public AtomicEditor getDateTimeEditor() { return new DateTimeEditor(); }

   public AtomicRenderer getTimeRenderer() { return new StringRenderer(); }
   public AtomicEditor getTimeEditor() { return new TimeSpinnerEditor(); }

   public AtomicRenderer getTimeSpanRenderer() { return new StringRenderer(); }
   public AtomicEditor getTimeSpanEditor() { return new TimeSpanEditor(); }

   public AtomicRenderer getChoiceEORenderer() { return new StringRenderer(); }
   public AtomicEditor getChoiceEOEditor() { return new ChoiceEOEditor(); }

   public AtomicRenderer getTermsRenderer() { return new BooleanRenderer(); }
   public AtomicEditor getTermsEditor() { return new TermsEditor(); }

   public AtomicRenderer getImageRenderer() { return new ImagePicker(false); }
   public AtomicEditor getImageEditor() { return new ImagePicker(); }

   public AtomicRenderer getFileRenderer() { return new FilePicker(); }
   public AtomicEditor getFileEditor() { return new FilePicker().putInEditMode(); }

   // ================================
   // SimpleListEO Views:
   // ================================

   public ListEView getListView(AbstractListEO leo) { return new JListView(leo); }
   public ListEView getListViewAsTable(AbstractListEO leo) { return new TableView(leo); }
   public ListEView getListViewAsIcons(AbstractListEO leo) { return new GridListView(leo); }
   public ListEView getListViewAsTree(AbstractListEO leo) { return new MyListTreeView(leo); }

   public ListEView getListViewAsCalendar(CalEventList list)
   {
      return new CalendarListView(list);
   }

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

   public ListEView getRelationalListView(RelationalList leo)
   {
      return new RelationalListView(leo);
   }

   public ListEView getListViewMinimized(AbstractListEO leo)
   {
      return new com.u2d.view.swing.list.ListItemView(leo);
   }
   public ListEView getExpandableListView(RelationalList leo)
   {
      com.u2d.view.swing.list.ExpandableView view = new com.u2d.view.swing.list.ExpandableView(leo);
      if (leo.field().isTabView())
      {
         view.toggle(true);
      }
      return view;
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
   public View getFindView2(ComplexType type, Association association)
   {
      FindView2 findview = new FindView2(type);
      findview.setPickState(association);
      return findview;
   }


   /*
    folder views are sometimes constructed dynamically/reflectivley
    (see viewinfo).  the expected signature for these methods is that
    they access a parameter of type ComplexEObject.
    */
   public ComplexEView getFolderView(ComplexEObject ceo)
   {
      if (! (ceo instanceof Folder))
         throw new IllegalArgumentException("Object must be a folder type");
      
      Folder folder = (Folder) ceo;
      return new FolderView(folder);
   }

   public ComplexEView getOutlookView(ComplexEObject ceo)
   {
      if (!(ceo instanceof Folder))
         throw new IllegalArgumentException("Object must be a folder type");

      Folder folder = (Folder) ceo;
      return new OutlookFolderView(folder);
   }

   public ComplexEView getQueryView(CompositeQuery query) { return new QueryView(query); }


   public EView getTypeRestrictionMgrUi(TypeRestrictionMgr mgr)
   {
      return new TypeRestrictionMgrUi(mgr);
   }
   public EView getRoleTypeRestrictionMgrUi(RoleTypeRestrictionMgr mgr)
   {
      return new RoleTypeRestrictionMgrUi(mgr);
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
                     getInstance().message("No items selected");
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
   
   public static SwingViewMechanism getInstance()
   {
      ViewMechanism vmech = Context.getInstance().getViewMechanism();
      if (! (vmech instanceof SwingViewMechanism) )
      {
         throw new RuntimeException("Application is not configured to run with Swing View Mechanism");
      }
      return (SwingViewMechanism) vmech;
   }
   
   
   // Utility:
   public static void invokeSwingAction(final SwingAction action)
   {
      SwingViewMechanism.getInstance().setWaitCursor();
      new Thread()
      {
        public void run()
        {
           try
           {
              action.offEDT();
           }
           finally
           {
              SwingUtilities.invokeLater(new Runnable()
              {
                 public void run()
                 {
                    action.backOnEDT();
                    SwingViewMechanism.getInstance().setDefaultCursor();
                 }
              });
           }
        }
      }.start();
   }

}
