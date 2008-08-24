/*
 * Created on Nov 3, 2003
 */
package com.u2d.view.swing.find;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import com.u2d.find.CompositeQuery;
import com.u2d.list.CriteriaListEO;
import com.u2d.model.ComplexType;
import com.u2d.ui.IconButton;
import net.miginfocom.swing.MigLayout;

/**
 * @author Eitan Suez
 */
public class FindForm extends JPanel
{
   private ComplexType _type;
   
   private java.util.List<FieldFilter> _filters;
   private JPanel _mainPnl;
//   private ButtonGroup _radios;

   
	public FindForm(ComplexType type)
	{
      _type = type;
		
		setLayout(new BorderLayout());
		
//      add(radiosPnl(), BorderLayout.PAGE_START);
		
      _mainPnl = new JPanel();
      _mainPnl.setBorder(BorderFactory.createEtchedBorder());
      MigLayout layout = new MigLayout("insets 3, wrap 3");
      _mainPnl.setLayout(layout);
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
      for (FieldFilter filter : _filters)
      {
         _mainPnl.add(filter);
         _mainPnl.add(addConstraintBtn(), "gap unrel");
         _mainPnl.add(removeConstraintBtn(filter));
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
