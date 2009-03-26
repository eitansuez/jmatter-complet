package com.u2d.view.echo;

import com.u2d.app.*;
import com.u2d.view.*;
import com.u2d.view.echo.list.ListView;
import com.u2d.ui.desktop.Positioning;
import com.u2d.wizard.details.Wizard;
import com.u2d.reporting.Reportable;
import com.u2d.model.*;
import com.u2d.find.CompositeQuery;
import com.u2d.field.Association;
import com.u2d.type.AbstractChoiceEO;
import com.u2d.type.composite.USAddress;
import com.u2d.calendar.Calendrier;
import com.u2d.calendar.Schedule;
import com.u2d.calendar.CalEvent;
import com.u2d.calendar.CalEventList;
import com.u2d.element.EOCommand;
import com.u2d.element.CommandInfo;
import com.u2d.element.Command;
import com.u2d.list.RelationalList;
import com.u2d.interaction.Instruction;
import nextapp.echo.app.Component;
import nextapp.echo.app.WindowPane;
//import com.u2d.app.Tracing;
//import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Dec 28, 2006
 * Time: 11:51:38 AM
 */
public class EchoViewMechanism implements ViewMechanism
{
   private AppSession _appSession;
   private AppFrame _appFrame;
   private LoginDialog _loginDialog;
//   private transient Logger _tracer = Tracing.tracer();
   
   public EchoViewMechanism() {}

   public void setAppSession(AppSession appSession) { _appSession = appSession; }

   public AppFrame getAppFrame() { return _appFrame; }

   public void launch()
   {
      _appFrame = new AppFrame(_appSession);
   }

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
   }

   public void displayViewFor(Object value, EView source, Positioning positioningHint)
   {
      if (value == null) return;

      if (value instanceof Throwable)
      {
//         displayFrame(new ExceptionFrame((Throwable) value), positioningHint);
      }
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
      else if (value instanceof Component)
      {
         Component component = (Component) value;
         displayView(component, positioningHint);
      }
   }

   public void displayView(View view, Positioning positioning)
   {
      if (view instanceof WindowPane)
      {
         _appFrame.addFrame((WindowPane) view, positioning);
      }
      else
      {
         _appFrame.addFrame(new GenericFrame(view), positioning);
      }
   }

   public void displayView(EView view, EView source)
   {
      _appFrame.addFrame(frameFor(view));
   }

   private WindowPane frameFor(EView view)
   {
      if (view instanceof WindowPane)
      {
         return (WindowPane) view;
      }
      if (view instanceof ListEView)
      {
//         return new FlexiFrame(new ListEOPanel(view));
         return new FlexiFrame(view);
      }
//      else if (view instanceof CalendarView)
//      {
//         return new CalendarFrame(view);
//      }
//      else if (view instanceof ScheduleView)
//      {
//         return new CalendarFrame(view);
//      }
      else if (view instanceof ComplexEView)
      {
//         return new FlexiFrame(new EOPanel(view));
         return new FlexiFrame(view);
      }

      throw new IllegalArgumentException(
            "Don't know how to make a frame for view: "+view);
   }

   public void displayView(Component component, Positioning positioning)
   {
      _appFrame.addFrame(new FlexiFrame(component));
   }

   public void displayWizard(Wizard wizard)
   {
   }

   public void displayReport(Reportable reportable)
   {
   }

   public void dismiss(EView eView)
   {
   }

   public void message(String string)
   {
   }

   public void showMsgDlg(String string)
   {
   }

   public void showMsgDlg(String string, EView eView)
   {
   }

   public void showMsgDlg(String string, View view)
   {
   }

   public ComplexEView getIconView(ComplexEObject ceo)
   {
      checkState(ceo);
      IconView view = new IconView();
      view.bind(ceo);
      return view;
   }

   public ComplexEView getCollapsedView(ComplexEObject complexEObject)
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

   public ComplexEView getFormView(ComplexEObject complexEObject)
   {
      return null;
   }

   public ComplexEView getExpandableView(ComplexEObject complexEObject)
   {
      return null;
   }

   public ComplexEView getTreeView(ComplexEObject complexEObject)
   {
      return null;
   }

   public ComplexEView getOmniView(ComplexEObject complexEObject)
   {
      return null;
   }

   public ComplexEView getTabBodyView(ComplexEObject complexEObject)
   {
      return null;
   }

   public ComplexEView getFolderView(ComplexEObject folder) { return null; }
   public ComplexEView getOutlookView(ComplexEObject folder) { return null; }

   public Object getMapView(MappableEO mappable) { return null; } 
   public Object getListViewOnMap(AbstractListEO leo) { return null; }

   public EView getTypeRestrictionMgrUi(TypeRestrictionMgr mgr) { return null; }
   public EView getRoleTypeRestrictionMgrUi(RoleTypeRestrictionMgr mgr) { return null; }


   public ComplexEView getQueryView(CompositeQuery compositeQuery)
   {
      return null;
   }
   public Object getAddressViewOnMap(USAddress addr) { return null; }

   public EView getAggregateView(ComplexEObject value) { return null; }
   public ComplexEView getAssociationView(Association association) { return null; }

   public ComplexEView getChoiceView(AbstractChoiceEO abstractChoiceEO) { return null; }
   
   public EView getTypePicker(ComplexType type) { return null; }

   
   

   public ComplexEView getCalendarView(Calendrier calendrier)
   {
      return null;
   }

   public ComplexEView getScheduleView(Schedule schedule)
   {
      return null;
   }

   public ComplexEView getCalEventView(CalEvent calEvent)
   {
      return null;
   }

   public ComplexEView getCalEventView(CalEvent calEvent, Schedule schedule)
   {
      return null;
   }

   public View getParamListView(EOCommand eoCommand, Object object, CommandInfo commandInfo)
   {
      return null;
   }

   public View getFindView(ComplexType complexType)    { return null; }
   public View getFindView2(ComplexType complexType) { return null; }
   public View getFindView2(ComplexType type, Association association) { return null; }

   public AtomicEView getAtomicView(AtomicEObject atomicEObject)
   {
      return null;
   }

   public AtomicRenderer getStringRenderer()
   {
      return null;
   }

   public AtomicEditor getStringEditor()
   {
      return null;
   }

   public AtomicEditor getMultiPickEditor(String[] options)
   {
      return null;
   }

   public AtomicRenderer getPasswordRenderer()
   {
      return null;
   }

   public AtomicEditor getPasswordEditor()
   {
      return null;
   }

   public AtomicRenderer getBooleanRenderer()
   {
      return null;
   }

   public AtomicEditor getBooleanEditor()
   {
      return null;
   }

   public AtomicRenderer getTextRenderer()
   {
      return null;
   }

   public AtomicEditor getTextEditor()
   {
      return null;
   }

   public AtomicRenderer getCharRenderer()
   {
      return null;
   }

   public AtomicEditor getCharEditor()
   {
      return null;
   }

   public AtomicRenderer getIntRenderer()
   {
      return null;
   }

   public AtomicEditor getIntEditor()
   {
      return null;
   }

   public AtomicRenderer getLongRenderer() { return null; } 
   public AtomicEditor getLongEditor() { return null; }

   public AtomicRenderer getFloatRenderer() { return null; } 
   public AtomicEditor getFloatEditor() { return null; }

   public AtomicRenderer getBigDecimalRenderer() { return null; }
   public AtomicEditor getBigDecimalEditor() { return null; }

   public AtomicRenderer getPercentRenderer() { return null; }
   public AtomicEditor getPercentEditor() { return null; }

   public AtomicRenderer getDegreeRenderer() { return null; }
   public AtomicEditor getDegreeEditor() { return null; }

   public AtomicRenderer getEmailRenderer() { return null; }
   public AtomicEditor getEmailEditor() { return null; }

   public AtomicRenderer getURIRenderer() { return null; }
   public AtomicEditor getURIEditor() { return null; }

   public AtomicRenderer getUSDollarRenderer() { return null; }
   public AtomicEditor getUSDollarEditor() { return null; }
   public AtomicRenderer getMoneyRenderer() { return null; } 
   public AtomicEditor getMoneyEditor() { return null; }

   public AtomicRenderer getUSZipRenderer() { return null; }
   public AtomicEditor getUSZipEditor() { return null; }

   public AtomicRenderer getUSPhoneRenderer() { return null; }
   public AtomicEditor getUSPhoneEditor() { return null; }

   public AtomicRenderer getSSNRenderer() { return null; }
   public AtomicEditor getSSNEditor() { return null; }

   public AtomicRenderer getColorRenderer() { return null; }
   public AtomicEditor getColorEditor() { return null; }

   public AtomicRenderer getDateRenderer() { return null; }
   public AtomicEditor getDateEditor() { return null; }

   public AtomicRenderer getDateWithAgeRenderer() { return null; }
   public AtomicEditor getDateWithAgeEditor() { return null; }

   public AtomicRenderer getDateTimeRenderer() { return null; }
   public AtomicEditor getDateTimeEditor() { return null; }

   public AtomicRenderer getTimeRenderer() { return null; }
   public AtomicEditor getTimeEditor() { return null; }

   public AtomicRenderer getTimeSpanRenderer()
   {
      return null;
   }

   public AtomicEditor getTimeSpanEditor()
   {
      return null;
   }

   public AtomicRenderer getChoiceEORenderer()
   {
      return null;
   }

   public AtomicEditor getChoiceEOEditor()
   {
      return null;
   }

   public AtomicRenderer getTermsRenderer()
   {
      return null;
   }

   public AtomicEditor getTermsEditor()
   {
      return null;
   }

   public AtomicRenderer getImageRenderer()
   {
      return null;
   }

   public AtomicEditor getImageEditor()
   {
      return null;
   }

   public AtomicRenderer getFileRenderer()
   {
      return null;
   }

   public AtomicEditor getFileEditor()
   {
      return null;
   }

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


   public ListEView getListViewAsCalendar(CalEventList list)
   {
      return null;
   }

   public ListEView getOmniListView(AbstractListEO leo)
   {
      return null;
   }

   public ListEView getToolbarView(String string, AbstractListEO leo)
   {
      return null;
   }

   public ListEView getRelationalListView(RelationalList list)
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
      return null;
   }

   public ListEView getPaginableView(ListEView listEView)
   {
      // TODO: implement
      // for now..
      return listEView;
   }

   public ListEView getEditableListView(AbstractListEO leo)
   {
      return null;
   }

   public ListEView getExpandableListView(RelationalList list)
   {
      return null;
   }

   public ListEView getMultiChoiceView(AbstractListEO leo)
   {
      return null;
   }

   public ListEView getAlternateListView(AbstractListEO leo, String[] strings)
   {
      // for now short-circuit alternate views..
      return getListView(leo);
   }

   public ComplexEView getAlternateView(ComplexEObject complexEObject, String[] strings)
   {
      return null;
   }

   public void setEditable(Editor editor, boolean b)
   {
   }
   
   private void checkState(ComplexEObject ceo)
   {
      if (ceo.isNullState())
      {
         ceo.restoreState();
      }
   }

   public EView getInstructionView(Instruction instruction)
   {
      return null;
   }

   public void displayParamsListView(EOCommand cmd, Object value, CommandInfo cmdInfo)
   {
   }

   public EView getCommandView(Command cmd, EObject eo)
   {
      return null;
   }
}
