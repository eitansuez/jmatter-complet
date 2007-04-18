/*
 * Created on Jan 26, 2004
 */
package com.u2d.view.swing;

import java.awt.*;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.text.JTextComponent;
import java.util.*;
import java.util.List;
import java.beans.*;
import com.u2d.element.Field;
import com.u2d.field.CompositeField;
import com.u2d.field.AggregateField;
import com.u2d.model.*;
import com.u2d.validation.ValidationNotifier;
import com.u2d.view.*;
import com.u2d.view.swing.list.TableView;
import com.u2d.view.swing.list.CompositeTableView;
import com.u2d.ui.CustomTabbedPane;
import com.u2d.app.Tracing;

/**
 * @author Eitan Suez
 */
public class FormView extends JPanel implements ComplexEView, Editor
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

      JPanel mainPane = new FormPane();

      FormLayout layout =
         new FormLayout("right:pref, 5px, left:pref:grow", "");

      DefaultFormBuilder builder = new DefaultFormBuilder(layout, mainPane);
      CellConstraints cc = new CellConstraints();

      layoutChildFields(_ceo, builder, cc);

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
            mainPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
            add(new JScrollPane(mainPane), BorderLayout.CENTER);
         }
      }
   }

   private void layoutChildFields(ComplexEObject ceo,
                                  DefaultFormBuilder builder, CellConstraints cc)
   {
      if ( (ceo.field() != null) && (ceo.field().isInterfaceType()) )
         ceo.setField(null, null);
      
      List fields = (_partialFieldList == null) ? ceo.childFields() : _partialFieldList;
      
      JComponent vPnl;
      Field field = null;
      for (Iterator itr = fields.iterator(); itr.hasNext(); )
      {
         field = (Field) itr.next();

         if ( field.hidden() || "createdOn".equals(field.name())
               || "status".equals(field.name()) )
            continue;

         EView view = field.getView(ceo);
         _childViews.add(view);

         JComponent comp = (JComponent) view;

         if ( field.isTabView() )
         {
            if (field.isAtomic())
            {
               tabbedPane().addTab(field.label(), comp);
            }
            else
            {
               tabbedPane().addTab(field.label(), field.fieldtype().iconSm(), comp);
            }
         }
         else if (field.isAggregate() && ((AggregateField) field).flattenIntoParent())
         {
            layoutChildFields((ComplexEObject) field.get(ceo), builder, cc);
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
            appendRow(builder, cc, caption, comp, vPnl);
         }
      }
   }

   private void appendRow(DefaultFormBuilder builder, CellConstraints cc,
                          JComponent caption, JComponent comp,
                          JComponent vPnl)
   {
      builder.appendRow("pref");
      builder.add(vPnl, cc.xy(3, builder.getRow()));
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
      if (comp instanceof TableView || comp instanceof CompositeTableView)
      {
         builder.appendRow("pref");
         builder.add(caption, cc.xyw(1, builder.getRow(), 3));
         builder.nextLine();

         builder.appendRow("pref");
         if (comp instanceof JTable)
         {
            builder.add(new JScrollPane(comp), cc.xyw(1, builder.getRow(), 3));
         }
         else
         {
            builder.add(comp, cc.xyw(1, builder.getRow(), 3));
         }
         builder.nextLine();
      }
      else
      {
         builder.appendRow("pref");
         builder.add(caption, cc.xy(1, builder.getRow()));
         builder.add(comp, cc.xy(3, builder.getRow()));
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
               new Thread()
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
               }.start();
            }
         });
      }
      return _tabbedPane;
   }

   public int transferValue()
   {
      int count = 0;
      for (Iterator itr = _childViews.iterator(); itr.hasNext(); )
      {
         EView view = (EView) itr.next();
         Tracing.tracer().fine("attempting to transfer value for field "+view.getEObject().field());
         if (view instanceof Editor)
         {
            Field field = view.getEObject().field();

            if (field.hidden()) continue;

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
   
   public int validateValue()
   {
      if (_partialFieldList == null)
      {
         return _ceo.validate();
      }
      // else..
      int errorCount = 0;
      for (int i=0; i<_partialFieldList.size(); i++)
      {
         errorCount += _partialFieldList.get(i).validate(_ceo);
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

            if (field.hidden()) continue;

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

   public void focusFirstEditableField()
   {
      focusFirstEditableField(FormView.this);
   }
   private boolean focusFirstEditableField(Container container)
   {
      for (int i=0; i<container.getComponentCount(); i++)
      {
         Component c = container.getComponent(i);
         if (c instanceof JTextComponent)
         {
            JTextComponent textC = (JTextComponent) c;
//            System.out.println("Requesting focus for: "+textC.getClass().getName());
            textC.requestFocusInWindow();
            textC.selectAll();
            return true;
         }
         else if (c instanceof JTabbedPane)
         {
            JTabbedPane tp = (JTabbedPane) c;
            Container tabContents = (Container) tp.getSelectedComponent();
            return focusFirstEditableField(tabContents);
         }
         else if (c instanceof Container)
         {
            boolean done = focusFirstEditableField((Container) c);
            if (done) return done;
         }
      }
      return false;
   }
}
