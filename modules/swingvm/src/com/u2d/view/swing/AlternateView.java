/*
 * Created on Mar 31, 2005
 */
package com.u2d.view.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.model.Editor;
import com.u2d.ui.CardPanel;
import com.u2d.ui.desktop.CloseableJInternalFrame;
import com.u2d.ui.IconButton;
import com.u2d.view.ComplexEView;
import com.u2d.view.CompositeView;
import com.u2d.view.EView;
import com.u2d.view.ViewInfo;
import com.u2d.type.composite.Folder;

/**
 * @author Eitan Suez
 */
public class AlternateView extends JPanel
                           implements ComplexEView, CompositeView, Editor
{
   protected ComplexEObject _ceo;

   private String[] _viewNames;
   private JPanel _controlPane;
   private CardPanel _viewPane = new CardPanel();
   private Map _map = new HashMap();

   public AlternateView(ComplexEObject ceo, JComponent view, String[] viewNames)
   {
      addView(view, viewNames[0]);
      init(ceo, viewNames);
   }
   public AlternateView(ComplexEObject ceo, String[] viewNames)
   {
      init(ceo, viewNames);
   }

   private void init(ComplexEObject ceo, String[] viewNames)
   {
      _ceo = ceo;
      _viewNames = viewNames;

      buildControlPane();
      show(_viewNames[0]);

      setLayout(new BorderLayout());
      setOpaque(false);
      add(_controlPane, BorderLayout.PAGE_START);
      add(_viewPane, BorderLayout.CENTER);
   }

   private void buildControlPane()
   {
      _controlPane = new JPanel();
      _controlPane.setOpaque(false);
      _controlPane.setLayout(new FlowLayout(FlowLayout.TRAILING));
      Icon icon, rolloverIcon;
      for (int i=0; i<_viewNames.length; i++)
      {
         icon = icon(_viewNames[i]);
         rolloverIcon = rolloverIcon(_viewNames[i]);
         _controlPane.add(button(icon, rolloverIcon, _viewNames[i]));
      }
   }

   public JPanel getControlPane() { return _controlPane; }
   public JPanel getViewPane() { return _viewPane; }

   private synchronized void show(String viewName)
   {
      if (_map.get(viewName) == null)
      {
         addView(view(viewName), viewName);
      }
      _viewPane.show(viewName);
      CloseableJInternalFrame.updateSize(this);
   }

   private void addView(JComponent view, String viewName)
   {
      _viewPane.add(view, viewName);
      _map.put(viewName, view);
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
      return ViewInfo.getViewInfo(viewName).getIcon();
   }
   private Icon rolloverIcon(String viewName)
   {
      return ViewInfo.getViewInfo(viewName).getRolloverIcon();
   }

   private JComponent view(String viewName)
   {
      JComponent comp = (JComponent) ViewInfo.getViewByName(viewName, _ceo);
      comp.applyComponentOrientation(getComponentOrientation());
      return comp;
   }



   public EView getInnerView()
   {
      return (EView) _map.get(_viewPane.getCurrentCardName());
   }


   public EObject getEObject() { return _ceo; }
   public void detach()
   {
      for (Iterator itr = _map.keySet().iterator(); itr.hasNext(); )
      {
         Object key = itr.next();
         EView view = (EView) _map.get(key);
         view.detach();
      }
   }

   public void stateChanged(ChangeEvent e) {}

   public void propertyChange(PropertyChangeEvent evt) {}

   public boolean isMinimized() { return false; }


   public boolean isEditable()
   {
      EView currentView = getInnerView();
      if (currentView instanceof Editor)
      {
         Editor editor = (Editor) currentView;
         return editor.isEditable();
      }
      return false;
   }
   public void setEditable(boolean editable)
   {
      EView currentView = getInnerView();
      if (currentView instanceof Editor)
      {
         Editor editor = (Editor) currentView;
         editor.setEditable(editable);
      }

      // default/switch to formview when editing
      // and default to folderview when in read only state
      if ( Folder.class.equals(_ceo.type().getJavaClass()) )
      {
         show ( (editable) ? "formview" : "folderview" );
      }
   }
   public int transferValue()
   {
      EView currentView = getInnerView();
      if (currentView instanceof Editor)
      {
         Editor editor = (Editor) currentView;
         return editor.transferValue();
      }
      return 0;
   }

   public int validateValue()
   {
      // a higher order-function would do nice to refactor these two methods.. 
      EView currentView = getInnerView();
      if (currentView instanceof Editor)
      {
         Editor editor = (Editor) currentView;
         return editor.validateValue();
      }
      return 0;
   }
}

