package com.u2d.view.swing.list;

import com.u2d.view.ListEView;
import com.u2d.view.EView;
import com.u2d.view.swing.CommandAdapter;
import com.u2d.model.EObject;
import com.u2d.pattern.Onion;
import com.u2d.pattern.OnionPeeler;
import com.u2d.pattern.Processor;
import com.u2d.element.Command;
import com.u2d.app.Tracing;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Oct 14, 2005
 * Time: 11:02:48 AM
 */
public class CommandsMenuView extends JMenu implements ListEView
{
   private EObject _eo;
   private Onion _commands;
   private EView _source;
   private JComponent _parent;
   private Map _indexMap = new HashMap();

   public CommandsMenuView()
   {
      super("Commands");
      setMnemonic('c');
   }

   public void bind(EObject eo, JComponent parent, EView source)
   {
      _eo = eo;
      _eo.addChangeListener(this);

      _parent = parent;
      _source = source;
      
      setup();
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
               if (cmd.isForbidden())
               {
                  Tracing.tracer().info("command "+cmd+" is forbidden;  skipping");
                  return;
               }

               if (_eo.field() != null &&
                   _eo.field().isAggregate() &&
                   "delete".equalsIgnoreCase(cmd.name()))
                  return;

               add(new CommandAdapter(cmd, _eo, _source));
               _indexMap.put(new Integer(index),
                             new Integer(getComponentCount()-1));
               index++;
            }
            public void pause()
            {
               addSeparator();
            }
            public void done() {}
         }).peel(_commands);

      _commands.addListDataListener(this);
      _parent.add(this);
   }

   public void detach()
   {
      detachCmds();
      if (_eo != null)
      {
         _eo.removeChangeListener(this);
         _eo = null;
      }
      _parent.remove(this);
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
               add(new JMenuItem(action), componentIndex);
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

}
