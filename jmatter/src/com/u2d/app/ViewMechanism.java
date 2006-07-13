/*
 * Created on Jan 26, 2004
 */
package com.u2d.app;

import com.u2d.calendar.*;
import com.u2d.element.CommandInfo;
import com.u2d.element.EOCommand;
import com.u2d.field.Association;
import com.u2d.find.CompositeQuery;
import com.u2d.list.RelationalList;
import com.u2d.model.*;
import com.u2d.reporting.*;
import com.u2d.type.AbstractChoiceEO;
import com.u2d.view.*;
import com.u2d.wizard.details.Wizard;
import com.u2d.model.AtomicEditor;
import com.u2d.model.AtomicRenderer;
import com.u2d.ui.desktop.Positioning;

/**
 * @author Eitan Suez
 */
public interface ViewMechanism
{
   public void launch();

   // login-related
   public void showLogin();
   public void dismissLogin();
   public void loginInvalid();
   public void userLocked();
   
   public void initReporting();

   public void displayViewFor(Object value, EView source, Positioning positioningHint);
   public void displayView(View view, Positioning positioning);
   public void displayView(EView view);
   public void displayWizard(Wizard wizard);
   public void displayReport(Reportable reportable);

   public void dismiss(EView eview);

   public void onMessage(String message);

   public void showMsgDlg(String message); 
   public void showMsgDlg(String message, EView source);
   public void showMsgDlg(String message, View source);

   // views for complex types (complexeobjects)
   public ComplexEView getIconView(ComplexEObject ceo);
   public ComplexEView getCollapsedView(ComplexEObject ceo);
   public ComplexEView getListItemView(ComplexEObject ceo);
   public ComplexEView getFormView(ComplexEObject ceo);
   public ComplexEView getExpandableView(ComplexEObject ceo);
   public ComplexEView getTreeView(ComplexEObject ceo);
   public ComplexEView getOmniView(ComplexEObject ceo);
   public ComplexEView getTabBodyView(ComplexEObject ceo);


   // TODO: revise signature to strictly apply to Folder argument types
   public ComplexEView getFolderView(ComplexEObject ceo);
   public ComplexEView getOutlookView(ComplexEObject ceo);
   public ComplexEView getQueryView(CompositeQuery query);

   public ComplexEView getAssociationView(Association association);
   public ComplexEView getChoiceView(AbstractChoiceEO choice);

   // for calendaring / scheduling
   public ComplexEView getCalendarView(Calendrier calendar);
   public ComplexEView getScheduleView(Schedule schedule);
   public ComplexEView getCalEventView(CalEvent event);
   public ComplexEView getCalEventView(CalEvent event, Schedule schedule);

   // for commands
   public View getParamListView(EOCommand cmd, Object value, CommandInfo cmdInfo);
   public View getFindView(ComplexType type);
   public View getFindView2(ComplexType type);

   // views for atomic types (atomiceobjects)
   public AtomicEView getAtomicView(AtomicEObject eo);

   public AtomicRenderer getStringRenderer();
   public AtomicEditor getStringEditor();

   public AtomicRenderer getPasswordRenderer();
   public AtomicEditor getPasswordEditor();

   public AtomicRenderer getBooleanRenderer();
   public AtomicEditor getBooleanEditor();

   public AtomicRenderer getTextRenderer();
   public AtomicEditor getTextEditor();

   public AtomicRenderer getCharRenderer();
   public AtomicEditor getCharEditor();

   public AtomicRenderer getIntRenderer();
   public AtomicEditor getIntEditor();

   public AtomicRenderer getLongRenderer();
   public AtomicEditor getLongEditor();

   public AtomicRenderer getFloatRenderer();
   public AtomicEditor getFloatEditor();

   public AtomicRenderer getPercentRenderer();
   public AtomicEditor getPercentEditor();

   public AtomicRenderer getEmailRenderer();
   public AtomicEditor getEmailEditor();

   public AtomicRenderer getURIRenderer();
   public AtomicEditor getURIEditor();

   public AtomicRenderer getUSDollarRenderer();
   public AtomicEditor getUSDollarEditor();

   public AtomicRenderer getUSZipRenderer();
   public AtomicEditor getUSZipEditor();

   public AtomicRenderer getUSPhoneRenderer();
   public AtomicEditor getUSPhoneEditor();

   public AtomicRenderer getSSNRenderer();
   public AtomicEditor getSSNEditor();

   public AtomicRenderer getDateRenderer();
   public AtomicEditor getDateEditor();

   public AtomicRenderer getDateWithAgeRenderer();
   public AtomicEditor getDateWithAgeEditor();

   public AtomicRenderer getDateTimeRenderer();
   public AtomicEditor getDateTimeEditor();

   public AtomicRenderer getTimeRenderer();
   public AtomicEditor getTimeEditor();

   public AtomicRenderer getTimeSpanRenderer();
   public AtomicEditor getTimeSpanEditor();

   public AtomicRenderer getChoiceEORenderer();
   public AtomicEditor getChoiceEOEditor();

   public AtomicRenderer getTermsRenderer();
   public AtomicEditor getTermsEditor();

   public AtomicRenderer getImageRenderer();
   public AtomicEditor getImageEditor();

   public AtomicRenderer getFileRenderer();
   public AtomicEditor getFileEditor();

   // views for lists (listeobjects)
   public ListEView getListView(AbstractListEO leo);
   public ListEView getListViewAsTable(AbstractListEO leo);
   public ListEView getListViewAsIcons(AbstractListEO leo);
   public ListEView getListViewAsTree(AbstractListEO leo);
   public ListEView getOmniListView(AbstractListEO leo);
   public ListEView getToolbarView(String name, AbstractListEO leo);
   public ListEView getRelationalListView(RelationalList leo);
   public ListEView getPickView(AbstractListEO leo);
   public View getMultiPickView(AbstractListEO leo);
   public ListEView getListViewMinimized(AbstractListEO leo);
   public ListEView getPaginableView(ListEView leview);
   public ListEView getEditableListView(AbstractListEO leo);
   public ListEView getExpandableListView(RelationalList leo);
   
   public ListEView getMultiChoiceView(AbstractListEO leo);

   public ListEView getAlternateListView(AbstractListEO leo, String[] viewNames);
   public ComplexEView getAlternateView(ComplexEObject ceo, String[] viewNames);

   public void setEditable(Editor editor, boolean editable);
}
