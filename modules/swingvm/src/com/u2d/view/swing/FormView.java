/*
 * Created on Jan 26, 2004
 */
package com.u2d.view.swing;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.u2d.app.Tracing;
import com.u2d.element.Field;
import com.u2d.field.AggregateField;
import com.u2d.field.CompositeField;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.model.Editor;
import com.u2d.ui.CustomTabbedPane;
import com.u2d.ui.UIUtils;
import com.u2d.validation.ValidationNotifier;
import com.u2d.view.EView;
import com.u2d.view.swing.list.CompositeTableView;
import com.u2d.view.swing.list.TableView;
import com.u2d.view.swing.list.CompositeTabularView;
import org.jdesktop.swingx.JXPanel;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * @author Eitan Suez
 */
public class FormView extends JXPanel implements IFormView
{
   private ComplexEObject _ceo;
   private boolean _leafContext;

   private List<EView> _childViews;  // exclude read-only fields
   private java.util.Collection<ValidationNoticePanel> _vPnls;

   private boolean _hasTabs = false;
   private boolean _editable = false;

   private List<Field> _partialFieldList = null;
   private List<FieldCaption> _fieldCaptions = new ArrayList<FieldCaption>();


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

      for (EView view : _childViews)
      {
         view.detach();
      }

      for (FieldCaption c : _fieldCaptions)
      {
         c.detach();
      }
      _fieldCaptions.clear();
   }

   private void layItOut()
   {
      _childViews = new ArrayList<EView>();
      _vPnls = new HashSet<ValidationNoticePanel>();

      JPanel mainPane = mainPane();

      setLayout(new BorderLayout());
      // avoid using a tabbedPane altogether unless necessary (> 1 tab is actually used)
      if (tabbedPane().getTabCount() > 0)
      {
         _hasTabs = true;
         mainPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
         tabbedPane().insertTab("Main", null, new JScrollPane(mainPane), "", 0);
         add(tabbedPane(), BorderLayout.CENTER);
      }
      else
      {
         // a formview within an expandable tree, does not require a scrollpane
         if (_leafContext)
         {
            add(mainPane, BorderLayout.CENTER);
         }
         else
         {
//            mainPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
            add(new JScrollPane(mainPane), BorderLayout.CENTER);
         }
      }
   }
   
   private JPanel mainPane()
   {
      boolean hasCustomMainTabPanel = _ceo.hasCustomMainTabPanel();
      if (hasCustomMainTabPanel)
      {
         EView customMainTabPanel = _ceo.mainTabPanel();
         _childViews.add(customMainTabPanel);
         FormLayout layout = new FormLayout("right:pref, 5px, left:pref:grow", "");
         DefaultFormBuilder builder = new DefaultFormBuilder(layout, new FormPane());
         layoutChildFields(_ceo, builder, new CellConstraints(), hasCustomMainTabPanel);
         return (JPanel) customMainTabPanel;
      }
      else
      {
         FormLayout layout = new FormLayout("right:pref, 5px, left:pref:grow", "");
         DefaultFormBuilder builder = new DefaultFormBuilder(layout, new FormPane());
         layoutChildFields(_ceo, builder, new CellConstraints(), hasCustomMainTabPanel);
         return builder.getPanel();
      }
   }

   private void layoutChildFields(ComplexEObject ceo,
                                  DefaultFormBuilder builder, CellConstraints cc, boolean hasCustomMainTabPanel)
   {
      if ( (ceo.field() != null) && (ceo.field().isInterfaceType()) )
         ceo.setField(null, null);

      List fields = (_partialFieldList == null) ? ceo.childFields() : _partialFieldList;
      
      ValidationNoticePanel vPnl;
      Field field;
      for (Iterator itr = fields.iterator(); itr.hasNext(); )
      {
         field = (Field) itr.next();

         if ( field.hidden() || "createdOn".equals(field.name())
               || "status".equals(field.name()) )
            continue;

         if ( field.isTabView() )
         {
            EView view = field.getView(ceo);
            _childViews.add(view);
            JComponent comp = (JComponent) view;

            if (field.isAtomic())
            {
               tabbedPane().addTab(field.label(), comp);
            }
            else
            {
               // basically if object is in aggregate context (not a major entity)
               // and has no icon (falls back to default "?" icon), then default to not
               // rendering the icon at all, which is i believe a sensible default.
               if (field.isAggregate() && field.fieldtype().iconSmResourceRef().endsWith("Objects16.png"))
               {
                  tabbedPane().addTab(field.label(), comp);
               }
               else
               {
                  tabbedPane().addTab(field.label(), field.fieldtype().iconSm(), comp);
               }
            }
         }
         else if (!hasCustomMainTabPanel)
         {
            EView view = field.getView(ceo);
            _childViews.add(view);
            JComponent comp = (JComponent) view;

            if (field.isAggregate() && ((AggregateField) field).flattenIntoParent())
            {
               layoutChildFields((ComplexEObject) field.get(ceo), builder, cc, hasCustomMainTabPanel);
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
               _fieldCaptions.add(caption);
               appendRow(builder, cc, caption, comp, vPnl);
            }
         }
      }
   }

   private void appendRow(DefaultFormBuilder builder, CellConstraints cc,
                          JComponent caption, JComponent comp,
                          JComponent vPnl)
   {
      builder.appendRow("pref");
      builder.add(vPnl, cc.rc(builder.getRow(), 3));
      builder.nextLine();

      appendItem(builder, cc, caption, comp);

      builder.appendRow("3px"); // a vertical gap
      builder.nextLine();
   }

   private void appendItem(DefaultFormBuilder builder, CellConstraints cc,
                           JComponent caption, JComponent comp)
   {
      // tableviews are wide.  save space by laying out caption and
      // component one below the other..
      if (comp instanceof TableView || comp instanceof CompositeTableView || comp instanceof CompositeTabularView )
      {
         builder.appendRow("bottom:pref");
         builder.add(caption, cc.rcw(builder.getRow(), 1, 3));
         builder.nextLine();

         builder.appendRow("top:pref");
         if (comp instanceof JTable)
         {
            builder.add(new JScrollPane(comp), cc.rcw(builder.getRow(), 1, 3));
         }
         else
         {
            builder.add(comp, cc.rcw(builder.getRow(), 1, 3));
         }
         builder.nextLine();
      }
      else
      {
         builder.appendRow("pref");
         builder.add(caption, cc.rc(builder.getRow(), 1));
         builder.add(comp, cc.rc(builder.getRow(), 3));
         builder.nextLine();
      }
   }


   JTabbedPane _tabbedPane = null;
   private JTabbedPane tabbedPane()
   {
      if (_tabbedPane == null)
      {
         _tabbedPane = new CustomTabbedPane();
         _tabbedPane.addChangeListener(new ChangeListener()
         {
            public void stateChanged(ChangeEvent e)
            {
               // this sucks but it works (turns out to be known bug, posted
               //  against java1.4.2)
               AppLoader.getInstance().newThread(new Runnable()
               {
                  public void run()
                  {
                     SwingUtilities.invokeLater(new Runnable()
                     {
                        public void run()
                        {
                           focusFirstEditableField();
                        }
                     });
                  }
               }).start();
            }
         });
      }
      return _tabbedPane;
   }

   public int transferValue()
   {
      int count = 0;
      for (EView view : _childViews)
      {
         Tracing.tracer().fine("attempting to transfer value for field " + view.getEObject().field());
         if (view instanceof Editor)
         {
            Field field = view.getEObject().field();

            // comment: the field==null case is new;  i encounter it when customizing the
            //  main tab panel for a view, where the view is a partial view of the parent object,
            //  not of a specific child field.
            if (field == null || field.hidden()) continue;

            if (field.isComposite() && !field.isIndexed())
            {
               CompositeField cfield = ((CompositeField) field);
               if (cfield.isReadOnly() ||
                     ( cfield.isIdentity() && !_ceo.isTransientState() )
                  )
                  continue;
            }

            Tracing.tracer().fine("transferring value for field " + view.getEObject().field());
            count += ((Editor) view).transferValue();
         }
      }
      return count;
   }
   
   public int validateValue()
   {
      if (_partialFieldList == null)
      {
         return _ceo.validate();
      }
      // else..
      int errorCount = 0;
      for (Field field : _partialFieldList)
      {
         errorCount += field.validate(_ceo);
      }
      return errorCount;
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

      Field field;
      EObject eo;

      for (EView view : _childViews)
      {
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

            if (field != null && field.hidden()) continue;

            if (field != null && field.isComposite() && editable && !field.isIndexed())
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
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            setEditable(_ceo.isEditableState());
         }
      });
   }


   private void resetValidations()
   {
      for (ValidationNoticePanel vPnl : _vPnls)
      {
         vPnl.reset();
      }
   }
   private void listenForValidations()
   {
      for (ValidationNoticePanel vPnl : _vPnls)
      {
         vPnl.startListening();
      }
   }
   private void stopListeningForValidations()
   {
      for (ValidationNoticePanel vPnl : _vPnls)
      {
         vPnl.stopListening();
      }
   }

   public boolean isEditable() { return _editable; }
   public boolean hasTabs() { return _hasTabs; }

   public EObject getEObject() { return _ceo; }
   public void propertyChange(final PropertyChangeEvent evt) {}

   public boolean isMinimized() { return false; }

   public void focusFirstEditableField()
   {
      UIUtils.focusFirstEditableField(FormView.this);
   }
}
