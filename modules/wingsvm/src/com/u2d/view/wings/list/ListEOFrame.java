package com.u2d.view.wings.list;

import com.u2d.view.ListEView;
import com.u2d.view.CompositeView;
import com.u2d.view.EView;
import com.u2d.model.AbstractListEO;
import com.u2d.model.EObject;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.ImageIcon;
import org.wings.*;

/**
 * @author Eitan Suez
 */
public class ListEOFrame extends SInternalFrame
               implements ListEView, CompositeView
{
   private TitleBarView _titleBarView;
   private EView _view;
   private AbstractListEO _leo;

   public ListEOFrame(EView view)
   {
      super();
      setMaximizable(true);  setClosable(true);
//      setResizable(true); setIconifiable(true);

      _view = view;
      _leo = (AbstractListEO) _view.getEObject();
      _leo.addListDataListener(this);
      _leo.addChangeListener(this);

      setTitle(_leo.title().toString());
      setIcon(new SImageIcon((ImageIcon) _leo.iconSm()));

      SContainer contentPane = getContentPane();
      contentPane.setLayout(new SBorderLayout());
      _titleBarView = new TitleBarView(_leo, this, _view);
      contentPane.add(_titleBarView, SBorderLayout.NORTH);

      contentPane.add((SComponent) _view, SBorderLayout.CENTER);

      setupKeyStrokes();

//      pack();
   }

//   static String MAP_KEY = "new-item";
//   static KeyStroke COMMAND_N =
//         KeyStroke.getKeyStroke(KeyEvent.VK_N, Platform.mask());

   private void setupKeyStrokes()
   {
//      Command newCmd = _leo.command("New");
//      if (newCmd != null)
//      {
//         getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(com.u2d.view.wings.list.ListEOFrame.COMMAND_N, com.u2d.view.wings.list.ListEOFrame.MAP_KEY);
//         getActionMap().put(com.u2d.view.wings.list.ListEOFrame.MAP_KEY, new CommandAdapter(newCmd, _leo, this));
//      }
   }

   public EView getView() { return _view; }
   public EObject getEObject() { return _leo; }

   public void contentsChanged(ListDataEvent evt) { updateTitle(); }
   public void intervalAdded(ListDataEvent evt) { updateTitle(); }
   public void intervalRemoved(ListDataEvent evt) { updateTitle(); }

   private void updateTitle() { setTitle(_leo.title().toString()); }

   public void stateChanged(ChangeEvent evt) {}

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
