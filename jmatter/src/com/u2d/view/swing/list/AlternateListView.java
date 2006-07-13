/*
 * Created on Mar 31, 2005
 */
package com.u2d.view.swing.list;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListDataEvent;
import com.u2d.model.AbstractListEO;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.ui.CardPanel;
import com.u2d.ui.desktop.CloseableJInternalFrame;
import com.u2d.ui.IconButton;
import com.u2d.view.*;
import com.u2d.view.ViewInfo;
import com.u2d.view.swing.CommandAdapter;
import com.u2d.element.Command;

/**
 * @author Eitan Suez
 */
public class AlternateListView extends JPanel
                               implements ListEView, CompositeView
{
   protected AbstractListEO _leo;

   private String[] _viewNames;
   private JPanel _controlPane;
   private CardPanel _viewPane;
   private Map _map = new HashMap();
   private JPanel _pickPane;

   public AlternateListView(AbstractListEO leo, String[] viewNames)
   {
      _leo = leo;
      _viewNames = viewNames;

      buildControlPane();
      buildViewPane();
      buildPickPane();

      setLayout(new BorderLayout());
      setOpaque(false);
      add(_controlPane, BorderLayout.NORTH);
      add(_viewPane, BorderLayout.CENTER);
      add(_pickPane, BorderLayout.SOUTH);

      _pickPane.setVisible(false);
      stateChanged(null);  // setup initial state..
      _leo.addChangeListener(this);
   }

   private void buildControlPane()
   {
      _controlPane = new JPanel();
      _controlPane.setOpaque(false);
      _controlPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
      Icon icon, rolloverIcon;
      for (int i=0; i<_viewNames.length; i++)
      {
         icon = icon(_viewNames[i]);
         rolloverIcon = rolloverIcon(_viewNames[i]);
         _controlPane.add(button(icon, rolloverIcon, _viewNames[i]));
      }
   }

   private void buildViewPane()
   {
      _viewPane = new CardPanel();
      show(_viewNames[0]);
   }

   public JPanel getControlPane() { return _controlPane; }
   public JPanel getViewPane() { return _viewPane; }

   private void show(String viewName)
   {
      JComponent view = null;
      if (_map.get(viewName) == null)
      {
         view = view(viewName);

         if (view instanceof CompositeView)
         {
            _viewPane.add(view, viewName);
         }
         else
         {
            JScrollPane scrollPane = new JScrollPane(view);
            _viewPane.add(scrollPane, viewName);
         }

         _map.put(viewName, view);
      }
      _viewPane.show(viewName);
      CloseableJInternalFrame.updateSize(this);
   }

   private JButton button(Icon icon, Icon rolloverIcon, final String viewName)
   {
      JButton btn = new IconButton(icon, rolloverIcon);
      btn.addActionListener(new ActionListener()
            {
               public void actionPerformed(ActionEvent evt)
               {
                  show(viewName);
               }
            });
      return btn;
   }

   private Icon icon(String viewName)
   {
      return ViewInfo.getListViewInfo(viewName).getIcon();
   }
   private Icon rolloverIcon(String viewName)
   {
      return ViewInfo.getListViewInfo(viewName).getRolloverIcon();
   }

   private JComponent view(String viewName)
   {
      return (JComponent) ViewInfo.getListViewByName(viewName, _leo);
   }



   public EView getInnerView()
   {
      return (EView) _map.get(_viewPane.getCurrentCardName());
   }


   public EObject getEObject() { return _leo; }
   public void detach()
   {
      Iterator itr = _map.keySet().iterator();
      EView view = null;
      Object key = null;
      while (itr.hasNext())
      {
         key = itr.next();
         view = (EView) _map.get(key);
         view.detach();
      }

      _leo.removeChangeListener(this);
   }

   public void contentsChanged(ListDataEvent e) {}
   public void intervalAdded(ListDataEvent e) {}
   public void intervalRemoved(ListDataEvent e) {}

   public void stateChanged(ChangeEvent e)
   {
      _pickPane.setVisible(_leo.isPickState());
   }


   private void buildPickPane()
   {
      _pickPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      if (_leo.command("New") != null)
         _pickPane.add(newBtn());
      _pickPane.add(pickBtn());
   }
   private JButton newBtn()
   {
      Command cmd = _leo.command("New");
      CommandAdapter action = new CommandAdapter(cmd, _leo, this);
      return new JButton(action);
   }
   private JButton pickBtn()
   {
      JButton pickBtn = new JButton("Pick");
      pickBtn.setMnemonic('p');
      pickBtn.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent evt)
            {
               JComponent view = (JComponent) getInnerView();
               if (!(view instanceof Selectable)) return;

               final ComplexEObject value = ((Selectable) view).selectedEO();
               if (value == null) return;

               new Thread()
               {
                  public void run()
                  {
                     _leo.pick(value);
                  }
               }.start();

               if (!_leo.isInContext())
                  CloseableJInternalFrame.close(AlternateListView.this);
            }
         });
      return pickBtn;
   }

   public boolean isMinimized() { return false; }

}
