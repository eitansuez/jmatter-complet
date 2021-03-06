/*
 * Created on Jan 26, 2004
 */
package com.u2d.app;

import com.u2d.calendar.CalEvent;
import com.u2d.calendar.CalEventList;
import com.u2d.calendar.Calendrier;
import com.u2d.calendar.Schedule;
import com.u2d.element.CommandInfo;
import com.u2d.element.EOCommand;
import com.u2d.element.Command;
import com.u2d.field.Association;
import com.u2d.find.CompositeQuery;
import com.u2d.list.RelationalList;
import com.u2d.model.*;
import com.u2d.type.AbstractChoiceEO;
import com.u2d.type.composite.USAddress;
import com.u2d.ui.desktop.Positioning;
import com.u2d.view.*;
import com.u2d.wizard.details.Wizard;
import com.u2d.interaction.Instruction;

/**
 * @author Eitan Suez
 */
public interface ViewMechanism
{
   public void launch();
   public void setAppSession(AppSession session);

   // login-related
   public void showLogin();
   public void dismissLogin();
   public void loginInvalid();
   public void userLocked();
   
   public void displayViewFor(Object value, EView source, Positioning positioningHint);
   public void displayView(View view, Positioning positioning);
   public void displayView(EView view, EView source);
   public void displayWizard(Wizard wizard);
   public void displayParamsListView(EOCommand cmd, Object value, CommandInfo cmdInfo);

   public void dismiss(EView eview);

   public void message(String message);

   // views for complex types (complexeobjects)
   public ComplexEView getIconView(ComplexEObject ceo);
   public ComplexEView getCollapsedView(ComplexEObject ceo);
   public ComplexEView getListItemView(ComplexEObject ceo);
   public ComplexEView getFormView(ComplexEObject ceo);
   public ComplexEView getExpandableView(ComplexEObject ceo);
   public ComplexEView getTreeView(ComplexEObject ceo);
   public ComplexEView getOmniView(ComplexEObject ceo);
   public ComplexEView getTabBodyView(ComplexEObject ceo);


   public ComplexEView getFolderView(ComplexEObject ceo);
   public ComplexEView getOutlookView(ComplexEObject ceo);
   public ComplexEView getQueryView(CompositeQuery query);
   
   public Object getAddressViewOnMap(USAddress addr);

   public EView getTypeRestrictionMgrUi(TypeRestrictionMgr mgr);
   public EView getRoleTypeRestrictionMgrUi(RoleTypeRestrictionMgr mgr);
   
   public EView getAggregateView(ComplexEObject value);
   public ComplexEView getAssociationView(Association association);
   public ComplexEView getChoiceView(AbstractChoiceEO choice);

   public EView getTypePicker(ComplexType type);

   // for calendaring / scheduling
   public ComplexEView getCalendarView(Calendrier calendar);
   public ComplexEView getScheduleView(Schedule schedule);
   public ComplexEView getCalEventView(CalEvent event);
   public ComplexEView getCalEventView(CalEvent event, Schedule schedule);
   
   /* TODO: retrofit to return an EView */
   public Object getMapView(MappableEO mappable);
   /* TODO: retrofit to return a ListEView */
   public Object getListViewOnMap(AbstractListEO leo);

   // for commands
   public View getParamListView(EOCommand cmd, Object value, CommandInfo cmdInfo);
   public View getFindView(ComplexType type);
   public View getFindView2(ComplexType type);
   public View getFindView2(ComplexType type, Association association);

   // views for atomic types (atomiceobjects)
   public AtomicEView getAtomicView(AtomicEObject eo);

   public AtomicRenderer getStringRenderer();
   public AtomicEditor getStringEditor();
   public AtomicEditor getMultiPickEditor(String[] options);

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

   public AtomicRenderer getBigDecimalRenderer();
   public AtomicEditor getBigDecimalEditor();

   public AtomicRenderer getPercentRenderer();
   public AtomicEditor getPercentEditor();

   public AtomicRenderer getDegreeRenderer();
   public AtomicEditor getDegreeEditor();

   public AtomicRenderer getEmailRenderer();
   public AtomicEditor getEmailEditor();

   public AtomicRenderer getURIRenderer();
   public AtomicEditor getURIEditor();

   /**
    * @deprecated Use Money Class and Corresponding MoneyEditor instead
    */
   public AtomicRenderer getUSDollarRenderer();
   /**
    * @deprecated Use Money Class and Corresponding MoneyEditor instead
    */
   public AtomicEditor getUSDollarEditor();

   public AtomicRenderer getMoneyRenderer();
   public AtomicEditor getMoneyEditor();

   public AtomicRenderer getUSZipRenderer();
   public AtomicEditor getUSZipEditor();

   public AtomicRenderer getUSPhoneRenderer();
   public AtomicEditor getUSPhoneEditor();

   public AtomicRenderer getSSNRenderer();
   public AtomicEditor getSSNEditor();
   
   public AtomicRenderer getColorRenderer();
   public AtomicEditor getColorEditor();

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
   public ListEView getListViewAsCalendar(CalEventList list);
   
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
   
   public EView getInstructionView(Instruction instruction);

   public void setEditable(Editor editor, boolean editable);

   public EView getCommandView(Command cmd, EObject eo);
   
   
}
