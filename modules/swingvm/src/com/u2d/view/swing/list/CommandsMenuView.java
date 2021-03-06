package com.u2d.view.swing.list;

import com.u2d.view.ListEView;
import com.u2d.view.EView;
import com.u2d.view.swing.CommandAdapter;
import com.u2d.model.EObject;
import com.u2d.pattern.Onion;
import com.u2d.pattern.OnionPeeler;
import com.u2d.pattern.Processor;
import com.u2d.pattern.Filter;
import com.u2d.element.Command;
import javax.swing.*;
import javax.swing.event.ListDataEvent;
import java.util.Map;
import java.util.HashMap;
import java.awt.*;

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
   private Map<Integer, Integer> _indexMap = new HashMap<Integer, Integer>();
   
   private Filter _customFilter;

   public CommandsMenuView()
   {
      super();
   }
   
   public CommandsMenuView(Filter filter)
   {
      this();
      _customFilter = filter;
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

      _commands = _eo.filteredCommands();
      
      if (_customFilter != null)
         _commands = _commands.filter(_customFilter);
      
      new OnionPeeler(new Processor()
         {
            int index = 0;
            int subindex;
         
            public void process(Object obj)
            {
               Command cmd = (Command) obj;
               if (cmd.isOpenInNonMinimizedContext(_source)) return;

               add(new CommandAdapter(cmd, _eo, _source));
               _indexMap.put(index, getComponentCount() - 1);
               index++;
               subindex++;
            }
            public void pause()
            {
               if (subindex > 0)
                  addSeparator();
               subindex = 0;
            }
            public void done() {}
         }).peel(_commands);

      _commands.addListDataListener(this);
      String title = _eo.toString();
      setText(title);
      setMnemonic(title.charAt(0));
      _parent.add(this);
   }

   public void detach(boolean removeSelfFromMenu)
   {
      detachCmds();
      if (_eo != null)
      {
         _eo.removeChangeListener(this);
         _eo = null;
      }
      if (removeSelfFromMenu)
         _parent.remove(this);
   }
   public void detach() { detach(true); }

   private void detachCmds()
   {
      for (int i=0; i<getComponentCount(); i++)
      {
         Component c = getComponent(i);
         if (c instanceof JMenuItem)
         {
            JMenuItem item = (JMenuItem) getComponent(i);
            ((CommandAdapter) item.getAction()).detach();
         }
      }
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
               int componentIndex = _indexMap.get(i);

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
               int componentIndex = _indexMap.get(i);
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
