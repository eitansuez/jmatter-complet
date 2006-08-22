package com.u2d.view.wings;

import com.u2d.view.ComplexEView;
import com.u2d.view.EView;
import com.u2d.model.Editor;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.element.Field;
import com.u2d.field.AggregateField;
import com.u2d.field.CompositeField;
import com.u2d.validation.ValidationNotifier;
import com.u2d.app.Tracing;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashSet;
import java.beans.PropertyChangeEvent;
import org.wings.*;
import org.wings.border.SEmptyBorder;
import javax.swing.ImageIcon;
import java.awt.GridBagConstraints;

/**
 * @author Eitan Suez
 */
public class FormView extends SPanel implements ComplexEView, Editor
{
   private ComplexEObject _ceo;
   private boolean _leafContext;

   private java.util.List _childViews;  // exclude read-only fields
   private java.util.Collection _vPnls;

   private boolean _hasTabs = false;
   private boolean _editable = false;

   private List<Field> _partialFieldList = null;


   public FormView(ComplexEObject ceo)
   {
      this(ceo, false);
   }
   public FormView(ComplexEObject ceo, boolean isLeafContext)
   {
      super();
      _leafContext = isLeafContext;
      attach(ceo);
      stateChanged(null);
   }

   public FormView(ComplexEObject ceo, List<Field> partialFieldList)
   {
      _partialFieldList = partialFieldList;
      _leafContext = false;
      attach(ceo);
      stateChanged(null);
   }

   private void attach(ComplexEObject ceo)
   {
      _ceo = ceo;
      _ceo.addPropertyChangeListener(this);
      _ceo.addChangeListener(this);
      layItOut();
   }

   public void detach()
   {
      _ceo.removePropertyChangeListener(this);
      _ceo.removeChangeListener(this);

      stopListeningForValidations();

      Iterator itr = _childViews.iterator();
      EView view = null;
      while (itr.hasNext())
      {
         view = (EView) itr.next();
         view.detach();
      }
   }

   private void layItOut()
   {
      _childViews = new ArrayList();
      _vPnls = new HashSet();

      SPanel mainPane = new SPanel();
      mainPane.setLayout(new SGridBagLayout());
      GridBagConstraints cc = new GridBagConstraints();
      cc.gridy = 0;
      
      layoutChildFields(_ceo, mainPane, cc);

      setLayout(new SBorderLayout());
      // avoid using a tabbedPane altogether unless necessary (> 1 tab is actually used)
      if (tabbedPane().getTabCount() > 0)
      {
         _hasTabs = true;
         mainPane.setBorder(new SEmptyBorder(5,5,5,5));
         tabbedPane().insertTab("Main", null, mainPane, "", 0);
         add(tabbedPane(), SBorderLayout.CENTER);
      }
      else
      {
         // a formview within an expandable tree, does not require a scrollpane
         if (_leafContext)
         {
            add(mainPane, SBorderLayout.CENTER);
         }
         else
         {
            mainPane.setBorder( new SEmptyBorder(5,5,5,5));
            add(mainPane, SBorderLayout.CENTER);
         }
      }
   }

   private void layoutChildFields(ComplexEObject ceo, SPanel parentPanel, GridBagConstraints cc)
   {
      if ((ceo.field() != null) && (ceo.field().isInterfaceType()) )
         ceo.setField(null, null);

      List fields = _partialFieldList;
      if (fields == null)
      {
         fields =  ceo.childFields();
      }

      SComponent vPnl;
      Field field = null;
      for (Iterator itr = fields.iterator(); itr.hasNext(); )
      {
         field = (Field) itr.next();

         if ( field.isHidden() || "createdOn".equals(field.name())
               || "status".equals(field.name()) )
            continue;

         EView view = field.getView(ceo);
         _childViews.add(view);

         SComponent comp = (SComponent) view;

         if ( field.isTabView() )
         {
            if (field.isAtomic())
            {
               tabbedPane().addTab(field.label(), comp);
            }
            else
            {
               SIcon icon = new SImageIcon((ImageIcon) field.fieldtype().iconSm());
               tabbedPane().addTab(field.label(), icon, comp);
            }
         }
         else if (field.isAggregate() && ((AggregateField) field).flattenIntoParent())
         {
            layoutChildFields((ComplexEObject) field.get(ceo), parentPanel, cc);
         }
         else
         {
            ValidationNotifier notifier = view.getEObject();
            if (field.isAssociation())
            {
               notifier = ceo.association(field.name());
            }
            vPnl = new ValidationNoticePanel(notifier, ceo);
            _vPnls.add(vPnl);

            FieldCaption caption = new FieldCaption(field, comp);
            appendRow(parentPanel, cc, caption, comp, vPnl);
         }
      }
   }

   private void appendRow(SPanel parentPanel, GridBagConstraints cc,
                          SComponent caption, SComponent comp,
                          SComponent vPnl)
   {
      cc.gridx = 0;
      cc.gridwidth = GridBagConstraints.REMAINDER;
      parentPanel.add(vPnl, cc);
      cc.gridy++;
      
      appendItem(parentPanel, cc, caption, comp);
      cc.gridy++;
   }

   private void appendItem(SPanel parentPanel, GridBagConstraints cc,
                           SComponent caption, SComponent comp)
   {
      // tableviews are wide.  save space by laying out caption and
      // component one below the other..
//      if (comp instanceof TableView || comp instanceof CompositeTableView)
//      {
//         builder.appendRow("pref");
//         builder.add(caption, cc.xyw(1, builder.getRow(), 3));
//         builder.nextLine();
//
//         builder.appendRow("pref");
//         builder.add(comp, cc.xyw(1, builder.getRow(), 3));
//         builder.nextLine();
//      }
//      else
//      {
      cc.gridx = 0;
      cc.gridwidth = 1;
      parentPanel.add(caption, cc);
      
      cc.gridx = 1;
      cc.gridwidth = GridBagConstraints.REMAINDER;
      parentPanel.add(comp, cc);
//      }
   }


   STabbedPane _tabbedPane = null;
   private STabbedPane tabbedPane()
   {
      if (_tabbedPane == null)
         _tabbedPane = new STabbedPane();
      return _tabbedPane;
   }

   public int transferValue()
   {
      Iterator itr = _childViews.iterator();

      EView view = null;
      Field field = null;
      int count = 0;
      while (itr.hasNext())
      {
         view = (EView) itr.next();
         Tracing.tracer().fine("attempting to transfer value for field "+view.getEObject().field());
         if (view instanceof Editor)
         {
            field = view.getEObject().field();

            if (field.isHidden()) continue;

            if (field.isComposite() && !field.isIndexed())
            {
               CompositeField cfield = ((CompositeField) field);
               if (cfield.isReadOnly() ||
                     ( cfield.isIdentity() && !_ceo.isTransientState() )
                  )
                  continue;
            }

            Tracing.tracer().fine("transferring value for field "+view.getEObject().field());
            count += ((Editor) view).transferValue();
         }
      }
      return count;
   }

   public void setEditable(boolean editable)
   {
      _editable = editable;

      if (_editable)
      {
         listenForValidations();
      }
      else
      {
         stopListeningForValidations();
         resetValidations();
      }

      EView view = null;
      Field field = null;
      EObject eo = null;

      for (Iterator itr = _childViews.iterator(); itr.hasNext(); )
      {
         view = (EView) itr.next();
         if (view instanceof Editor)
         {
            eo = view.getEObject();
            if (eo == null) return;
            // explanation of the above:  on cancel, it's possible to receive
            // setEditable while detachment is going on, in which case eo
            // will be detached from the view and will be null.  in this case
            // just ignore the setEditable message altogether
            // (the view is likely to be disposed soon anyway)

            field = eo.field();

            if (field.isHidden()) continue;

            if (field.isComposite() && editable && !field.isIndexed())
            {
               CompositeField cfield = ((CompositeField) field);
               if (cfield.isReadOnly() ||
                     ( cfield.isIdentity() && !_ceo.isTransientState() )
                  )
                  continue;
            }

            ((Editor) view).setEditable(_editable);
         }
      }
   }

   public void stateChanged(javax.swing.event.ChangeEvent evt)
   {
      setEditable(_ceo.isEditableState());
   }


   private void resetValidations()
   {
      Iterator itr = _vPnls.iterator();
      ValidationNoticePanel vPnl = null;
      while (itr.hasNext())
      {
         vPnl = (ValidationNoticePanel) itr.next();
         vPnl.reset();
      }
   }
   private void listenForValidations()
   {
      Iterator itr = _vPnls.iterator();
      ValidationNoticePanel vPnl = null;
      while (itr.hasNext())
      {
         vPnl = (ValidationNoticePanel) itr.next();
         vPnl.startListening();
      }
   }
   private void stopListeningForValidations()
   {
      Iterator itr = _vPnls.iterator();
      ValidationNoticePanel vPnl = null;
      while (itr.hasNext())
      {
         vPnl = (ValidationNoticePanel) itr.next();
         vPnl.stopListening();
      }
   }

   public boolean isEditable() { return _editable; }
   public boolean hasTabs() { return _hasTabs; }

   public EObject getEObject() { return _ceo; }
   public void propertyChange(final PropertyChangeEvent evt) {}

   public boolean isMinimized() { return false; }

}
