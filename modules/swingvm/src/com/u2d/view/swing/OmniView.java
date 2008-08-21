/*
 * Created on Apr 1, 2005
 */
package com.u2d.view.swing;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.util.Map;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.TreePath;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.model.NullComplexEObject;
import com.u2d.model.Editor;
import com.u2d.view.ComplexEView;
import com.u2d.view.EView;
import com.u2d.view.CompositeView;
import com.u2d.ui.CardBuffer;

/**
 * 
 * Separately:  consider making omniview a compositeview with a getInnerView() impl.
 *
 * @author Eitan Suez
 */
public class OmniView extends LTRCapableJSplitPane
                      implements ComplexEView, TreeSelectionListener, CompositeView
{
   private ComplexEObject _ceo;
   private JTreeView _tree;
   private CardBuffer _cardBuffer;
   private TreeNodeAdapter _selected;

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
         _cardBuffer.switchIn(_blankPanel);
         return;
      }
     
      EObject neweo = leaveSelectedNode(path);
   
      if (neweo == null || neweo instanceof NullComplexEObject)
      {
         _cardBuffer.switchIn(_blankPanel);
         return;
      }

      EView view = getViewFor(neweo);
      _cardBuffer.switchIn((JComponent) view);
   }

   private EObject leaveSelectedNode(TreePath path)
   {
      TreeNodeAdapter nNode = (TreeNodeAdapter) path.getLastPathComponent();
      EObject neweo = nNode.getEObject();

      if ((_selected != null) && (nNode != _selected))
      {
         EObject oldeo = _selected.getEObject();
         if (oldeo != null)
         {
            oldeo.removeChangeListener(this);
         }
      }

      _selected = nNode;
      neweo.addChangeListener(this);
      return neweo;
   }

   public EView getInnerView()
   {
      return (EView) _cardBuffer.getCurrentItem();
   }

   private synchronized EView getViewFor(EObject eo)
   {
      EView view = viewCache.get(eo);
      if (view == null)
      {
         view = newViewFor(eo);
         viewCache.put(eo, view);
      }
      if (eo instanceof ComplexEObject)
      {
         ComplexEObject ceo = (ComplexEObject) eo;
         if (ceo.isEditState() && view instanceof Editor)
         {
            ceo.setEditor((Editor) view);
         }
      }
      return view;
   }

   private Map<EObject, EView> viewCache = new HashMap<EObject, EView>();

   private EView newViewFor(EObject eo)
   {
      EView view = eo.getMainView();
      // nested alternateviews in an omniview context can be
      // confusing so just plant innerview in there
      while (view instanceof AlternateView)
      {
         view = ((AlternateView) view).getInnerView();
      }
      return view;
   }

   public void detach()
   {
      _tree.removeTreeSelectionListener(this);
      _tree.detach();

      for (EView view : viewCache.values())
      {
         view.detach();
      }
      viewCache.clear();
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

   public void stateChanged(ChangeEvent e)
   {
      /*if (_selected != null)  {
            if (_selected.getEO() instanceof AbstractListEO)
            {
               if(_selected.refreshSubNodes()) _tree.updateUI();
            }
        }*/
   }

}
