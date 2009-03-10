package com.u2d.view.swing.list;

import com.u2d.model.EObject;
import com.u2d.model.ComplexEObject;
import com.u2d.view.EView;
import com.u2d.view.ListEView;
import com.u2d.view.swing.CommandButton;
import com.u2d.pattern.Onion;
import com.u2d.pattern.OnionPeeler;
import com.u2d.pattern.Processor;
import com.u2d.element.Command;
import com.u2d.css4swing.style.ComponentStyle;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListDataEvent;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.awt.*;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXPanel;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Oct 14, 2005
 * Time: 11:02:34 AM
 */
public class CommandsButtonView extends JXPanel implements ListEView
{
   private EObject _eo;
   private Onion _commands;
   private JComponent _parent;
   private EView _source;
   private Map<Integer, Integer> _indexMap = new HashMap<Integer, Integer>();
   private boolean _horizontalLayout;

   public CommandsButtonView()
   {
      super();
      ComponentStyle.addClass(this, "formcommands-panel");
   }

   public void bind(EObject eo, JComponent parent, Object constraints, EView source)
   {
      _eo = eo;
      _eo.addChangeListener(this);

      _parent = parent;
      _horizontalLayout = (constraints == BorderLayout.PAGE_START ||
                           constraints == BorderLayout.PAGE_END);
      _source = source;

      setup();
      _parent.add(this, constraints);
   }

   public void detach()
   {
      detachCmds();
      if (_eo != null)
      {
         _eo.removeChangeListener(this);
         _eo = null;
      }
      if (_parent != null) // there's a case where view is not added
      {
         _parent.remove(this);
      }
      _source = null;
   }

   private void detachCmds()
   {
      for (int i=0; i<getComponentCount(); i++)
      {
         Component c = getComponent(i);
         if (c instanceof CommandButton)
         {
            ((CommandButton) c).detach();
         }
      }
      if (_commands != null)
      {
         _commands.removeListDataListener(this);
         removeAll();
         _commands = null;
      }
   }

   public boolean hasCommandsFor(ComplexEObject ceo, EView source)
   {
      int count = 0;
      for (Iterator itr = ceo.filteredCommands().iterator(); itr.hasNext(); )
      {
         Command cmd = (Command) itr.next();
         if (cmd.isOpenInNonMinimizedContext(source)) continue;
         if (cmd.isMinorCommand(source)) continue;
         count++;
      }
      return (count > 0);
   }

   private void setup()
   {
      detachCmds();
      if (_eo == null) return;
      
      _commands = _eo.commands();

      String insets = "insets 5";
      
      MigLayout layout;
      if (_horizontalLayout)
      {
         layout = new MigLayout(insets + ", alignx trailing", "fill, sizegroup", "");
      }
      else
      {
         layout = new MigLayout(insets + ", flowy", "[grow, fill]", "");
      }
      setLayout(layout);

      new OnionPeeler(new Processor()
         {
            int index = 0;
            boolean firstPass = true;
            boolean gapBefore = false;
            public void process(Object obj)
            {
               Command cmd = (Command) obj;
               if (cmd.isOpenInNonMinimizedContext(_source)) return;
               if (cmd.isMinorCommand(_source)) return;
               
               CommandButton btn = new CommandButton(cmd, _eo, _source, firstPass);
               firstPass = false;

               if (gapBefore)
               {
                  add(btn, "gaptop unrel");
                  gapBefore = false;
               }
               else
               {
                  add(btn);
               }

               _indexMap.put(index, getComponentCount() - 1);
               index++;
            }

            public void pause()
            {
               gapBefore = true;
            }
         
            public void done() {}
         
         }).peel(_eo.filteredCommands());

      //System.out.println("buttons grafted, source set on cmdAdapter: "+_source);

      revalidate();
      repaint();

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

               CommandButton btn = new CommandButton(cmd, _eo, _source, false);
               add(btn, componentIndex);
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
               CommandButton btn = (CommandButton) getComponent(componentIndex);
               btn.detach();
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
