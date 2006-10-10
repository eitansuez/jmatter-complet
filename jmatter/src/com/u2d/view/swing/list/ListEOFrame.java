/*
 * Created on Dec 12, 2003
 */
package com.u2d.view.swing.list;

import com.u2d.model.AbstractListEO;
import com.u2d.model.EObject;
import com.u2d.ui.desktop.CloseableJInternalFrame;
import com.u2d.ui.Platform;
import com.u2d.view.*;
import com.u2d.view.swing.CommandAdapter;
import com.u2d.element.Command;
import java.awt.*;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.event.*;

/**
 * @author Eitan Suez
 */
public class ListEOFrame extends CloseableJInternalFrame
               implements ListEView, CompositeView
{
   private TitleBarView _titleBarView;
   private EView _view;
   private AbstractListEO _leo;
   
   public ListEOFrame(EView view)
   {
      super();
      setResizable(true); setMaximizable(true); setIconifiable(true); setClosable(true);

      _view = view;
      _leo = (AbstractListEO) _view.getEObject();
      _leo.addListDataListener(this);
      _leo.addChangeListener(this);
      
      setTitle(_leo.title().toString());
      setFrameIcon(_leo.iconSm());
      
      JPanel contentPane = (JPanel) getContentPane();
      _titleBarView = new TitleBarView(_leo, this, _view);
      contentPane.add(_titleBarView, BorderLayout.NORTH);
      
      if (_view instanceof CompositeView)
      {
         contentPane.add((JComponent) _view, BorderLayout.CENTER);
      }
      else
      {
         JScrollPane scrollPane = new JScrollPane((JComponent) _view);
         contentPane.add(scrollPane, BorderLayout.CENTER);
      }
      
      setupKeyStrokes();
      
      pack();
   }

   static String MAP_KEY = "new-item";
   static KeyStroke COMMAND_N = 
         KeyStroke.getKeyStroke(KeyEvent.VK_N, Platform.mask());

   private void setupKeyStrokes()
   {
      Command newCmd = _leo.command("New");
      if (newCmd != null)
      {
         getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(COMMAND_N, MAP_KEY);
         getActionMap().put(MAP_KEY, new CommandAdapter(newCmd, _leo, this));
      }
   }

   public EView getView() { return _view; }
   public EObject getEObject() { return _leo; }
   
   public void contentsChanged(ListDataEvent evt) { updateTitle(); }
   public void intervalAdded(ListDataEvent evt) { updateTitle(); }
   public void intervalRemoved(ListDataEvent evt) { updateTitle(); }

   private void updateTitle()
   {
      if (!SwingUtilities.isEventDispatchThread())
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               setTitle(_leo.title().toString());
            }
         });
      }
      else
      {
         setTitle(_leo.title().toString());
      }
   }

   public void stateChanged(javax.swing.event.ChangeEvent evt) {}
   
   public EView getInnerView()
   {
      EView view = _view;
      while (view instanceof CompositeView)
         view = ((CompositeView) view).getInnerView();
      return view;
   }
   
   public void dispose()
   {
      super.dispose();
      _leo.removeListDataListener(this);
      _leo.removeChangeListener(this);
      detach();
   }
   
   public void detach()
   {
      _titleBarView.detach();
      _view.detach();
      _titleBarView = null;
      _view = null;
      _leo = null;
   }
   
   public boolean isMinimized() { return false; }

}
