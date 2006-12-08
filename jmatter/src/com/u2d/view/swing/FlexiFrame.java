package com.u2d.view.swing;

import com.u2d.ui.desktop.CloseableJInternalFrame;
import com.u2d.ui.Platform;
import com.u2d.view.*;
import com.u2d.model.EObject;
import com.u2d.model.ComplexEObject;
import com.u2d.model.AbstractListEO;
import com.u2d.app.Context;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.util.List;
import java.util.ArrayList;
import java.awt.event.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Dec 5, 2006
 * Time: 3:56:43 PM
 * @author Eitan Suez
 */
public class FlexiFrame extends CloseableJInternalFrame implements RootView, ChangeListener
{
   private List<JComponent> _views = new ArrayList<JComponent>();
   private TabContainer _tabPane = new TabContainer();
   private static TabFocusListener _tabFocusListener = new TabFocusListener();
   
   public FlexiFrame()
   {
      super();
      setResizable(true); setMaximizable(true); setIconifiable(true); setClosable(true);
      setupToFocusOnDragEnter();
   }
   public FlexiFrame(JComponent view)
   {
      this();
      addView(view);
      pack();
   }
   
   public void addView(JComponent view)
   {
      if (_views.isEmpty())
      {
         _views.add(view);
         if (view instanceof EView)
         {
            ((EView) view).getEObject().addChangeListener(this);
         }
         setContentPane(view);
         setDecorations();
      }
      else if (_views.size() == 1)
      {
         JComponent comp = _views.get(0);
         remove(comp);
         if (comp instanceof EView)
         {
            ((EView) view).getEObject().removeChangeListener(this);
         }
         _views.add(view);
         _tabPane.addTab(_views.get(0));
         _tabPane.addTab(_views.get(1));
         setContentPane(_tabPane);
      }
      else if (_views.size() > 1)
      {
         _views.add(view);
         _tabPane.addTab(view);
      }

      updateSize();
   }
   
   public void stateChanged(final ChangeEvent e)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            Object source = e.getSource();
            if (source instanceof ComplexEObject)
            {
               ComplexEObject ceo = (ComplexEObject) source;
               if (ceo.isNullState())
               {
                  dispose();
               }
            }
            
            setDecorations();
         }
      });
   }
   
   private void setDecorations()
   {
      if (_views.size() == 1)
      {
         if (_views.get(0) instanceof EView)
         {
            EView view = (EView) _views.get(0);
            setFrameIcon(view.getEObject().iconSm());
            setTitle(view.getEObject().toString());
         }
         else if (_views.get(0) instanceof View)
         {
            View view = (View) _views.get(0);
            setFrameIcon(view.iconSm());
            setTitle(view.getTitle());
         }
      }
   }
   

   public void removeView(JComponent view)
   {
      if (_views.size() > 2)
      {
         _tabPane.removeTab(view);
         _views.remove(view);
         detach(view);
      }
      else if (_views.size() == 2)
      {
         _tabPane.removeTab(view);
         _views.remove(view);
         detach(view);
         _tabPane.removeTab(_views.get(0));
         remove(_tabPane);
         setContentPane(_views.get(0));
         setDecorations();
         requestFocusInWindow();
      }
      else
      {
         dispose();
      }
      revalidate(); repaint();
   }
   
   public void replaceView(JComponent view)
   {
      if (_views.size() == 1)
      {
         remove(_views.get(0));
         detach(_views.get(0));
         _views.clear();
         setContentPane(view);
         _views.add(view);
      }
      else
      {
         _tabPane.replaceView(view);
      }
      revalidate(); repaint();
   }
   
   public void dispose()
   {
      super.dispose();
      detach();
   }
   
   private void detach()
   {
      if (_views.size() == 1 && _views.get(0) instanceof EView)
      {
         ((EView) _views.get(0)).getEObject().removeChangeListener(this);
      }
      for (JComponent view : _views)
      {
         detach(view);
      }
      _views.clear();
      _tabPane.detach();
      
   }
   private void detach(JComponent view)
   {
      if (view instanceof EView)
      {
         ((EView) view).detach();
      }
   }
   
   static String CLOSETAB_MAP_KEY = "CLOSE_TAB";
   static KeyStroke COMMAND_W = KeyStroke.getKeyStroke(KeyEvent.VK_W, Platform.mask());
   
   class TabContainer extends JTabbedPane implements ChangeListener
   {
      JPopupMenu _contextMenu;
      Action _closeAction, _detachAction;
      
      public TabContainer()
      {
         setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
         
         setupActions();
         setupKeyBindingForClosingSelectedTab();
         setupContextMenu();
      }
      
      private void setupActions()
      {
         _closeAction = new AbstractAction("Close Tab")
         {
            public void actionPerformed(ActionEvent evt)
            {
               JComponent comp = (JComponent) getSelectedComponent();
               if (comp != null)
               {
                  if (getTabCount() > 1)
                  {
                     removeView(comp);
                  }
                  else
                  {
                     FlexiFrame.this.close();
                  }
               }
            }
         };
         _detachAction = new AbstractAction("Detach Tab")
         {
            public void actionPerformed(ActionEvent evt)
            {
               JComponent view = (JComponent) getSelectedComponent();
               removeTab(view);
               _views.remove(view);
               
               FlexiFrame detachedView = new FlexiFrame(view);
               Context.getInstance().swingvmech().displayFrame(detachedView);
            }
         };
      }
      
      private void setupContextMenu()
      {
         _contextMenu = new JPopupMenu();
         _contextMenu.add(_detachAction);
         _contextMenu.add(_closeAction);

         addMouseListener(new MouseAdapter()
         {
            public void mousePressed(MouseEvent e)
            {
               if (SwingUtilities.isRightMouseButton(e))
               {
                  _contextMenu.show(TabContainer.this, e.getX(), e.getY());
                  e.consume();
               }
            }
         });
      }
      
      private void setupKeyBindingForClosingSelectedTab()
      {
         getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(COMMAND_W, CLOSETAB_MAP_KEY);
         getActionMap().put(CLOSETAB_MAP_KEY, _closeAction);
      }


      public void stateChanged(final ChangeEvent e)
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               for (int i=0; i<getTabCount(); i++)
               {
                  JComponent c = (JComponent) getComponentAt(i);
                  if (c instanceof ComplexEView)
                  {
                     ComplexEObject ceo = (ComplexEObject) ((ComplexEView) c).getEObject();
                     if (ceo == e.getSource())
                     {
                        if (ceo.isNullState())
                        {
                           removeView(c);
                           return;
                        }
                        if (!ceo.isEditableState())
                        {
                           setTitleAt(i, ceo.title().toString());
                        }
                     }
                  }
                  else if (c instanceof ListEView)
                  {
                     AbstractListEO leo = (AbstractListEO) ((ListEView) c).getEObject();
                     if (leo == e.getSource())
                     {
                        setTitleAt(i, leo.title().toString());
                     }
                  }
               }
            }
         });
      }

      public void detach()
      {
         for (int i=0; i<getTabCount(); i++)
         {
            detach(i);
         }
      }
      public void detach(int index)
      {
         Component c = getComponentAt(index);
         if (c instanceof EView)
         {
            ((EView) c).getEObject().removeChangeListener(this);
         }
      }


      public void removeNotify()
      {
         super.removeNotify();
         getActionMap().remove(CLOSETAB_MAP_KEY);
      }

      public void replaceView(JComponent view)
      {
         int index = getSelectedIndex();
         super.removeTabAt(index);
         JComponent prevView = _views.remove(index);
         FlexiFrame.this.detach(prevView);
         detach(index);
         View aView = viewFor(view);
         super.insertTab(aView.getTitle(), aView.iconSm(), view, null, index);
         setSelectedComponent(view);
         requestFocusInWindow(view);
         if (view instanceof EView)
         {
            ((EView) view).getEObject().addChangeListener(this);
         }
      }
      
      public void addTab(JComponent comp)
      {
         View aView = viewFor(comp);
         super.addTab(aView.getTitle(), aView.iconSm(), comp);
         setSelectedComponent(comp);
         requestFocusInWindow(comp);
         setupKeyBindingForTabIndex(getTabCount()-1);
         if (comp instanceof EView)
         {
            ((EView) comp).getEObject().addChangeListener(this);
         }
      }
   
      public void removeTab(JComponent comp)
      {
         int index = _views.indexOf(comp);
         super.removeTabAt(index);
         removeKeyBindingForTabIndex(index);
      }
      private void removeKeyBindingForTabIndex(final int index)
      {
         int i = index + 1;
         getActionMap().remove("FOCUS_TAB_"+i);
      }
      private void setupKeyBindingForTabIndex(final int index)
      {
         int i = index + 1;
         KeyStroke shortcut = KeyStroke.getKeyStroke(
                                   Character.forDigit(i, 10),
                                   InputEvent.ALT_MASK);
         String mapKey = "FOCUS_TAB_"+i;
         getInputMap(WHEN_IN_FOCUSED_WINDOW).put(shortcut, mapKey);
         getActionMap().put(mapKey, new AbstractAction()
         {
            public void actionPerformed(ActionEvent evt)
            {
               if (index < getTabCount())
                  setSelectedIndex(index);
            }
         });
      }

      private View viewFor(JComponent comp)
      {
         if (comp instanceof View)
         {
            return (View) comp;
         }
         else if (comp instanceof EView)
         {
            return new ViewAdapter((EView) comp);
         }
         else
         {
            return new BasicView(comp);
         }
      }
      
      private void requestFocusInWindow(JComponent comp)
      {
         // delay request to get around problem with getting focus..
         comp.addComponentListener(_tabFocusListener);
      }
   

   }  // end class TabContainer
   
   
   class ViewAdapter implements View
   {
      EObject _eo;
      
      ViewAdapter(EView eview)
      {
         _eo = eview.getEObject();
      }
      
      public String getTitle()
      {
         if (_eo instanceof ComplexEObject)
         {
            ComplexEObject ceo = (ComplexEObject) _eo;
            if (ceo.isTransientState())
            {
               return "New " + ceo.type().getNaturalName();
            }
         }
         return _eo.title().toString();
      }
      
      public Icon iconSm() { return _eo.iconSm(); }
      public Icon iconLg() { return _eo.iconLg(); }
      public boolean withTitlePane() { return true; }
   }
   class BasicView implements View
   {
      JComponent _comp;
      BasicView(JComponent comp)
      {
         _comp = comp;
      }

      // hmmm.. TODO: still thinking about how to deal with this..
      public String getTitle() { return _comp.toString(); }

      public Icon iconSm() { return null; }
      public Icon iconLg() { return null; }
      public boolean withTitlePane() { return false; }
   }
   
   static class TabFocusListener extends ComponentAdapter
   {
      public void componentShown(ComponentEvent e)
      {
         Component c = e.getComponent();
         Container parent = c.getParent();
         if (parent instanceof JTabbedPane)
         {
            JTabbedPane tp = (JTabbedPane) parent;
            if (tp.getSelectedComponent() == c)
            {
               c.requestFocusInWindow();
            }
         }
      }
   }
   
}

