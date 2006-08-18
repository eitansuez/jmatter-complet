package com.u2d.view.wings.list;

import com.u2d.view.ListEView;
import com.u2d.view.EView;
import com.u2d.view.wings.CommandAdapter;
import com.u2d.model.EObject;
import com.u2d.pattern.Onion;
import com.u2d.pattern.OnionPeeler;
import com.u2d.pattern.Processor;
import com.u2d.element.Command;
import javax.swing.event.ListDataEvent;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import java.util.Map;
import java.util.HashMap;
import org.wings.SPopupMenu;
import org.wings.SSeparator;
import org.wings.SMenuItem;
import org.wings.SComponent;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Oct 14, 2005
 * Time: 11:03:09 AM
 */
public class CommandsContextMenuView
      extends SPopupMenu
      implements ListEView
{
   private EObject _eo;
   private Onion _commands;
   private EView _source;
   private EView _target;
   private Map _indexMap = new HashMap();
//   private ContextMouseListener _listener = new ContextMouseListener();

   public CommandsContextMenuView() {}

   public void bind(EObject eo, EView target, EView source)
   {
      _eo = eo;
      _eo.addChangeListener(this);

      _source = source;

      _target = target;

      setup();

//      ((SComponent) _target).addMouseListener(_listener);
      ((SComponent) _target).setComponentPopupMenu(this);
   }

   public void bind(EObject eo, EView source)
   {
      bind(eo, source, source);
   }

   public void detach()
   {
      detachCmds();
      if (_eo != null)
      {
         _eo.removeChangeListener(this);
         _eo = null;
      }

      if (_target != null)
      {
//         ((SComponent) _target).removeMouseListener(_listener);
      }
      _indexMap.clear();
      _source = null;
      _target = null;
   }

   private void detachCmds()
   {
      if (_commands != null)
      {
         _commands.removeListDataListener(this);
         removeAll();
         _commands = null;
      }
   }

   private void setup()
   {
      detachCmds();
      if (_eo == null) return;

      _commands = _eo.commands();
      new OnionPeeler(new Processor()
         {
            int index = 0;
            public void process(Object obj)
            {
               Command cmd = (Command) obj;
               if (cmd.isOpenInNonMinimizedContext(_source)) return;

               if (cmd.filter(_eo)) return;

               Action commandAdapter = new CommandAdapter(cmd, _eo, _source);
               add(new SMenuItem(commandAdapter));
               _indexMap.put(new Integer(index),
                             new Integer(getMenuComponentCount()-1));
               index++;
            }
            public void pause()
            {
               add(new SSeparator());
            }
            public void done() {}
         }).peel(_commands);

      _commands.addListDataListener(this);
   }

   public void intervalAdded(final ListDataEvent e)
   {
      for (int i=e.getIndex0(); i<=e.getIndex1(); i++)
      {
         Command cmd = (Command) _eo.commands().get(i);
//               int componentIndex =
//                     ((Integer) _indexMap.get(new Integer(i))).intValue();

         Action action = new CommandAdapter(cmd, _eo, _source);
//               add(new SMenuItem(action), componentIndex);
         add(new SMenuItem(action));
      }
   }

   public void intervalRemoved(final ListDataEvent e)
   {
      for (int i=e.getIndex1(); i>=e.getIndex0(); i--)
      {
         int componentIndex = ((Integer) _indexMap.
            get(new Integer(i))).intValue();
         remove(componentIndex);
      }
   }

   public void contentsChanged(ListDataEvent e) { setup(); }
   public void stateChanged(javax.swing.event.ChangeEvent evt) { setup(); }

   public EObject getEObject() { return _eo; }
   public boolean isMinimized() { return false; }

//   class ContextMouseListener extends MouseAdapter
//   {
//      // for non-microsoft platforms:
//      public void mousePressed(MouseEvent evt)
//      {
//         if (evt.isPopupTrigger() && isEnabled())
//         {
//            show(evt.getComponent(), evt.getX(), evt.getY());
//         }
//      }
//      // for microsoft platform:
//      public void mouseReleased(MouseEvent evt)
//      {
//         if (evt.isPopupTrigger() && isEnabled())
//            show(evt.getComponent(), evt.getX(), evt.getY());
//      }
//   }

}
