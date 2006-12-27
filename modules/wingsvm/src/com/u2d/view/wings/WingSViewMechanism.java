package com.u2d.view.wings;

import com.u2d.app.*;
import com.u2d.view.*;
import com.u2d.view.wings.list.*;
import com.u2d.view.wings.atom.*;
import com.u2d.ui.desktop.Positioning;
import com.u2d.wizard.details.Wizard;
import com.u2d.reporting.Reportable;
import com.u2d.model.*;
import com.u2d.find.CompositeQuery;
import com.u2d.field.Association;
import com.u2d.type.AbstractChoiceEO;
import com.u2d.calendar.Calendrier;
import com.u2d.calendar.Schedule;
import com.u2d.calendar.CalEvent;
import com.u2d.element.EOCommand;
import com.u2d.element.CommandInfo;
import com.u2d.list.RelationalList;
import com.u2d.list.CompositeList;
import org.wings.SInternalFrame;
import org.wings.SContainer;
import org.wings.SComponent;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jun 22, 2006
 * Time: 6:55:58 PM
 */
public class WingSViewMechanism implements ViewMechanism
{
   private AppSession _appSession;
   private AppFrame _appFrame;
   private LoginDialog _loginDialog;

   private transient Logger _tracer = Tracing.tracer();

   public WingSViewMechanism() { }

   public void setAppSession(AppSession appSession) { _appSession = appSession; }

   // lots of work to do here..

   public void launch()
   {
      _appFrame = new AppFrame(_appSession);
      _appFrame.setVisible(true);
   }

   // login-related
   public void showLogin()
   {
      if (_loginDialog == null)
      {
         _loginDialog = new LoginDialog(_appSession);
         _appFrame.addLoginDialog(_loginDialog);
      }

      _appFrame.centerFrame(_loginDialog);
      _loginDialog.clear();
   }

   public void dismissLogin()
   {
      _loginDialog.setVisible(false);
   }

   public void loginInvalid()
   {
      _loginDialog.loginInvalid();
   }

   public void userLocked()
   {
      _loginDialog.userLocked();
   }

   public void initReporting()
   {
      // [tbd] noop
   }

   public void displayViewFor(Object value, EView source, Positioning positioningHint)
   {
      _tracer.fine("displayViewFor:: value: "+value);
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

         _tracer.fine("displayViewFor:: displaying view: "+view);
         displayView(view, source);
      }
      else if (value instanceof EView)
      {
         _tracer.fine("displayViewFor:: view is an EView");
         displayView((EView) value, source);
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
   }

   public void displayView(View view, Positioning positioning)
   {
      if (view instanceof SInternalFrame)
      {
         _appFrame.addFrame((SInternalFrame) view, positioning);
      }
      else
      {
         GenericFrame frame = new GenericFrame(view);
         _appFrame.addFrame(frame, positioning);
      }
   }

   public void displayView(EView view, EView source)
   {
      _tracer.fine("in displayview(EView)..");

      _appFrame.addFrame(frameFor(view));
   }

   private SInternalFrame frameFor(EView view)
   {
      if (view instanceof SInternalFrame)
         return (SInternalFrame) view;
      if (view instanceof ListEView)
      {
         return new ListEOFrame((ListEView) view);
      }
//      else if (view instanceof CalendarView)
//         return new CalendarFrame((CalendarView) view);
//      else if (view instanceof ScheduleView)
//         return new CalendarFrame((ScheduleView) view);
      else if (view instanceof ComplexEView)
         return new EOFrame((ComplexEView) view);

      throw new IllegalArgumentException(
            "Don't know how to make a frame for view: "+view);
   }

   public void displayWizard(Wizard wizard)
   {
      // [tbd] noop
   }

   public void displayReport(Reportable reportable)
   {
      // [tbd] noop
   }

   public void dismiss(EView eview)
   {
      getParentInternalFrame((SComponent) eview).dispose();
   }

   public void onMessage(String message)
   {
      _appFrame.onMessage(message);
   }

   public void showMsgDlg(String message)
   {
   }

   public void showMsgDlg(String message, EView source)
   {
   }

   public void showMsgDlg(String message, View source)
   {
   }

   // views for complex types (complexeobjects)
   public ComplexEView getIconView(ComplexEObject ceo)
   {
      checkState(ceo);
      IconView view = new IconView();
      view.bind(ceo);
      return view;
   }

   public ComplexEView getCollapsedView(ComplexEObject ceo)
   {
      return null;
   }

   public ComplexEView getListItemView(ComplexEObject ceo)
   {
      checkState(ceo);
      ListItemView view = new ListItemView();
      view.bind(ceo);
      return view;
   }

   public ComplexEView getFormView(ComplexEObject ceo)
   {
      return new TopLevelFormView(ceo);
   }

   public ComplexEView getExpandableView(ComplexEObject ceo)
   {
      checkState(ceo);
      ExpandableView view = new ExpandableView();
      view.bind(ceo);
      return view;
   }

   public ComplexEView getTreeView(ComplexEObject ceo)
   {
      return null;
   }

   public ComplexEView getOmniView(ComplexEObject ceo)
   {
      // [tbd] noop (fancy views:  will be implemented in second phase of port)
      return null;
   }

   public ComplexEView getTabBodyView(ComplexEObject ceo)
   {
      checkState(ceo);
      return new TabBodyView(ceo);
   }

   // TODO: revise signature to strictly apply to Folder argument types
   public ComplexEView getFolderView(ComplexEObject ceo)
   {
      return null;
   }

   public ComplexEView getOutlookView(ComplexEObject ceo)
   {
      return null;
   }

   public ComplexEView getQueryView(CompositeQuery query)
   {
      return null;
   }

   public ComplexEView getAssociationView(Association association)
   {
      return new AssociationView(association);
   }

   /* the understanding here is that choices are also CEOs */
   public ComplexEView getChoiceView(AbstractChoiceEO choice)
   {
      return new ChoiceView(choice);
   }

   // for calendaring / scheduling
   public ComplexEView getCalendarView(Calendrier calendar)
   {
      return null;
   }

   public ComplexEView getScheduleView(Schedule schedule)
   {
      return null;
   }

   public ComplexEView getCalEventView(CalEvent event)
   {
      return null;
   }

   public ComplexEView getCalEventView(CalEvent event, Schedule schedule)
   {
      return null;
   }

   // for commands
   public View getParamListView(EOCommand cmd, Object value, CommandInfo cmdInfo)
   {
      return new ParamListView(cmd, value, cmdInfo);
   }

   public View getFindView(ComplexType type)
   {
      return null;
   }

   public View getFindView2(ComplexType type)
   {
      return null;
   }

   // views for atomic types (atomiceobjects)
   public AtomicEView getAtomicView(AtomicEObject eo)
   {
      AtomicView view = new AtomicView();
      view.bind(eo);
      return view;
   }

   public AtomicRenderer getStringRenderer()    { return new StringRenderer(); }
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
   public AtomicEditor getTextEditor() { return new TextEditor(); }
   public AtomicRenderer getCharRenderer() { return getStringRenderer(); }
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

   public AtomicRenderer getPercentRenderer() { return getStringRenderer(); }
   public AtomicEditor getPercentEditor() { return new PercentEditor(); }

   public AtomicRenderer getEmailRenderer() { return new StringRenderer(); }
   public AtomicEditor getEmailEditor() { return new EmailEditor(); }

   public AtomicRenderer getURIRenderer() { return new URIRenderer(); }
   public AtomicEditor getURIEditor() { return new URIEditor(); }

   public AtomicRenderer getUSDollarRenderer() { return getStringRenderer(); }
   public AtomicEditor getUSDollarEditor() { return new USDollarEditor(); }

   public AtomicRenderer getUSZipRenderer() { return getStringRenderer(); }
   public AtomicEditor getUSZipEditor() { return new USZipEditor(); }

   public AtomicRenderer getUSPhoneRenderer() { return getStringRenderer(); }
   public AtomicEditor getUSPhoneEditor() { return new USPhoneEditor(); }

   public AtomicRenderer getSSNRenderer() { return getStringRenderer(); }
   public AtomicEditor getSSNEditor() { return new SSNEditor(); }


   // TODO: implement
   public AtomicRenderer getColorRenderer() { return null; }
   public AtomicEditor getColorEditor() { return null; }

   public AtomicRenderer getDateRenderer() { return new DateRenderer(); }
   public AtomicEditor getDateEditor() { return new DateEditor(); }


   public AtomicRenderer getDateWithAgeRenderer() { return new DateWithAgeRenderer(); }
   public AtomicEditor getDateWithAgeEditor() { return new DateWithAgeEditor(); }

   public AtomicRenderer getDateTimeRenderer() { return getStringRenderer(); }
   public AtomicEditor getDateTimeEditor() { return new DateTimeEditor(); }

   public AtomicRenderer getTimeRenderer() { return new StringRenderer(); }
   public AtomicEditor getTimeEditor() { return new TimeEditor(); }

   public AtomicRenderer getTimeSpanRenderer()
   {
      // punt for now
      return null;
   }

   public AtomicEditor getTimeSpanEditor()
   {
      // punt for now
      return null;
   }

   public AtomicRenderer getChoiceEORenderer() { return new StringRenderer(); }
   public AtomicEditor getChoiceEOEditor() { return new ChoiceEOEditor(); }

   public AtomicRenderer getTermsRenderer()
   {
      return null;
   }

   public AtomicEditor getTermsEditor()
   {
      return null;
   }

   public AtomicRenderer getImageRenderer() { return new ImagePicker(); }
   public AtomicEditor getImageEditor()
   {
      ImagePicker imgPicker = new ImagePicker();
      imgPicker.putInEditMode();
      return imgPicker;
   }

   public AtomicRenderer getFileRenderer() { return new FilePicker(); }
   public AtomicEditor getFileEditor() { return new FilePicker().putInEditMode(); }

   // views for lists (listeobjects)
   public ListEView getListView(AbstractListEO leo)
   {
      return new ListView(leo);
   }

   public ListEView getListViewAsTable(AbstractListEO leo)
   {
      return null;
   }

   public ListEView getListViewAsIcons(AbstractListEO leo)
   {
      return null;
   }

   public ListEView getListViewAsTree(AbstractListEO leo)
   {
      return null;
   }

   public ListEView getOmniListView(AbstractListEO leo)
   {
      return null;
   }

   public ListEView getToolbarView(String name, AbstractListEO leo)
   {
      return null;
   }

   public ListEView getRelationalListView(RelationalList leo)
   {
      return null;
   }

   public ListEView getPickView(AbstractListEO leo)
   {
      return null;
   }

   public View getMultiPickView(AbstractListEO leo)
   {
      return null;
   }

   public ListEView getListViewMinimized(AbstractListEO leo)
   {
      return new com.u2d.view.wings.list.ListItemView(leo);
   }

   public ListEView getPaginableView(ListEView leview)
   {
      return new PaginableView(leview);
   }

   public ListEView getEditableListView(AbstractListEO leo)
   {
      if (leo instanceof CompositeList)
      {
         //return new CompositeTableView((CompositeList) leo);
         throw new RuntimeException("TBD..implementation of an editable list view for composites for web");
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

   public ListEView getExpandableListView(RelationalList leo)
   {
      return new com.u2d.view.wings.list.ExpandableView(leo);
   }

   public ListEView getMultiChoiceView(AbstractListEO leo)
   {
      return null;
   }

   public ListEView getAlternateListView(AbstractListEO leo, String[] viewNames)
   {
//      return null;
      // stubbed for now:
      return getListView(leo);
   }

   public ComplexEView getAlternateView(ComplexEObject ceo, String[] viewNames)
   {
//      return null;
      // stubbed for now:
      return getFormView(ceo);
   }

   public void setEditable(Editor editor, boolean editable)
   {
      editor.setEditable(editable);
   }


   private void checkState(final ComplexEObject ceo)
   {
      // it's time to bring out the aop..[tbd]
      if (ceo.isNullState()) ceo.restoreState();
   }

   public static SInternalFrame getParentInternalFrame(SComponent item)
   {
      SContainer parent = item.getParent();
      while (!(parent instanceof SInternalFrame))
      {
         parent = parent.getParent();
      }
      return (SInternalFrame) parent;
   }

   
}
