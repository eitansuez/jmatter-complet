package com.u2d.view.swing.list;

import com.u2d.view.ListEView;
import com.u2d.view.EView;
import com.u2d.view.swing.CommandAdapter;
import com.u2d.view.swing.CommandIconButton;
import com.u2d.model.EObject;
import com.u2d.pattern.Onion;
import com.u2d.pattern.OnionPeeler;
import com.u2d.pattern.Processor;
import com.u2d.element.Command;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListDataEvent;
import java.util.Map;
import java.util.HashMap;
import java.awt.*;
import org.jdesktop.swingx.JXPanel;
import net.miginfocom.swing.MigLayout;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Apr 2, 2008
 * Time: 2:44:27 PM
 */
public class CommandsIconButtonView extends JXPanel implements ListEView
{
      private EObject _eo;
      private Onion _commands;
      private EView _source;
      private Map<Integer, Integer> _indexMap = new HashMap<Integer, Integer>();

      public CommandsIconButtonView()
      {
         super();
         setLayout(new FlowLayout(FlowLayout.LEADING));
         setOpaque(false);
         Border border = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
         setBorder(border);
      }

      public void bind(EObject eo, EView source)
      {
         _eo = eo;
         _eo.addChangeListener(this);
         _source = source;
         setup();
      }

      public void detach()
      {
         detachCmds();
         if (_eo != null)
         {
            _eo.removeChangeListener(this);
            _eo = null;
         }
      }

      private void detachCmds()
      {
         for (int i=0; i<getComponentCount(); i++)
         {
            ((CommandAdapter) ((JButton) getComponent(i)).getAction()).detach();
         }
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

         MigLayout layout = new MigLayout("insets 2");
         setLayout(layout);

         new OnionPeeler(new Processor()
         {
            int index = 0;

            public void process(Object obj)
            {
               Command cmd = (Command) obj;
               if (cmd.isOpenInNonMinimizedContext(_source)) return;
               if (cmd.isMinorCommand(_source)) return;

               CommandIconButton btn = new CommandIconButton(cmd, _eo, _source);
               add(btn);

               _indexMap.put(index, getComponentCount() - 1);
               index++;
            }

            public void pause() { }

            public void done() { }

         }).peel(_eo.filteredCommands());

         _commands.addListDataListener(this);
      }


      /*
       * this method and the next need a lot of work..
       */
      public void intervalAdded(final ListDataEvent e)
      {
         SwingUtilities.invokeLater(new Runnable() {
            public void run()
            {
               for (int i=e.getIndex0(); i<=e.getIndex1(); i++)
               {
                  Command cmd = (Command) _eo.commands().get(i);
                  int componentIndex = _indexMap.get(i);
                  add(new CommandIconButton(cmd, _eo, _source), componentIndex);
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
