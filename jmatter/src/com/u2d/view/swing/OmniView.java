/*
 * Created on Apr 1, 2005
 */
package com.u2d.view.swing;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.TreePath;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.model.NullComplexEObject;
import com.u2d.view.ComplexEView;
import com.u2d.view.EView;
import com.u2d.ui.CardBuffer;

/**
 * @author Eitan Suez
 */
public class OmniView extends JSplitPane 
                          implements ComplexEView, TreeSelectionListener
{
   private ComplexEObject _ceo;
   private JTreeView _tree;
   private CardBuffer _cardBuffer;

   private JPanel _blankPanel = new JPanel() {
      public Dimension getMinimumSize() { return new Dimension(400,200); }
      public Dimension getPreferredSize() { return getMinimumSize(); }
   };

   public OmniView(ComplexEObject ceo)
   {
      _ceo = ceo;

      setOrientation(HORIZONTAL_SPLIT);
      setDividerSize(8);
      setOneTouchExpandable(true);
      setResizeWeight(0.3);
      
      _cardBuffer = new CardBuffer(_blankPanel);
      setRightComponent(_cardBuffer);
      
      _tree = new JTreeView(_ceo);
      JScrollPane scrollPane = new JScrollPane(_tree);
      setLeftComponent(scrollPane);
      
      // give treeview a minimum width..
      Dimension minimumSize = new Dimension(150, 150);
      scrollPane.setMinimumSize(minimumSize);

      _tree.getSelectionModel().addTreeSelectionListener(this);
      TreePath rootPath = new TreePath(_tree.getModel().getRoot());
      _tree.setSelectionPath(rootPath);
      
      // want splitpane to behave in such a way that 
      // when the user resizes it, that the new sizes become the 
      // preferred sizes
   }
   
   public void valueChanged(TreeSelectionEvent evt)
   {
      TreePath path = _tree.getSelectionPath();
      if (path == null)
      {
         switchInBlankPanel();
         return;
      }

      EObject neweo = (EObject) path.getLastPathComponent();
      if (neweo == null || neweo instanceof NullComplexEObject)
      {
         switchInBlankPanel();
         return;
      }
      
//      Component currentComp = _cardBuffer.getCurrentItem();
//      if (currentComp instanceof EView)
//      {
//         EView currentView = (EView) currentComp;
//         EObject currenteo = currentView.getEObject();
//         if ( ((ComplexEObject) currenteo).type().equals(
//               ((ComplexEObject) neweo).type()) )
//         {
//            currentView.detach();
//            currentView.bind(neweo);
//         }
//      }
      
      EView view = neweo.getMainView();
      // nested alternateviews in an omniview context can be
      // confusing so just plant innerview in there
      while (view instanceof AlternateView)
      {
         view = ((AlternateView) view).getInnerView();
      }
      Component previous = _cardBuffer.switchIn((JComponent) view);
      if (previous != null && previous instanceof EView)
         ((EView) previous).detach();
   }
   
   private void switchInBlankPanel()
   {
      Component previous = _cardBuffer.switchIn(_blankPanel);
      if (previous != null && previous instanceof EView)
         ((EView) previous).detach();
   }
   
   public void detach()
   {
      _tree.detach();
   }

   public EObject getEObject() { return _ceo; }

   public Dimension getMinimumSize() { return new Dimension(550, 300); }
   public Dimension getPreferredSize()
   {
      Dimension preferred = super.getPreferredSize();
      Dimension minimum = getMinimumSize();
      if (preferred.width < minimum.width)
      {
         return minimum;
      }
      return preferred;
   }
   
   public boolean isMinimized() { return false; }
   public void propertyChange(PropertyChangeEvent evt) {}
   public void stateChanged(ChangeEvent e) {}
   
}
