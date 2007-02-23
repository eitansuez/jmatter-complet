package com.u2d.app;

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
 * Date: Sep 20, 2006
 * Time: 11:48:56 AM
 * 
 * The Null Pattern
 * This is returned as the view mechanism before the actual view mechanism
 * is launched / comes up.
 */
public class NullViewMechanism implements ViewMechanism
{
   public void launch() { }
   public void showLogin() { } 
   public void dismissLogin() { }
   public void loginInvalid() { } 
   public void userLocked() { } 
   public void initReporting() { }
   public void displayViewFor(Object value, EView source, Positioning positioningHint) { }
   public void displayView(View view, Positioning positioning) { }
   public void displayView(EView view, EView source) { }
   public void displayWizard(Wizard wizard) { }
   public void displayReport(Reportable reportable) { }
   public void dismiss(EView eview) { }
   public void message(String message) { }
   public void showMsgDlg(String message) { }
   public void showMsgDlg(String message, EView source) { }
   public void showMsgDlg(String message, View source) { }

   // views for complex types (complexeobjects)
   public ComplexEView getIconView(ComplexEObject ceo) { return null; }
   public ComplexEView getCollapsedView(ComplexEObject ceo) { return null; }
   public ComplexEView getListItemView(ComplexEObject ceo) { return null; }
   public ComplexEView getFormView(ComplexEObject ceo) { return null; }
   public ComplexEView getExpandableView(ComplexEObject ceo) { return null; }
   public ComplexEView getTreeView(ComplexEObject ceo) { return null; }
   public ComplexEView getOmniView(ComplexEObject ceo) { return null; }
   public ComplexEView getTabBodyView(ComplexEObject ceo) { return null; }

   public ComplexEView getFolderView(ComplexEObject ceo) { return null; }
   public ComplexEView getOutlookView(ComplexEObject ceo) { return null; }
   public ComplexEView getQueryView(CompositeQuery query) { return null; }
   public ComplexEView getAssociationView(Association association) { return null; }
   public ComplexEView getChoiceView(AbstractChoiceEO choice) { return null; }

   public ComplexEView getCalendarView(Calendrier calendar) { return null; }
   public ComplexEView getScheduleView(Schedule schedule) { return null; }
   public ComplexEView getCalEventView(CalEvent event) { return null; }
   public ComplexEView getCalEventView(CalEvent event, Schedule schedule) { return null; }

   public View getParamListView(EOCommand cmd, Object value, CommandInfo cmdInfo) { return null; }
   public View getFindView(ComplexType type) { return null; }
   public View getFindView2(ComplexType type) { return null; }

   public AtomicEView getAtomicView(AtomicEObject eo) { return null; }

   public AtomicRenderer getStringRenderer() { return null; }
   public AtomicEditor getStringEditor() { return null; } 
   public AtomicRenderer getPasswordRenderer() { return null; } 
   public AtomicEditor getPasswordEditor() { return null; } 
   public AtomicRenderer getBooleanRenderer() { return null; } 
   public AtomicEditor getBooleanEditor() { return null; } 
   public AtomicRenderer getTextRenderer() { return null; } 
   public AtomicEditor getTextEditor() { return null; } 
   public AtomicRenderer getCharRenderer() { return null; } 
   public AtomicEditor getCharEditor() { return null; } 
   public AtomicRenderer getIntRenderer() { return null; } 
   public AtomicEditor getIntEditor() { return null; } 
   public AtomicRenderer getLongRenderer() { return null; } 
   public AtomicEditor getLongEditor() { return null; } 
   public AtomicRenderer getFloatRenderer() { return null; } 
   public AtomicEditor getFloatEditor() { return null; } 
   public AtomicRenderer getPercentRenderer() { return null; } 
   public AtomicEditor getPercentEditor() { return null; } 
   public AtomicRenderer getEmailRenderer() { return null; } 
   public AtomicEditor getEmailEditor() { return null; } 
   public AtomicRenderer getURIRenderer() { return null; } 
   public AtomicEditor getURIEditor() { return null; } 
   public AtomicRenderer getUSDollarRenderer() { return null; } 
   public AtomicEditor getUSDollarEditor() { return null; } 
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
   public AtomicRenderer getTimeSpanRenderer() { return null; } 
   public AtomicEditor getTimeSpanEditor() { return null; } 
   public AtomicRenderer getChoiceEORenderer() { return null; } 
   public AtomicEditor getChoiceEOEditor() { return null; } 
   public AtomicRenderer getTermsRenderer() { return null; } 
   public AtomicEditor getTermsEditor() { return null; } 
   public AtomicRenderer getImageRenderer() { return null; } 
   public AtomicEditor getImageEditor() { return null; } 
   public AtomicRenderer getFileRenderer() { return null; } 
   public AtomicEditor getFileEditor() { return null; }

   public ListEView getListView(AbstractListEO leo) { return null; }
   public ListEView getListViewAsTable(AbstractListEO leo) { return null; }
   public ListEView getListViewAsIcons(AbstractListEO leo) { return null; }
   public ListEView getListViewAsTree(AbstractListEO leo) { return null; }
   public ListEView getOmniListView(AbstractListEO leo) { return null; }
   public ListEView getToolbarView(String name, AbstractListEO leo) { return null; }
   public ListEView getRelationalListView(RelationalList leo) { return null; }
   public ListEView getPickView(AbstractListEO leo) { return null; }
   public View getMultiPickView(AbstractListEO leo) { return null; }
   public ListEView getListViewMinimized(AbstractListEO leo) { return null; }
   public ListEView getPaginableView(ListEView leview) { return null; }
   public ListEView getEditableListView(AbstractListEO leo) { return null; }
   public ListEView getExpandableListView(RelationalList leo) { return null; }
   public ListEView getMultiChoiceView(AbstractListEO leo) { return null; }
   public ListEView getAlternateListView(AbstractListEO leo, String[] viewNames) { return null; }
   public ComplexEView getAlternateView(ComplexEObject ceo, String[] viewNames) { return null; }
   
   public void setEditable(Editor editor, boolean editable) { }
}
