/*
 * Created on Jan 26, 2004
 */
package com.u2d.view.swing;

import com.u2d.app.*;
import com.u2d.calendar.CalEvent;
import com.u2d.calendar.CalEventList;
import com.u2d.calendar.Calendrier;
import com.u2d.calendar.Schedule;
import com.u2d.css4swing.CSSEngine;
import com.u2d.css4swing.selector.Selector;
import com.u2d.css4swing.style.ComponentStyle;
import com.u2d.element.Command;
import com.u2d.element.CommandInfo;
import com.u2d.element.EOCommand;
import com.u2d.field.Association;
import com.u2d.field.IndexedField;
import com.u2d.find.CompositeQuery;
import com.u2d.list.CompositeList;
import com.u2d.list.RelationalList;
import com.u2d.model.*;
import com.u2d.type.AbstractChoiceEO;
import com.u2d.type.composite.Folder;
import com.u2d.type.composite.USAddress;
import com.u2d.ui.desktop.CloseableJInternalFrame;
import com.u2d.ui.desktop.Positioning;
import com.u2d.ui.UIUtils;
import com.u2d.ui.Caption;
import com.u2d.view.*;
import com.u2d.view.swing.atom.*;
import com.u2d.view.swing.calendar.fancy.CalEventView;
import com.u2d.view.swing.calendar.fancy.CalendarFrame;
import com.u2d.view.swing.calendar.fancy.CalendarView;
import com.u2d.view.swing.calendar.fancy.ScheduleView;
import com.u2d.view.swing.calendar.simple.CalendarListView;
import com.u2d.view.swing.find.FindView;
import com.u2d.view.swing.find.FindView2;
import com.u2d.view.swing.find.QueryView;
import com.u2d.view.swing.list.*;
import com.u2d.view.swing.restrict.RoleTypeRestrictionMgrUi;
import com.u2d.view.swing.restrict.TypeRestrictionMgrUi;
import com.u2d.view.swing.map.AddressViewOnMap;
import com.u2d.view.swing.map.ListMapView;
import com.u2d.view.swing.map.EOMapView;
import com.u2d.wizard.details.Wizard;
import com.u2d.wizard.ui.WizardPane;
import com.u2d.interaction.Instruction;
//import spin.over.CheckingRepaintManager;
import javax.swing.*;
import javax.swing.event.SwingPropertyChangeSupport;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.Set;
import net.miginfocom.swing.MigLayout;

/**
 * @author Eitan Suez
 */
public class SwingViewMechanism implements ViewMechanism
{
   private static SwingViewMechanism vmech = new SwingViewMechanism();
   public static SwingViewMechanism getInstance() { return vmech; }
   
   private AppContainer _appContainer;
   private LoginDialog _loginDialog;

   private AppSession _appSession;

   private SwingViewMechanism()
   {
      CSSEngine.initialize();
      UIManager.getDefaults().addResourceBundle("app/uidefaults");
   }

   InputTracker _inputTracker;
   int _modifiers;
   private boolean isShiftDown() { return (_modifiers & InputEvent.SHIFT_MASK) != 0; }
   public boolean isControlDown() { return (_modifiers & InputEvent.CTRL_MASK) != 0; }
   public boolean isAltDown() { return (_modifiers & InputEvent.ALT_MASK) != 0; }

   class InputTracker implements AWTEventListener
   {
      public void eventDispatched(AWTEvent event)
      {
         if ((event.getID() == MouseEvent.MOUSE_PRESSED || event.getID() == KeyEvent.KEY_PRESSED))
         {
            _modifiers = ((InputEvent) event).getModifiers();
         }
      }
   }
   private void setupInputTracker()
   {
      _inputTracker = new InputTracker();
      Toolkit.getDefaultToolkit().addAWTEventListener(_inputTracker, AWTEvent.MOUSE_EVENT_MASK);
   }

   public void setAppSession(AppSession appSession)
   {
      _appSession = appSession;

      if (_loginDialog != null)
      {
         _loginDialog.setAuthMgr(_appSession);
      }
      
      if (_appSession == null)
      {
         _appContainer.appUnloaded();
      }
      else
      {
         if (_appSession.getApp() != null) // hack
         {
            _appContainer.appLoaded(_appSession);
         }
      }
      
   }

   private boolean labelEditorLayoutHorizontal = true;
   public void setLabelEditorLayoutHorizontal(boolean value)
   {
      labelEditorLayoutHorizontal = value;
   }
   public boolean isLabelEditorLayoutHorizontal() { return labelEditorLayoutHorizontal; }

   
   public void launch(AppContainer applet)
   {
      final Splash splash = new Splash();
      _appContainer = applet;
      SwingViewMechanism.invokeSwingAction(new SwingAction()
      {
         public void offEDT()
         {
            AppLoader.getInstance().launchApp(splash);
         }

         public void backOnEDT()
         {
            splash.dispose();
         }
      });
   }
   public void end()
   {
      AppLoader.getInstance().endApp();
      _loginDialog = null;
   }
   
   public void launch()
   {
      final Splash splash = new Splash();

      _appContainer = new AppFrame();
      setupInputTracker();

      invokeSwingAction(new SwingAction()
      {
         private RuntimeException ex = null;
         public void offEDT()
         {
            try
            {
               AppLoader.getInstance().launchApp(splash);
            }
            catch (RuntimeException ex)
            {
               this.ex = ex;
               throw ex;
            }
         }
         
         public void backOnEDT()
         {
            splash.dispose();
            _appContainer.setVisible(true);

            if (this.ex != null)
            {
               showErrorDialog(ex);
               System.exit(1);
            }
         }
      });
   }

   public Frame getFrame() { return (Frame) _appContainer; }

   private void showErrorDialog(Exception ex)
   {
      final JDialog dlg = new JDialog((Frame) _appContainer, "An error has occurred", true);
      dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
      MigLayout mainLayout = new MigLayout("insets dialog");
      JPanel errorPane = new JPanel(mainLayout);
      Icon errorIcon = UIManager.getDefaults().getIcon("OptionPane.errorIcon");
      JLabel title = new JLabel("An error has occurred", errorIcon, SwingConstants.LEFT);
      ComponentStyle.addClass(title, "title");
      errorPane.add(title, "north");
      MigLayout layout = new MigLayout("flowy", "fill, grow");
      JPanel centerPane = new JPanel(layout);
      String explanation = "An initialization error has occurred. " +
            "Below is a technical description of the underlying cause.";
      centerPane.add(new JLabel(explanation));
      JLabel errMsg = new Caption(ex.getMessage(), 120);
      ComponentStyle.addClass(errMsg, "message-label");
      centerPane.add(errMsg);
      StringWriter sw = new StringWriter(500);
      ex.printStackTrace(new PrintWriter(sw));
      String text = sw.toString();
      JTextArea area = new JTextArea(text, 35, 80);
      area.setEditable(false);
      centerPane.add(new JScrollPane(area));
      centerPane.add(new JLabel("The application will now close"));
      JButton okBtn = new JButton("OK");
      okBtn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent evt)
         {
            dlg.dispose();
         }
      });
      centerPane.add(okBtn, "alignx trailing, tag ok");
      errorPane.add(centerPane);
      errorPane.setMaximumSize(new Dimension(800,600));
      dlg.setContentPane(errorPane);
      dlg.pack();
      UIUtils.center((Container) _appContainer, dlg, true);
      okBtn.requestFocusInWindow();
      dlg.setVisible(true);
      dlg.dispose();
   }

   public void showLogin()
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            ensureLoginDialog();
            _loginDialog.clear();
         }
      });
   }
   
   private synchronized void ensureLoginDialog()
   {
      if (_loginDialog == null)
      {
         _loginDialog = new LoginDialog(_appSession);
         _appContainer.addLoginDialog(_loginDialog);
         _loginDialog.position();
      }
   }

   public void dismissLogin()
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            ensureLoginDialog();
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

   public void contributeToHeader(JComponent component)
   {
      _appContainer.contributeToHeader(component);
   }


   public void displayViewFor(final Object value, final EView source, final Positioning positioningHint)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            if (value == null) return;

            if (value instanceof Throwable)
            {
               displayFrame(new ExceptionFrame((Throwable) value), positioningHint);
            }
            if (value instanceof Viewable)
            {
               if (value instanceof ComplexEObject)
               {
                  if (_appContainer.focusFrameForObject((EObject) value))
                  {
                     return;
                  }
               }

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
      });
   }



   public void displayView(final EView view, final EView source)
   {
      SwingUtilities.invokeLater(new Runnable()
            {
               public void run()
               {
                  if (source == null)
                  {
                     _appContainer.addFrame(frameFor(view));
                     return;
                  }
                  
                  JComponent srcComp = (JComponent) source;
                  Container container = SwingUtilities.getAncestorOfClass(FlexiFrame.class, srcComp);
                  // since java 6, the above getAncestorOfClass call returns null
                  // this has to do with the discontinuity in contaiment hierarchy that components
                  // such as JList create.  here's a workaround:
                  if (container == null)
                  {
                     Component focusOwner = 
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
                  if (isShiftDown())
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
                     _appContainer.popup(menu);
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
         _appContainer.addFrame(frameFor(view));
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
         return new FlexiFrame(new ListEOPanel(view));
      }
      else if (view instanceof CalendarView)
      {
         return new CalendarFrame(view);
      }
      else if (view instanceof ScheduleView)
      {
         return new CalendarFrame(view);
      }
      else if (view instanceof ComplexEView)
      {
         return new FlexiFrame(new EOPanel(view));
      }

      throw new IllegalArgumentException(
            "Don't know how to make a frame for view: "+view);
   }

   public void displayParamsListView(final EOCommand cmd, final Object value, final CommandInfo cmdInfo)
   {
      SwingUtilities.invokeLater(new Runnable() {
         public void run()
         {
            View paramsView = getParamListView(cmd, value, cmdInfo);
            JInternalFrame frame = new GenericFrame(paramsView);
            _appContainer.addFrame(frame, Positioning.NEARMOUSE);
         }
      });
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
            _appContainer.addFrame(frame, positioning);
         }
      });
   }
   
   public void displayView(final JComponent component, final Positioning positioning)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            Object titleObj = component.getClientProperty("title");
            String title = titleObj == null ? "" : titleObj.toString();
            JInternalFrame frame = new CloseableJInternalFrame(title, true, true, true, true);
            frame.setContentPane(component);
            frame.pack();
            _appContainer.addFrame(frame, positioning);
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
            _appContainer.addFrame(frame);
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
   
   public void message(final String msg)
   {
      if (SwingUtilities.isEventDispatchThread())
      {
         if (_appContainer != null && _appContainer.isVisible())
            _appContainer.onMessage(msg);
      }
      else
      {
         SwingUtilities.invokeLater(new Runnable() {
            public void run()
            {
               if (_appContainer != null && _appContainer.isVisible())
                  _appContainer.onMessage(msg);
            } });
      }
   }

   public void setWaitCursor()
   {
      if (_appContainer == null) return;
      _appContainer.setWaitCursor();
   }

   public void setDefaultCursor()
   {
      if (_appContainer == null) return;
      _appContainer.setDefaultCursor();
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
                  _appContainer.addFrame(frame, positioning);
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
         Thread t = AppLoader.getInstance().newThread(new Runnable()
         {
            public void run()
            {
               ceo.restoreState();
            }
         });
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

   public Object getMapView(MappableEO mappable)
   {
      return new EOMapView(mappable);
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
   public AtomicEditor getMultiPickEditor(String[] options)
   {
      return new MultiPickEditor(options);
   }

   public AtomicRenderer getPasswordRenderer() { return new PasswordRenderer(); }
   public AtomicEditor getPasswordEditor() { return new PasswordEditor(); }

   public AtomicRenderer getBooleanRenderer() { return new BooleanRenderer(); }
   public AtomicEditor getBooleanEditor() { return new BooleanRadioEditor(); }

   public AtomicRenderer getTextRenderer()
   {
      TextEditor editor = new TextEditor();
      editor.setEditable(false);
      editor.setFocusable(false);
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
      editor.setFocusable(false);
      return editor;
   }
   public AtomicEditor getIntEditor() { return new IntEditor(); }
   
   public AtomicRenderer getLongRenderer() { return new StringRenderer(); }
   public AtomicEditor getLongEditor() { return new LongEditor(); } 
   
   public AtomicRenderer getFloatRenderer()
   {
      FloatEditor editor = new FloatEditor();
      editor.setEditable(false);
      editor.setFocusable(false);
      return editor;
   }
   public AtomicEditor getFloatEditor() { return new FloatEditor(); }
   public AtomicRenderer getBigDecimalRenderer()
   {
	   BigDecimalEditor editor = new BigDecimalEditor();
      editor.setEditable(false);
      editor.setFocusable(false);
      return editor;
   }
   public AtomicEditor getBigDecimalEditor() { return new BigDecimalEditor(); }

   public AtomicRenderer getPercentRenderer() { return new StringRenderer(); }
   public AtomicEditor getPercentEditor() { return new PercentEditor(); }

   public AtomicRenderer getDegreeRenderer() { return new StringRenderer(); }
   public AtomicEditor getDegreeEditor() { return new DegreeEditor(); }

   public AtomicRenderer getEmailRenderer() { return new StringRenderer(); }
   public AtomicEditor getEmailEditor() { return new EmailEditor(); }

   public AtomicRenderer getURIRenderer() { return new URIRenderer(); }
   public AtomicEditor getURIEditor() { return new URIEditor(); }

   public AtomicRenderer getUSDollarRenderer() { return new StringRenderer(); }
   public AtomicEditor getUSDollarEditor() { return new USDollarEditor(); }

   public AtomicRenderer getMoneyRenderer() { return new StringRenderer(); }
   public AtomicEditor getMoneyEditor() { return new MoneyEditor(); }

   public AtomicRenderer getUSZipRenderer() { return new StringRenderer(); }
   public AtomicEditor getUSZipEditor() { return new USZipEditor(); }

   public AtomicRenderer getUSPhoneRenderer() { return new StringRenderer(); }
   public AtomicEditor getUSPhoneEditor() { return new USPhoneEditor(); }

   public AtomicRenderer getSSNRenderer() { return new StringRenderer(); }
   public AtomicEditor getSSNEditor() { return new SSNEditor(); }

   public AtomicRenderer getColorRenderer() { return new ColorPicker(false); }
   public AtomicEditor getColorEditor() { return new ColorPicker(); }

   public AtomicRenderer getDateRenderer() { return new DateRenderer(); }
   public AtomicEditor getDateEditor() { return new DateEditor3(); }

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

   public ListEView getListView(AbstractListEO leo)
   {
      if (leo instanceof RelationalList && leo.field() != null && ((IndexedField) leo.field()).isOrdered())
      {
         return new ReorderListView(leo);
      }
      return new JListView(leo);
   }

   public ListEView getListViewAsTable(AbstractListEO leo) { return new TableView(leo); }
   public ListEView getListViewAsIcons(AbstractListEO leo) { return new GridListView(leo); }
   public ListEView getListViewAsTree(AbstractListEO leo) { return new MyListTreeView(leo); }

   public ListEView getListViewAsCalendar(CalEventList list)
   {
      return new CalendarListView(list);
   }
   public Object getListViewOnMap(AbstractListEO leo)
   {
      return new ListMapView(leo);
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
      {
         return new CompositeTableView((CompositeList) leo);
//         return new CompositeTabularView((CompositeList) leo);
      }
      else if (leo instanceof RelationalList)
      {
         return new EditableListView((RelationalList) leo);
      }
      else
      {
         throw new RuntimeException("getEditableListView at the moment works only in the context" +
               " of a compositelist or a relationallist");
      }
   }

   public EView getCommandView(Command cmd, EObject eo)
   {
      return new CommandButton(cmd, eo, null, false);
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

   public Object getAddressViewOnMap(USAddress addr)
   {
      return new AddressViewOnMap(addr);
   }


   public EView getInstructionView(Instruction instruction)
   {
      return new InstructionView(instruction);
   }

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
   
   // Utility:
   public static void invokeSwingAction(final SwingAction action)
   {
      SwingViewMechanism.getInstance().setWaitCursor();
      AppLoader.getInstance().newThread(new Runnable()
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
      }).start();
   }

   public void addStatusCssClassName(JComponent c, ComplexEObject ceo)
   {
      if (ceo.field("status") == null) return;
      if (ceo instanceof NullAssociation) return;

      Set<String> cssClasses = (Set<String>) c.getClientProperty(Selector.CLASS);
      if (cssClasses != null)
      {
         for (String name : cssClasses)
         {
            if (name.startsWith("state-"))
            {
               cssClasses.remove(name);
            }
         }
      }

      String stateName = ceo.field("status").get(ceo).toString();
      String cssClassName = String.format("state-%s", stateName.toLowerCase());
      ComponentStyle.addClass(c, cssClassName);
   }

   
   
   /* ** PropertyChangeSupport "Support" ** */
   protected transient SwingPropertyChangeSupport _changeSupport = new SwingPropertyChangeSupport(this);

   public void firePropertyChange(String propertyName, Object oldValue, Object newValue)
   {
      _changeSupport.firePropertyChange(propertyName, oldValue, newValue);
   }
   public void firePropertyChange(PropertyChangeEvent event)
   {
      _changeSupport.firePropertyChange(event);
   }
   public void firePropertyChange(String propertyName, int oldValue, int newValue)
   {
      firePropertyChange(propertyName, new Integer(oldValue), new Integer(newValue));
   }
   public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue)
   {
      firePropertyChange(propertyName, Boolean.valueOf(oldValue), Boolean.valueOf(newValue));
   }

   public void addPropertyChangeListener(PropertyChangeListener listener)
   {
      _changeSupport.addPropertyChangeListener(listener);
   }
   public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
   {
      _changeSupport.addPropertyChangeListener(propertyName, listener);
   }

   public void removePropertyChangeListener(PropertyChangeListener listener)
   {
      _changeSupport.removePropertyChangeListener(listener);
   }
   public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
   {
      _changeSupport.removePropertyChangeListener(propertyName, listener);
   }

   
}
