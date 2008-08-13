package com.u2d.view.swing.list;

import com.u2d.view.ListEView;
import com.u2d.view.View;
import com.u2d.view.swing.calendar.NavPanel;
import com.u2d.view.swing.find.FindPanel;
import com.u2d.model.AbstractListEO;
import com.u2d.model.ComplexEObject;
import com.u2d.list.CriteriaListEO;
import com.u2d.list.Paginable;
import com.u2d.find.QueryReceiver;
import com.u2d.find.CompositeQuery;
import com.u2d.ui.IconButton;
import com.u2d.ui.desktop.CloseableJInternalFrame;
import com.u2d.field.Association;
import com.u2d.field.IndexedField;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

/**
 * A view used for performing associations to list (indexed) fields.
 * 
 * For example:  say a person wants to register for many events
 * (1-many or many-many).  There will be a person.events association.
 * 
 * Will be able to specify which events to register to in a single
 * GUI operation:  select "browse" and this pick view will appear.
 * 
 * lhs: the target list (events)
 * rhs: a mechanism for browsing/searching events to add to my list
 * 
 * uses the typical "double list" user interface design pattern
 * (see http://www.cs.helsinki.fi/u/salaakso/patterns/Double-List.html )
 */
public class MultiPickView extends JPanel implements View
{
   private AbstractListEO _leo;
   private JListView _optionsView;

   public MultiPickView(AbstractListEO leo)
   {
      _leo = leo;
      ListEView listView = (ListEView) _leo.getMainView();

      JPanel middlePane = new JPanel(); // lay it out such that items are vertically centered
      JButton btn = new IconButton(NavPanel.PREV_ICON, NavPanel.PREV_ROLLOVER);
      middlePane.add(btn);
      btn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            Object[] objects = _optionsView.getSelectedValues();
            java.util.List list = new ArrayList();
            for (int i=0; i<objects.length; i++)
            {
               list.add(objects[i]);
            }

            list.addAll(_leo.getItems());
            Association association = _leo.parentObject().association(_leo.field().name());
            association.associateList(list);
         }
      });


      AbstractListEO optionsList;
      
      ComplexEObject parentObject = _leo.parentObject();
      IndexedField listField = (IndexedField) _leo.field();
      if (listField.hasListAssociationConstraint() && listField.associationOptions(parentObject) != null)
      {
         optionsList = listField.associationOptions(parentObject);
      }
      else
      {
         CompositeQuery query = new CompositeQuery(_leo.type());
         listField.bindConstraintTo(query, parentObject);  // may not bind anything

         optionsList = new CriteriaListEO(query);
      }

      _optionsView = new JListView(optionsList);
      _optionsView.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

      JPanel container = new JPanel(new BorderLayout());
      if (!listField.hasAssociationConstraint())
      {
         container.add(new FindPanel((QueryReceiver) optionsList), BorderLayout.NORTH);
      }
      if (optionsList instanceof Paginable)
      {
         container.add(new PaginableView(_optionsView), BorderLayout.CENTER);
      }
      else
      {
         container.add(new JScrollPane(_optionsView), BorderLayout.CENTER);
      }

      FormLayout formLayout = new FormLayout("fill:pref, center:pref, fill:pref", "fill:pref, pref");
      DefaultFormBuilder builder = new DefaultFormBuilder(formLayout, this);
      CellConstraints cc = new CellConstraints();

      builder.add((JComponent) listView, cc.xy(1, 1, "fill, fill"));
      builder.add(middlePane, cc.xy(2, 1, "center, center"));
      builder.add(container, cc.xy(3, 1, "fill, fill"));

      JButton doneBtn = new JButton("Done");
      doneBtn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            CloseableJInternalFrame.close(MultiPickView.this);
         }
      });
      builder.add(doneBtn, cc.xyw(1, 2, 3));
   }

   public String getTitle() { return "List Picker"; }
   public Icon iconSm() { return _leo.iconSm(); }
   public Icon iconLg() { return _leo.iconLg(); }
   public boolean withTitlePane() { return false; }
}
