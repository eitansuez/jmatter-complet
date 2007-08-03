package com.u2d.view.echo2;

import com.u2d.app.ViewMechanism;
import com.u2d.app.AppSession;
import com.u2d.app.TypeRestrictionMgr;
import com.u2d.app.RoleTypeRestrictionMgr;
import com.u2d.view.*;
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
import com.u2d.calendar.CalEventList;
import com.u2d.element.EOCommand;
import com.u2d.element.CommandInfo;
import com.u2d.list.RelationalList;
//import com.u2d.app.Tracing;
//import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Dec 28, 2006
 * Time: 11:51:38 AM
 */
public class Echo2ViewMechanism implements ViewMechanism
{
   private AppSession _appSession;
   private AppFrame _appFrame;
   private LoginDialog _loginDialog;
//   private transient Logger _tracer = Tracing.tracer();
   
   public Echo2ViewMechanism() {}

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

   public void displayViewFor(Object object, EView eView, Positioning positioning)
   {
   }

   public void displayView(View view, Positioning positioning)
   {
   }

   public void displayView(EView eView, EView eView1)
   {
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

   public ComplexEView getListItemView(ComplexEObject complexEObject)
   {
      return null;
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

   public EView getTypeRestrictionMgrUi(TypeRestrictionMgr mgr) { return null; }
   public EView getRoleTypeRestrictionMgrUi(RoleTypeRestrictionMgr mgr) { return null; }

   public ComplexEView getQueryView(CompositeQuery compositeQuery)
   {
      return null;
   }

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

   public AtomicRenderer getLongRenderer()
   {
      return null;
   }

   public AtomicEditor getLongEditor()
   {
      return null;
   }

   public AtomicRenderer getFloatRenderer()
   {
      return null;
   }

   public AtomicEditor getFloatEditor()
   {
      return null;
   }

   public AtomicRenderer getPercentRenderer()
   {
      return null;
   }

   public AtomicEditor getPercentEditor()
   {
      return null;
   }

   public AtomicRenderer getEmailRenderer()
   {
      return null;
   }

   public AtomicEditor getEmailEditor()
   {
      return null;
   }

   public AtomicRenderer getURIRenderer()
   {
      return null;
   }

   public AtomicEditor getURIEditor()
   {
      return null;
   }

   public AtomicRenderer getUSDollarRenderer()
   {
      return null;
   }

   public AtomicEditor getUSDollarEditor()
   {
      return null;
   }

   public AtomicRenderer getUSZipRenderer()
   {
      return null;
   }

   public AtomicEditor getUSZipEditor()
   {
      return null;
   }

   public AtomicRenderer getUSPhoneRenderer()
   {
      return null;
   }

   public AtomicEditor getUSPhoneEditor()
   {
      return null;
   }

   public AtomicRenderer getSSNRenderer()
   {
      return null;
   }

   public AtomicEditor getSSNEditor()
   {
      return null;
   }

   public AtomicRenderer getColorRenderer()
   {
      return null;
   }

   public AtomicEditor getColorEditor()
   {
      return null;
   }

   public AtomicRenderer getDateRenderer()
   {
      return null;
   }

   public AtomicEditor getDateEditor()
   {
      return null;
   }

   public AtomicRenderer getDateWithAgeRenderer()
   {
      return null;
   }

   public AtomicEditor getDateWithAgeEditor()
   {
      return null;
   }

   public AtomicRenderer getDateTimeRenderer()
   {
      return null;
   }

   public AtomicEditor getDateTimeEditor()
   {
      return null;
   }

   public AtomicRenderer getTimeRenderer()
   {
      return null;
   }

   public AtomicEditor getTimeEditor()
   {
      return null;
   }

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

   public ListEView getListView(AbstractListEO abstractListEO)
   {
      return null;
   }

   public ListEView getListViewAsTable(AbstractListEO abstractListEO)
   {
      return null;
   }

   public ListEView getListViewAsIcons(AbstractListEO abstractListEO)
   {
      return null;
   }

   public ListEView getListViewAsTree(AbstractListEO abstractListEO)
   {
      return null;
   }


   public ListEView getListViewAsCalendar(CalEventList list)
   {
      return null;
   }

   public ListEView getOmniListView(AbstractListEO abstractListEO)
   {
      return null;
   }

   public ListEView getToolbarView(String string, AbstractListEO abstractListEO)
   {
      return null;
   }

   public ListEView getRelationalListView(RelationalList relationalList)
   {
      return null;
   }

   public ListEView getPickView(AbstractListEO abstractListEO)
   {
      return null;
   }

   public View getMultiPickView(AbstractListEO abstractListEO)
   {
      return null;
   }

   public ListEView getListViewMinimized(AbstractListEO abstractListEO)
   {
      return null;
   }

   public ListEView getPaginableView(ListEView listEView)
   {
      return null;
   }

   public ListEView getEditableListView(AbstractListEO abstractListEO)
   {
      return null;
   }

   public ListEView getExpandableListView(RelationalList relationalList)
   {
      return null;
   }

   public ListEView getMultiChoiceView(AbstractListEO abstractListEO)
   {
      return null;
   }

   public ListEView getAlternateListView(AbstractListEO abstractListEO, String[] strings)
   {
      return null;
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
   
}
