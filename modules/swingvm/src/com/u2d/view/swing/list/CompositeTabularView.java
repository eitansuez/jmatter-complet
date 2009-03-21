package com.u2d.view.swing.list;

import com.u2d.list.CompositeList;
import com.u2d.element.Field;
import com.u2d.element.Command;
import com.u2d.type.atom.BooleanEO;
import com.u2d.type.atom.NumericEO;
import com.u2d.view.EView;
import com.u2d.view.ListEView;
import com.u2d.view.swing.CommandAdapter;
import com.u2d.model.*;
import com.u2d.css4swing.style.ComponentStyle;
import com.u2d.app.Tracing;
import com.u2d.field.CompositeField;
import com.u2d.ui.IconButton;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListDataEvent;
import java.util.List;
import java.util.ArrayList;
import java.awt.*;
import net.miginfocom.swing.MigLayout;
import net.miginfocom.layout.LC;
import net.miginfocom.layout.AC;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Mar 28, 2008
 * Time: 3:46:22 PM
 *
 * The idea here is to use a tabular layout but not a jtable.
 * i.e. layout the rows and columns properly like a grid.
 */
public class CompositeTabularView extends JPanel implements ListEView, Editor
{
   private CompositeList _leo;

   private List<EView> _childViews;  // exclude read-only fields
   private List _typefields;
   private JPanel _panel;
   private boolean _fixedList;
   private IconButton _addBtn;

   public CompositeTabularView(CompositeList leo)
   {
      this(leo, false);
   }
   public CompositeTabularView(CompositeList leo, boolean fixedList)
   {
      _leo = leo;
      _typefields = _leo.type().fields();
      _leo.parentObject().addChangeListener(this);

      _fixedList = fixedList;

      setOpaque(false);
      layMeOut();

      _leo.addListDataListener(this);
   }

   private void addSeparator()
   {
      _panel.add(new JSeparator(), "growx, span, wrap");
   }

   // define layout dynamically:
   private void layMeOut()
   {
      setLayout(new BorderLayout());

      _childViews = new ArrayList<EView>();

      MigLayout layout = generateLayout();
      _panel = new JPanel(layout);
      _panel.setOpaque(false);

      addSeparator();  // line at top

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
         _panel.add(fieldLabel, "align center");
      }
      addSeparator();  // underlining header row

      // now the list item rows..
      for (int row=0; row<_leo.getSize(); row++)
      {
         ComplexEObject eo = (ComplexEObject) _leo.get(row);
         addRowForObject(eo);
      }

      addSeparator();  // line below table

      add(_panel, BorderLayout.CENTER);
      if (!_fixedList)
      {
         add(btnPanel(), BorderLayout.PAGE_END);
      }
   }

   private void addRowForObject(ComplexEObject eo) { addRowForObject(eo, false); }

   private void addRowForObject(ComplexEObject eo, boolean afterTheFact)
   {
      if (afterTheFact)
      {
         _panel.remove(_panel.getComponentCount()-1);
      }

      for (int col=0; col<_typefields.size(); col++)
      {
         Field field = (Field) _typefields.get(col);

         if ( field.hidden() || "createdOn".equals(field.name()) || "status".equals(field.name()) )
            continue;

         EView view = field.getView(eo);
         assert(view != null);
         _childViews.add(view);
         _panel.add((JComponent) view);
      }

      if (afterTheFact)
      {
         addSeparator();
      }
   }

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

   private MigLayout generateLayout()
   {
      int numCols = 0;
      AC colConstraints = new AC();

      for (int i=0; i<_typefields.size(); i++)
      {
         Field field = (Field) _typefields.get(i);

         if ( field.hidden() || "createdOn".equals(field.name()) || "status".equals(field.name()) )
            continue;

         numCols++;
         String alignment = "leading";
         if (NumericEO.class.isAssignableFrom(field.getJavaClass()))
         {
            alignment = "trailing";
         }
         colConstraints.align(alignment).fill().gap();
      }

      LC constraints = new LC();
      constraints.wrapAfter(numCols).gridGapX("unrel").fillX();
      return new MigLayout(constraints, colConstraints);
   }

   public EObject getEObject() { return _leo; }

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
      if (_leo.parentObject() != null && !_fixedList)
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
         addRowForObject(eo, true);
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

            if ( editable && (eo instanceof AtomicEObject) && ((AtomicEObject) eo).isReadOnly() )
               continue;

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
