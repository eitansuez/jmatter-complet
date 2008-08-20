package com.u2d.view.swing.list;

import com.u2d.list.CompositeList;
import com.u2d.element.Field;
import com.u2d.element.Command;
import com.u2d.type.atom.BooleanEO;
import com.u2d.view.EView;
import com.u2d.view.ListEView;
import com.u2d.view.swing.CommandAdapter;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.model.Editor;
import com.u2d.css4swing.style.ComponentStyle;
import com.u2d.app.Tracing;
import com.u2d.field.CompositeField;
import com.u2d.ui.IconButton;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListDataEvent;
import java.util.List;
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
   private DefaultFormBuilder _builder;
   private IconButton _addBtn;
   private List _typefields;

   public CompositeTabularView(CompositeList leo)
   {
      _leo = leo;
      _typefields = _leo.type().fields();
      _leo.parentObject().addChangeListener(this);

      setOpaque(false);
      layMeOut();

      bind();
   }

   // define layout dynamically:
   private void layMeOut()
   {
      setLayout(new BorderLayout());

      _childViews = new ArrayList<EView>();

      FormLayout layout = generateLayout();
      _builder = new DefaultFormBuilder(layout);

      _builder.appendSeparator();  // line at top

      // first row is table column headings
      for (int i=0; i< _typefields.size(); i++)
      {
         Field field = (Field) _typefields.get(i);

         if ( field.hidden() || "createdOn".equals(field.name()) || "status".equals(field.name()) )
            continue;

         Class cls = field.getJavaClass();
         String label = field.label();
         if (cls.equals(BooleanEO.class)) label = label + "?";
         JLabel fieldLabel = new JLabel(label);
         ComponentStyle.addClass(fieldLabel, "tabular-heading");
         _builder.append(fieldLabel);
      }
      _builder.nextLine();
      _builder.appendSeparator();  // line separating header row from content rows

      // now the list item rows..
      for (int row=0; row<_leo.getSize(); row++)
      {
         ComplexEObject eo = (ComplexEObject) _leo.get(row);
         addRowForObject(eo, true);
      }

      _builder.appendSeparator();  // bottom line separator

      JPanel panel = _builder.getPanel();
      panel.setOpaque(false);
      add(panel, BorderLayout.CENTER);
      add(btnPanel(), BorderLayout.PAGE_END);
   }

   private void addRowForObject(ComplexEObject eo, boolean initialLayout)
   {
      int row = 0;
      if (!initialLayout)
      {
         row = _builder.getRowCount();
         _builder.getLayout().insertRow(row, new RowSpec("pref"));
         _builder.getLayout().insertRow(row+1, new RowSpec("3dlu"));
      }

      for (int col=0; col<_typefields.size(); col++)
      {
         Field field = (Field) _typefields.get(col);

         if ( field.hidden() || "createdOn".equals(field.name()) || "status".equals(field.name()) )
            continue;

         EView view = field.getView(eo);
         assert(view != null);
         _childViews.add(view);
         if (initialLayout)
         {
            _builder.append((JComponent) view);
         }
         else
         {
            _builder.add((JComponent) view, cc.rc(row, (col+1)*2 - 1));
         }
      }

      _builder.nextLine(2);
   }
   private CellConstraints cc = new CellConstraints();

   private JPanel btnPanel()
   {
      JPanel pnl = new JPanel(new FlowLayout(FlowLayout.TRAILING));
      pnl.setOpaque(false);
      _addBtn = new IconButton(EditableListView.ADD_ICON, EditableListView.ADD_ROLLOVER);
      Command command = _leo.command("AddItem");
      CommandAdapter commandAdapter = new CommandAdapter(command, _leo, this);
      _addBtn.addActionListener(commandAdapter);  // want only actionPerformed(), not other features of action.
      _addBtn.setEnabled(_leo.parentObject().isEditableState());
      pnl.add(_addBtn);
      return pnl;
   }

   private FormLayout generateLayout()
   {
      // first, the columns:
      FormLayout layout = new FormLayout();
      for (int i=0; i<_typefields.size(); i++)
      {
         Field field = (Field) _typefields.get(i);

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
   private void bind()
   {
      _leo.addListDataListener(this);
   }

   public void detach()
   {
      _leo.removeListDataListener(this);
      if (_leo.parentObject() != null)
      {
         _leo.parentObject().removeChangeListener(this);
      }
      for (EView view : _childViews)
      {
         view.detach();
      }
   }

   public void stateChanged(ChangeEvent e)
   {
      if (_leo.parentObject() != null)
      {
         _addBtn.setEnabled(_leo.parentObject().isEditableState());
      }
   }

   public void intervalAdded(ListDataEvent e)
   {
      int num = e.getIndex1() - e.getIndex0();
      for (int i=0; i<=num; i++)
      {
         ComplexEObject eo = (ComplexEObject) _leo.getElementAt(e.getIndex1() + i);
         addRowForObject(eo, false);
      }
   }

   public void intervalRemoved(ListDataEvent e) { }
   public void contentsChanged(ListDataEvent e) { }

   public boolean isMinimized() { return false; }


   public int transferValue()
   {
      int count = 0;
      for (EView view : _childViews)
      {
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

      for (EView view : _childViews)
      {
         if (view instanceof Editor)
         {
            EObject eo = view.getEObject();
            if (eo == null) return;
            // explanation of the above:  on cancel, it's possible to receive
            // setEditable while detachment is going on, in which case eo
            // will be detached from the view and will be null.  in this case
            // just ignore the setEditable message altogether
            // (the view is likely to be disposed soon anyway)

            Field field = eo.field();

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
