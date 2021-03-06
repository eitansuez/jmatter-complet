/*
 * Created on Mar 31, 2005
 */
package com.u2d.view.swing.list;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListDataEvent;
import com.u2d.model.AbstractListEO;
import com.u2d.model.ComplexEObject;
import com.u2d.model.ComplexType;
import com.u2d.model.EObject;
import com.u2d.ui.CardPanel;
import com.u2d.ui.desktop.CloseableJInternalFrame;
import com.u2d.ui.IconButton;
import com.u2d.view.*;
import com.u2d.view.swing.CommandAdapter;
import com.u2d.view.swing.SwingViewMechanism;
import com.u2d.view.swing.SwingAction;
import com.u2d.view.swing.AppLoader;
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
   private Map<String, JComponent> _map = new HashMap<String, JComponent>();
   private JPanel _pickPane;

   public AlternateListView(AbstractListEO leo, String[] viewNames)
   {
      _leo = leo;
      _viewNames = viewNames;

      buildControlPane();
      buildViewPane();

      setLayout(new BorderLayout());
      setOpaque(false);
      add(_controlPane, BorderLayout.PAGE_START);
      add(_viewPane, BorderLayout.CENTER);

      stateChanged(null);  // setup initial state..
      _leo.addChangeListener(this);
   }

   private void buildControlPane()
   {
      _controlPane = new JPanel();
      _controlPane.setOpaque(false);
      _controlPane.setLayout(new FlowLayout(FlowLayout.TRAILING));
      Icon icon, rolloverIcon;
      for (String viewName : _viewNames)
      {
         icon = icon(viewName);
         rolloverIcon = rolloverIcon(viewName);
         _controlPane.add(button(icon, rolloverIcon, viewName));
      }
   }

   private void buildViewPane()
   {
      _viewPane = new CardPanel();
      show(firstViewName());
   }


   public void addNotify()
   {
      super.addNotify();
      AppLoader.getInstance().newThread(new Runnable()
      {
         public void run()
         {
            SwingUtilities.invokeLater(new Runnable()
            {
               public void run()
               {
                  cacheView(firstViewName()).requestFocusInWindow();
               }
            });
         }
      }).start();
   }
   
   private String firstViewName() { return _viewNames[0]; }


   public JPanel getControlPane() { return _controlPane; }
   public JPanel getViewPane() { return _viewPane; }

   private void show(String viewName)
   {
      cacheView(viewName);
      _viewPane.show(viewName);
      CloseableJInternalFrame.updateSize(this);
   }
   
   private JComponent cacheView(String viewName)
   {
      JComponent view = _map.get(viewName);
      if (view == null)
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
      return view;
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
      JComponent comp = (JComponent) ViewInfo.getListViewByName(viewName, _leo);
      comp.applyComponentOrientation(getComponentOrientation());
      return comp;
   }



   public EView getInnerView()
   {
      return (EView) _map.get(_viewPane.getCurrentCardName());
   }


   public EObject getEObject() { return _leo; }
   public void detach()
   {
      if (_pickPane != null)
      {
         for (int i=0; i<_pickPane.getComponentCount(); i++)
         {
            Component c = _pickPane.getComponent(i);
            if (c instanceof JButton)
            {
               Action action = ((JButton) c).getAction();
               if (action instanceof CommandAdapter)
               {
                  ((CommandAdapter) action).detach();
               }
            }
         }
      }
      for (String key : _map.keySet())
      {
         EView view = (EView) _map.get(key);
         view.detach();
      }

      _leo.removeChangeListener(this);
   }

   public void contentsChanged(ListDataEvent e) {}
   public void intervalAdded(ListDataEvent e) {}
   public void intervalRemoved(ListDataEvent e) {}

   public synchronized void stateChanged(ChangeEvent e)
   {
      if (_leo.isPickState() && _pickPane == null)
      {
         buildPickPane();
         add(_pickPane, BorderLayout.PAGE_END);
      }
      if (_pickPane != null)
      {
         _pickPane.setVisible(_leo.isPickState());
      }
   }


   private void buildPickPane()
   {
      _pickPane = new JPanel(new FlowLayout(FlowLayout.TRAILING));
      Command newCmd = _leo.command("New");
      Command typeNewCmd = _leo.type().command("New");
      if (newCmd != null && typeNewCmd!=null && !typeNewCmd.isForbidden(_leo.type()))
      {
         CommandAdapter action = new CommandAdapter(newCmd, _leo, this);
         _pickPane.add(new JButton(action));
      }
      _pickPane.add(pickBtn());
   }
   private JButton pickBtn()
   {
      JButton pickBtn = new JButton(ComplexType.localeLookupStatic("pick"));
      pickBtn.setMnemonic('p');
      pickBtn.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent evt)
            {
               JComponent view = (JComponent) getInnerView();
               if (!(view instanceof Selectable)) return;
               final ComplexEObject value = ((Selectable) view).selectedEO();
               if (value == null) return;

               SwingViewMechanism.invokeSwingAction(new SwingAction()
               {
                  public void offEDT() { _leo.pick(value); }

                  public void backOnEDT()
                  {
                     if (!_leo.isInContext())
                        CloseableJInternalFrame.close(AlternateListView.this);
                  }
               });
            }
         });
      return pickBtn;
   }

   public boolean isMinimized() { return false; }

}
