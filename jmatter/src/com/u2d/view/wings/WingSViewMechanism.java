package com.u2d.view.wings;

import com.u2d.app.ViewMechanism;
import com.u2d.app.Application;
import com.u2d.app.AppFactory;
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
import com.u2d.element.EOCommand;
import com.u2d.element.CommandInfo;
import com.u2d.list.RelationalList;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jun 22, 2006
 * Time: 6:55:58 PM
 */
public class WingSViewMechanism implements ViewMechanism
{
   private Application _app;
   private AppFrame _appFrame;

   private static WingSViewMechanism _instance = null;
   public static WingSViewMechanism getInstance()
   {
      if (_instance == null)
         _instance = new WingSViewMechanism();
      return _instance;
   }
   private WingSViewMechanism() { }

   // lots of work to do here..

   public void launch()
   {
      _app = AppFactory.getInstance().getApp();
      _appFrame = new AppFrame(_app);
      _appFrame.setVisible(true);
   }

   // login-related
   public void showLogin()
   {
      // [tbd] noop
   }

   public void dismissLogin()
   {
      // [tbd] noop
   }

   public void loginInvalid()
   {
      // [tbd] noop
   }

   public void userLocked()
   {
      // [tbd] noop
   }

   public void initReporting()
   {
      // [tbd] noop
   }

   public void displayViewFor(Object value, EView source, Positioning positioningHint)
   {
   }

   public void displayView(View view, Positioning positioning)
   {
   }

   public void displayView(EView view)
   {
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
   }

   public void onMessage(String message)
   {
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
      return null;
   }

   public ComplexEView getCollapsedView(ComplexEObject ceo)
   {
      return null;
   }

   public ComplexEView getListItemView(ComplexEObject ceo)
   {
      return null;
   }

   public ComplexEView getFormView(ComplexEObject ceo)
   {
      return null;
   }

   public ComplexEView getExpandableView(ComplexEObject ceo)
   {
      return null;
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
      return null;
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
      return null;
   }

   public ComplexEView getChoiceView(AbstractChoiceEO choice)
   {
      return null;
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
      return null;
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
      // need to implement this
      return null;
   }

   public AtomicRenderer getStringRenderer()
   {
      // need to implement this
      return null;
   }

   public AtomicEditor getStringEditor()
   {
      // need to implement this
      return null;
   }

   public AtomicRenderer getPasswordRenderer()
   {
      // punt for now
      return null;
   }

   public AtomicEditor getPasswordEditor()
   {
      // punt for now
      return null;
   }

   public AtomicRenderer getBooleanRenderer()
   {
      // punt for now
      return null;
   }

   public AtomicEditor getBooleanEditor()
   {
      // punt for now
      return null;
   }

   public AtomicRenderer getTextRenderer()
   {
      // punt for now
      return null;
   }

   public AtomicEditor getTextEditor()
   {
      // punt for now
      return null;
   }

   public AtomicRenderer getCharRenderer()
   {
      // punt for now
      return null;
   }

   public AtomicEditor getCharEditor()
   {
      // punt for now
      return null;
   }

   public AtomicRenderer getIntRenderer()
   {
      // punt for now
      return null;
   }

   public AtomicEditor getIntEditor()
   {
      // punt for now
      return null;
   }

   public AtomicRenderer getLongRenderer()
   {
      // punt for now
      return null;
   }

   public AtomicEditor getLongEditor()
   {
      // punt for now
      return null;
   }

   public AtomicRenderer getFloatRenderer()
   {
      // punt for now
      return null;
   }

   public AtomicEditor getFloatEditor()
   {
      // punt for now
      return null;
   }

   public AtomicRenderer getPercentRenderer()
   {
      // punt for now
      return null;
   }

   public AtomicEditor getPercentEditor()
   {
      // punt for now
      return null;
   }

   public AtomicRenderer getEmailRenderer()
   {
      // punt for now
      return null;
   }

   public AtomicEditor getEmailEditor()
   {
      // punt for now
      return null;
   }

   public AtomicRenderer getURIRenderer()
   {
      // punt for now
      return null;
   }

   public AtomicEditor getURIEditor()
   {
      // punt for now
      return null;
   }

   public AtomicRenderer getUSDollarRenderer()
   {
      // punt for now
      return null;
   }

   public AtomicEditor getUSDollarEditor()
   {
      // punt for now
      return null;
   }

   public AtomicRenderer getUSZipRenderer()
   {
      // punt for now
      return null;
   }

   public AtomicEditor getUSZipEditor()
   {
      // punt for now
      return null;
   }

   public AtomicRenderer getUSPhoneRenderer()
   {
      // punt for now
      return null;
   }

   public AtomicEditor getUSPhoneEditor()
   {
      // punt for now
      return null;
   }

   public AtomicRenderer getSSNRenderer()
   {
      // punt for now
      return null;
   }

   public AtomicEditor getSSNEditor()
   {
      // punt for now
      return null;
   }

   public AtomicRenderer getDateRenderer()
   {
      // punt for now
      return null;
   }

   public AtomicEditor getDateEditor()
   {
      // punt for now
      return null;
   }

   public AtomicRenderer getDateWithAgeRenderer()
   {
      // punt for now
      return null;
   }

   public AtomicEditor getDateWithAgeEditor()
   {
      // punt for now
      return null;
   }

   public AtomicRenderer getDateTimeRenderer()
   {
      // punt for now
      return null;
   }

   public AtomicEditor getDateTimeEditor()
   {
      // punt for now
      return null;
   }

   public AtomicRenderer getTimeRenderer()
   {
      // punt for now
      return null;
   }

   public AtomicEditor getTimeEditor()
   {
      // punt for now
      return null;
   }

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

   // views for lists (listeobjects)
   public ListEView getListView(AbstractListEO leo)
   {
      return null;
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
      return null;
   }

   public ListEView getPaginableView(ListEView leview)
   {
      return null;
   }

   public ListEView getEditableListView(AbstractListEO leo)
   {
      return null;
   }

   public ListEView getExpandableListView(RelationalList leo)
   {
      return null;
   }

   public ListEView getMultiChoiceView(AbstractListEO leo)
   {
      return null;
   }

   public ListEView getAlternateListView(AbstractListEO leo, String[] viewNames)
   {
      return null;
   }

   public ComplexEView getAlternateView(ComplexEObject ceo, String[] viewNames)
   {
      return null;
   }

   public void setEditable(Editor editor, boolean editable)
   {
      editor.setEditable(editable);
   }
}
