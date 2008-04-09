package com.u2d.view.swing.list;

import com.u2d.list.CompositeList;
import com.u2d.element.Field;
import com.u2d.type.atom.BooleanEO;
import com.u2d.view.EView;
import com.u2d.view.ListEView;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.model.Editor;
import com.u2d.css4swing.style.ComponentStyle;
import com.u2d.app.Tracing;
import com.u2d.field.CompositeField;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListDataEvent;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Mar 28, 2008
 * Time: 3:46:22 PM
 *
 * The idea here is to use a tabular layout but not a jtable.
 * i.e. use FormLayout to layout the rows and columns properly for a list
 *
 * Separately: this class is a good example of where java breaks down from a perspective of
 * refactoring.  I want to refactor the pattern of iterating over the fields, which is repeated
 * here three times, and in FormView.  A closure would make the implementation DRY, devoid of
 * cruft (like the creation of inner classes and interfaces).
 */
public class CompositeTabularView extends JPanel implements ListEView, Editor
{
   private CompositeList _leo;

   private List<EView> _childViews;  // exclude read-only fields

   public CompositeTabularView(CompositeList leo)
   {
      _leo = leo;
      _leo.parentObject().addChangeListener(this);

      setOpaque(false);
      layMeOut();
   }

   // define layout dynamically:
   private void layMeOut()
   {
      setLayout(new BorderLayout());
      _childViews = new ArrayList<EView>();

      List fields = _leo.type().fields();
      FormLayout layout = generateLayout(fields);

      DefaultFormBuilder builder = new DefaultFormBuilder(layout, this);

      builder.appendSeparator();  // line at top

      // first row is table column headings
      for (int i=0; i<fields.size(); i++)
      {
         Field field = (Field) fields.get(i);

         if ( field.hidden() || "createdOn".equals(field.name()) || "status".equals(field.name()) )
            continue;

         Class cls = field.getJavaClass();
         String label = field.label();
         if (cls.equals(BooleanEO.class)) label = label + "?";
         JLabel fieldLabel = new JLabel(label);
         ComponentStyle.addClass(fieldLabel, "tabular-heading");
         builder.append(fieldLabel);
      }
      builder.nextLine();
      builder.appendSeparator();  // line separating header row from content rows

      // now the list item rows..
      for (int row=0; row<_leo.getSize(); row++)
      {
         ComplexEObject eo = (ComplexEObject) _leo.get(row);

         for (int col=0; col<fields.size(); col++)
         {
            Field field = (Field) fields.get(col);

            if ( field.hidden() || "createdOn".equals(field.name()) || "status".equals(field.name()) )
               continue;

            EView view = field.getView(eo);
            assert(view != null);
            _childViews.add(view);
            builder.append((JComponent) view);
         }

         builder.nextLine(2);
      }

      builder.appendSeparator();  // bottom line separator
   }

   private FormLayout generateLayout(List fields)
   {
      // first, the columns:
      FormLayout layout = new FormLayout();
      for (int i=0; i<fields.size(); i++)
      {
         Field field = (Field) fields.get(i);

         if ( field.hidden() || "createdOn".equals(field.name()) || "status".equals(field.name()) )
            continue;

         layout.appendColumn(new ColumnSpec("pref"));
         layout.appendColumn(new ColumnSpec("5dlu"));
      }

      // next, the rows
      layout.appendRow(new RowSpec("3dlu")); // for line separator at top

      layout.appendRow(new RowSpec("pref"));  // header row
      layout.appendRow(new RowSpec("3dlu"));  // separator below header row

      for (int i=0; i <_leo.getSize(); i++)
      {
         layout.appendRow(new RowSpec("pref"));
         layout.appendRow(new RowSpec("3dlu"));
      }
      layout.appendRow(new RowSpec("3dlu")); // for line separator at bottom
      return layout;
   }

   public EObject getEObject() { return _leo; }
   public void detach()
   {
      if (_leo.parentObject() != null)
      {
         _leo.parentObject().removeChangeListener(this);
      }
      for (Iterator<EView> itr = _childViews.iterator(); itr.hasNext(); )
      {
         EView view = itr.next();
         view.detach();
      }
   }

   public void stateChanged(ChangeEvent e) { }

   public void intervalAdded(ListDataEvent e) { }
   public void intervalRemoved(ListDataEvent e) { }
   public void contentsChanged(ListDataEvent e) { }

   public boolean isMinimized() { return false; }


   public int transferValue()
   {
      int count = 0;
      for (Iterator<EView> itr = _childViews.iterator(); itr.hasNext(); )
      {
         EView view = itr.next();
         Tracing.tracer().fine("attempting to transfer value for field "+view.getEObject().field());
         if (view instanceof Editor)
         {
            Field field = view.getEObject().field();

            if (field.hidden()) continue;

            if (field.isComposite() && !field.isIndexed())
            {
               CompositeField cfield = ((CompositeField) field);
               if (cfield.isReadOnly() ||
                     ( cfield.isIdentity() && !_leo.parentObject().isTransientState() )
                  )
                  continue;
            }

            Tracing.tracer().fine("transferring value for field "+view.getEObject().field());
            count += ((Editor) view).transferValue();
         }
      }
      return count;
   }

   private boolean _editable = false;
   public void setEditable(boolean editable)
   {
      _editable = editable;

      EView view = null;
      Field field = null;
      EObject eo = null;

      for (Iterator<EView> itr = _childViews.iterator(); itr.hasNext(); )
      {
         view = itr.next();
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
                     ( cfield.isIdentity() && !_leo.parentObject().isTransientState() )
                  )
                  continue;
            }

            ((Editor) view).setEditable(_editable);
         }
      }
   }

   public boolean isEditable() { return _editable; }
   public int validateValue() { return _leo.validate(); }
}
