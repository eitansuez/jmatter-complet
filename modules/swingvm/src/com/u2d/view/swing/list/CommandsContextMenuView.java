package com.u2d.view.swing.list;

import com.u2d.view.ListEView;
import com.u2d.view.EView;
import com.u2d.view.swing.CommandAdapter;
import com.u2d.model.EObject;
import com.u2d.element.Command;
import com.u2d.pattern.Onion;
import com.u2d.pattern.OnionPeeler;
import com.u2d.pattern.Processor;
import com.u2d.css4swing.style.ComponentStyle;
import javax.swing.*;
import javax.swing.event.ListDataEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Oct 14, 2005
 * Time: 11:03:09 AM
 */
public class CommandsContextMenuView
      extends JPopupMenu
      implements ListEView
{
   private EObject _eo;
   private Onion _commands;
   private EView _source;
   private JComponent _target;
   private Map<Integer, Integer> _indexMap = new HashMap<Integer, Integer>();
   private ContextMouseListener _listener = new ContextMouseListener();

   public CommandsContextMenuView() {}

   public void bind(final EObject eo, final JComponent target, final EView source)
   {
      _eo = eo;
      _eo.addChangeListener(this);
      _source = source;
      _target = target;
      _target.addMouseListener(_listener);
      setup();
   }
   
   public void bind(EObject eo, EView source)
   {
      bind(eo, (JComponent) source, source);
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
         _target.removeMouseListener(_listener);
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

      _commands = _eo.filteredCommands();
      new OnionPeeler(new Processor()
         {
            int index = 0;
//            int subindex = 0;
            public void process(Object obj)
            {
               Command cmd = (Command) obj;
               if (cmd.isOpenInNonMinimizedContext(_source)) return;

               JMenuItem item = new JMenuItem(new CommandAdapter(cmd, _eo, _source));
               ComponentStyle.addClass(item, "command");
               add(item);
               
               _indexMap.put(new Integer(index),
                             new Integer(getComponentCount()-1));
               index++;
//               subindex++;
            }
            public void pause()
            {
               addSeparator();
//               if (subindex > 0) addSeparator();
//               subindex = 0;
            }
            public void done() {}
         }).peel(_commands);

      _commands.addListDataListener(this);
   }

   public void intervalAdded(final ListDataEvent e)
   {
      SwingUtilities.invokeLater(new Runnable() {
         public void run()
         {
            for (int i=e.getIndex0(); i<=e.getIndex1(); i++)
            {
               Command cmd = (Command) _eo.commands().get(i);
               int componentIndex = 
                     ((Integer) _indexMap.get(new Integer(i))).intValue();

               Action action = new CommandAdapter(cmd, _eo, _source);
               JMenuItem item = new JMenuItem(action);
               ComponentStyle.addClass(item, "command");
               add(item, componentIndex);
            }
         }
      });
   }

   public void intervalRemoved(final ListDataEvent e)
   {
      SwingUtilities.invokeLater(new Runnable() {
         public void run()
         {
            for (int i=e.getIndex1(); i>=e.getIndex0(); i--)
            {
               int componentIndex = ((Integer) _indexMap.
                  get(new Integer(i))).intValue();
               remove(componentIndex);
            }
         }
      });
   }

   public void contentsChanged(ListDataEvent e)
   {
      SwingUtilities.invokeLater(new Runnable() { public void run() { setup(); }});
   }

   public void stateChanged(javax.swing.event.ChangeEvent evt)
   {
      SwingUtilities.invokeLater(new Runnable() { public void run() { setup(); }});
   }

   public EObject getEObject() { return _eo; }
   public boolean isMinimized() { return false; }

   class ContextMouseListener extends MouseAdapter
   {
      // for non-microsoft platforms:
      public void mousePressed(MouseEvent evt)
      {
         if (evt.isPopupTrigger() && isEnabled())
         {
            show(evt.getComponent(), evt.getX(), evt.getY());
         }
      }
      // for microsoft platform:
      public void mouseReleased(MouseEvent evt)
      {
         if (evt.isPopupTrigger() && isEnabled())
            show(evt.getComponent(), evt.getX(), evt.getY());
      }
   }
   
}

