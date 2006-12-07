package com.u2d.view.swing.list;

import com.u2d.view.CompositeView;
import com.u2d.view.ListEView;
import com.u2d.view.EView;
import com.u2d.view.swing.CommandAdapter;
import com.u2d.model.AbstractListEO;
import com.u2d.model.EObject;
import com.u2d.ui.Platform;
import com.u2d.element.Command;
import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Dec 5, 2006
 * Time: 5:10:55 PM
 */
public class ListEOPanel  extends JPanel
      implements ListEView, CompositeView
{
   private TitleBarView _titleBarView;
   private EView _view;
   private AbstractListEO _leo;
   
   public ListEOPanel(EView view)
   {
      _view = view;
      _leo = (AbstractListEO) _view.getEObject();

      layItOut();
      setupKeyStrokes();
   }

   private void layItOut()
   {
      setLayout(new BorderLayout());
      _titleBarView = new TitleBarView(_leo, this, _view);
      add(_titleBarView, BorderLayout.NORTH);

      if (_view instanceof CompositeView)
      {
         add((JComponent) _view, BorderLayout.CENTER);
      }
      else
      {
         JScrollPane scrollPane = new JScrollPane((JComponent) _view);
         add(scrollPane, BorderLayout.CENTER);
      }
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
   
   public void stateChanged(ChangeEvent e) { }
   public void contentsChanged(ListDataEvent evt) {  }
   public void intervalAdded(ListDataEvent evt) { }
   public void intervalRemoved(ListDataEvent evt) { }

   public EView getInnerView()
   {
      EView view = _view;
      while (view instanceof CompositeView)
         view = ((CompositeView) view).getInnerView();
      return view;
   }
   
   public void detach()
   {
      _titleBarView.detach();
      _view.detach();
   }
   
   public boolean isMinimized() { return false; }

}
