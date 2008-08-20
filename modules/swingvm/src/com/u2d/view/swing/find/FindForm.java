/*
 * Created on Nov 3, 2003
 */
package com.u2d.view.swing.find;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.util.*;
import com.u2d.find.CompositeQuery;
import com.u2d.list.CriteriaListEO;
import com.u2d.model.ComplexType;
import com.u2d.ui.IconButton;

/**
 * @author Eitan Suez
 */
public class FindForm extends JPanel
{
   private ComplexType _type;
   
   private java.util.List<FieldFilter> _filters;
   private JPanel _mainPnl;
//   private ButtonGroup _radios;
   private PanelBuilder _builder;
   private CellConstraints _cc;

   
	public FindForm(ComplexType type)
	{
      _type = type;
		
		setLayout(new BorderLayout());
		
//      add(radiosPnl(), BorderLayout.PAGE_START);
		
      _mainPnl = new JPanel();
      _mainPnl.setBorder(BorderFactory.createEtchedBorder());
      FormLayout layout = new FormLayout("pref, 10px, pref, 5px, pref", "");
      _cc = new CellConstraints();
      _builder = new PanelBuilder(layout, _mainPnl);
      add(_mainPnl, BorderLayout.CENTER);
      
      _filters = new ArrayList<FieldFilter>();
      addFilter();
	}
   
   
//   private JPanel radiosPnl()
//   {
//      JPanel topPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
//      _radios = new ButtonGroup();
//      JRadioButton andBtn = new JRadioButton("and", true);  andBtn.setActionCommand("and");
//      JRadioButton orBtn = new JRadioButton("or", false);  orBtn.setActionCommand("or");
//      topPnl.add(andBtn);  _radios.add(andBtn);
//      topPnl.add(orBtn);  _radios.add(orBtn);
//      return topPnl;
//   }
	
   
   private void addFilter()
   {
      _filters.add(new FieldFilter(_type));
      update();
   }
   private void removeFilter(FieldFilter filter)
   {
      _filters.remove(filter);
      update();
   }

   
   private void update()
   {
      _mainPnl.removeAll();
      FieldFilter filter = null;
      for (int i=0; i<_filters.size(); i++)
      {
         //_mainPnl.add((JComponent) _filters.get(i));
         _builder.appendRow("pref");
         filter = (FieldFilter) _filters.get(i);
         _builder.add(filter, _cc.xy(1, _builder.getRow()));
         _builder.add(addConstraintBtn(), _cc.xy(3, _builder.getRow()));
         _builder.add(removeConstraintBtn(filter), _cc.xy(5, _builder.getRow()));
         _builder.nextLine();
      }
      com.u2d.ui.desktop.CloseableJInternalFrame.updateSize(FindForm.this);
   }
   
    private JButton addConstraintBtn()
    {
       JButton addBtn = new IconButton(QueryView.ADD_ICON, QueryView.ADD_ROLLOVER);
       addBtn.setToolTipText("Add a constraint");
       addBtn.addActionListener(new ActionListener()
       {
          public void actionPerformed(ActionEvent evt)
          {
             addFilter();
          }
       });
       return addBtn;
    }
    private JButton removeConstraintBtn(final FieldFilter filter)
    {
       JButton btn = new IconButton(QueryView.DEL_ICON, QueryView.DEL_ROLLOVER);
       btn.setToolTipText("Remove constraint");
       btn.addActionListener(new ActionListener()
       {
          public void actionPerformed(ActionEvent evt)
          {
             removeFilter(filter);
             if (_filters.size() == 0) addFilter();
          }
       });
       return btn;
    }
   
   public CompositeQuery query()
   {
      CompositeQuery query = new CompositeQuery(_type);

      for (FieldFilter filter : _filters)
      {
         if (filter.isTypeNarrowing())
         {
            query.setQueryType(filter.narrowedType());
         }
         else
         {
            query.addSpecification(filter.getSpec());
         }
      }
      return query;
   }
   
   public CriteriaListEO doFind()
   {
      return query().Execute(null);
   }
	
   
}
