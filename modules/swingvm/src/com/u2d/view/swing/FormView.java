/*
 * Created on Jan 26, 2004
 */
package com.u2d.view.swing;

import com.u2d.app.Tracing;
import com.u2d.element.Field;
import com.u2d.field.AggregateField;
import com.u2d.field.CompositeField;
import com.u2d.model.ComplexEObject;
import com.u2d.model.ComplexType;
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
import java.util.*;
import java.util.List;
import net.miginfocom.swing.MigLayout;

/**
 * @author Eitan Suez
 */
public class FormView extends JXPanel implements IFormView
{
   private ComplexEObject _ceo;
   private boolean _leafContext;

   private Map<String, EView> _childViews;  // exclude read-only fields
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
   }

   public FormView(ComplexEObject ceo, List<Field> partialFieldList)
   {
      _partialFieldList = partialFieldList;
      _leafContext = false;
      attach(ceo);
   }

   private void attach(ComplexEObject ceo)
   {
      _ceo = ceo;
      _ceo.addPropertyChangeListener(this);
      _ceo.addChangeListener(this);
      layItOut();
      stateChanged(null);
   }

   public void detach()
   {
      _ceo.removePropertyChangeListener(this);
      _ceo.removeChangeListener(this);

      stopListeningForValidations();
      _vPnls.clear();

      for (EView view : _childViews.values())
      {
         view.detach();
      }
      _childViews.clear();

      for (FieldCaption c : _fieldCaptions)
      {
         c.detach();
      }
      _fieldCaptions.clear();

      if (_tabbedPane != null)
      {
         for (ChangeListener l : _tabbedPane.getChangeListeners())
         {
            _tabbedPane.removeChangeListener(l);
         }
      }
   }

   private void layItOut()
   {
      _childViews = new HashMap<String, EView>();
      _vPnls = new HashSet<ValidationNoticePanel>();

      JPanel mainPane = mainPane();

      setLayout(new BorderLayout());
      // avoid using a tabbedPane altogether unless necessary (> 1 tab is actually used)
      if (tabbedPane().getTabCount() > 0)
      {
         _hasTabs = true;
         mainPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
         tabbedPane().insertTab(_ceo.type().mainTabCaption(), null, new JScrollPane(mainPane), "", 0);
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
      MigLayout layout = new MigLayout("insets 0 5 0 5, wrap 2, gapy 2", "[trailing][grow]", "");
      FormPane formPane = new FormPane(layout);
      layoutChildFields(_ceo, formPane, hasCustomMainTabPanel);

      if (hasCustomMainTabPanel)
      {
         EView customMainTabPanel = _ceo.mainTabPanel();
         _childViews.put("mainTabPanel", customMainTabPanel);
         return (JPanel) customMainTabPanel;
      }
      else
      {
         return formPane;
      }
   }

   private void layoutChildFields(ComplexEObject ceo, JPanel container, boolean hasCustomMainTabPanel)
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
            _childViews.put(field.name(), view);
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
            _childViews.put(field.name(), view);
            JComponent comp = (JComponent) view;

            if (field.isAggregate() && ((AggregateField) field).flattenIntoParent())
            {
               layoutChildFields((ComplexEObject) field.get(ceo), container, hasCustomMainTabPanel);
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
               container.add(vPnl, "skip, wrap");
               appendItem(container, caption, comp);
            }
         }
      }
   }

   private void appendItem(JPanel container, JComponent caption, JComponent comp)
   {
      // tableviews are wide.  save space by laying out caption and
      // component one below the other..
      if (comp instanceof TableView || comp instanceof CompositeTableView || comp instanceof CompositeTabularView )
      {
         String constraints = "alignx left, growx, span, wrap";
         container.add(caption, constraints);
         if (comp instanceof JTable)
         {
            container.add(new JScrollPane(comp), constraints);
         }
         else
         {
            container.add(comp, constraints);
         }
      }
      else
      {
         container.add(caption);
         container.add(comp);
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
                           focusField();
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
      for (EView view : _childViews.values())
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

      for (EView view : _childViews.values())
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

   public void focusField()
   {
      if (_ceo.type().hasDefaultFocusField())
      {
         String fieldName = _ceo.type().defaultFocusField();
         UIUtils.focusFirstEditableField((Container) _childViews.get(fieldName));
      }
      else
      {
         UIUtils.focusFirstEditableField(FormView.this);
      }
   }
}
