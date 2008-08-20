/*
 * Created on Apr 25, 2005
 */
package com.u2d.view.swing.find;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import com.u2d.find.CompositeQuery;
import com.u2d.find.QuerySpecification;
import com.u2d.model.EObject;
import com.u2d.model.Editor;
import com.u2d.ui.desktop.CloseableJInternalFrame;
import com.u2d.ui.IconButton;
import com.u2d.view.ComplexEView;
import com.u2d.view.swing.FieldCaption;
import com.u2d.view.swing.list.CommandsButtonView;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Eitan Suez
 */
public class QueryView extends JPanel implements ComplexEView, Editor
{
   private CompositeQuery _query;
   private JPanel _mainPnl;
   private java.util.List _filters;
   private PanelBuilder _builder;
   private CellConstraints _cc;
   private transient CommandsButtonView _cmdsView;
   private JComponent _nameView;

   public QueryView(CompositeQuery query)
   {
      _query = query;
      stateChanged(null);
      _query.addChangeListener(this);
      _cmdsView = new CommandsButtonView();
      layItOut();
      bindIt();
      
      int numSpecs = _query.getQuerySpecifications().getSize();
      if (numSpecs == 0) addFilter();
   }
   
   private void bindIt()
   {
      _cmdsView.bind(_query, this, BorderLayout.LINE_END, this);
      
      QuerySpecification spec = null;
      _filters = new ArrayList();
      for (int i=0; i<_query.getQuerySpecifications().getSize(); i++)
      {
         spec = (QuerySpecification) _query.getQuerySpecifications().getElementAt(i);
         _filters.add(new FieldFilter(_query.getQueryType(), spec));
      }
      update();
   }
   
   private void layItOut()
   {
      setLayout(new BorderLayout());
      
      JPanel namePnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
      _nameView = (JComponent) _query.getName().getView();
      FieldCaption nameLabel = new FieldCaption(_query.getName().field(), _nameView);
      namePnl.add(nameLabel);
      namePnl.add(_nameView);
      add(namePnl, BorderLayout.PAGE_START);
      
      _mainPnl = new JPanel();
      _mainPnl.setBorder(BorderFactory.createEtchedBorder());
      
      FormLayout layout = new FormLayout("pref, 10px, pref, 5px, pref", "");
      _cc = new CellConstraints();
      _builder = new PanelBuilder(layout, _mainPnl);
      
      add(_mainPnl, BorderLayout.CENTER);
   }
   
   private void addFilter()
   {
      _filters.add(new FieldFilter(_query.getQueryType()));
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
      com.u2d.ui.desktop.CloseableJInternalFrame.updateSize(QueryView.this);
   }
   
   private JButton addConstraintBtn()
   {
      JButton addBtn = new IconButton(ADD_ICON, ADD_ROLLOVER);
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
      JButton delBtn = new IconButton(DEL_ICON, DEL_ROLLOVER);
      delBtn.setToolTipText("Remove constraint");
      delBtn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent evt)
         {
            removeFilter(filter);
            if (_filters.size() == 0) addFilter();
         }
      });
      return delBtn;
   }
   
   public boolean isMinimized() { return false; }
   
   public boolean isEditable() { return true; }
   public void setEditable(boolean editable)
   {
      if (_nameView != null)
         ((Editor) _nameView).setEditable(editable);
   }
   
   public int transferValue()
   {
      int count = ((Editor) _nameView).transferValue();
      
      // 1. clear list..
      _query.getQuerySpecifications().setItems(new ArrayList());
      
      // 2. set query specifications list
      FieldFilter filter = null;
      for (int i=0; i<_filters.size(); i++)
      {
         filter = (FieldFilter) _filters.get(i);
         _query.getQuerySpecifications().add(filter.getSpec());
      }
      
      return count;
   }

   public int validateValue() { return _query.validate(); }

   public EObject getEObject() { return _query; }
   
   public void propertyChange(PropertyChangeEvent evt) {}
   public void stateChanged(javax.swing.event.ChangeEvent evt)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            setEditable(_query.isEditableState());
            
            if (_cmdsView != null)
            {
               CloseableJInternalFrame.updateSize(QueryView.this);
            }
         }
      });
   }
   
   public void detach()
   {
      _query.removeChangeListener(this);
   }
   
   public static ImageIcon ADD_ICON, DEL_ICON, ADD_ROLLOVER, DEL_ROLLOVER;
   static
   {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      java.net.URL imgURL = loader.getResource("images/add.png");
      ADD_ICON = new ImageIcon(imgURL);
      imgURL = loader.getResource("images/add-hover.png");
      ADD_ROLLOVER = new ImageIcon(imgURL);
      imgURL = loader.getResource("images/delete.png");
      DEL_ICON = new ImageIcon(imgURL);
      imgURL = loader.getResource("images/delete-hover.png");
      DEL_ROLLOVER = new ImageIcon(imgURL);
   }

}
