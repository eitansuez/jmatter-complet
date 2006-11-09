package com.u2d.view.swing.list;

import com.u2d.model.EObject;
import com.u2d.view.EView;
import com.u2d.view.ListEView;
import com.u2d.view.swing.CommandAdapter;
import com.u2d.pattern.Onion;
import com.u2d.pattern.OnionPeeler;
import com.u2d.pattern.Processor;
import com.u2d.element.Command;
import com.u2d.ui.LockedButton;
import com.u2d.ui.DefaultButton;
import com.u2d.ui.NormalButton;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.ButtonStackBuilder;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListDataEvent;
import java.util.Map;
import java.util.HashMap;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Oct 14, 2005
 * Time: 11:02:34 AM
 */
public class CommandsButtonView extends JPanel implements ListEView
{
   private EObject _eo;
   private Onion _commands;
   private JComponent _parent;
   private EView _source;
   private Map _indexMap = new HashMap();
   private boolean _horizontalLayout;

   public CommandsButtonView()
   {
      super();
      FormLayout layout = new FormLayout("5px, pref, 5px", "pref");
      setLayout(layout);
      setOpaque(true);
      setBackground(new Color(0xf8f8ff));
      Border border = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
      setBorder(border);
   }

   public void bind(EObject eo, JComponent parent, Object constraints, EView source)
   {
      _eo = eo;
      _eo.addChangeListener(this);

      _parent = parent;
      _horizontalLayout = (constraints == BorderLayout.NORTH ||
                           constraints == BorderLayout.SOUTH);
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

   private void setup()
   {
      detachCmds();
      if (_eo == null) return;
      
      _commands = _eo.commands();

      // No Common Interface between a buttonbarbuilder and a buttonstackbuilder!!
      final PanelBuilder builder;
      if (_horizontalLayout)
      {
         builder = new ButtonBarBuilder();
         ((ButtonBarBuilder) builder).addGlue();
      }
      else
      {
         builder = new ButtonStackBuilder();
         ((ButtonStackBuilder) builder).addRelatedGap();
      }

      // TODO:  produce another onion that is filtered and reduced
      //  and then bind to that.
      new OnionPeeler(new Processor()
         {
            int index = 0;
            boolean firstPass = true;
            public void process(Object obj)
            {
               Command cmd = (Command) obj;
               if (cmd.isOpenInNonMinimizedContext(_source)) return;
               if (cmd.isMinorCommand(_source)) return;
               
               if (cmd.filter(_eo)) return;

               CommandAdapter cmdAdapter = new CommandAdapter(cmd, _eo, _source);
               JButton btn;
               if (cmd.isSensitive())
               {
                  btn = new LockedButton(cmdAdapter);
                  firstPass = false;
               }
               else
               {
                  if (firstPass)
                  {
                     btn = new DefaultButton(cmdAdapter);
                     firstPass = false;
                  }
                  else
                  {
                     btn = new NormalButton(cmdAdapter);
                  }
               }

               if (_horizontalLayout)
               {
                  ((ButtonBarBuilder) builder).addFixedNarrow(btn);
                  ((ButtonBarBuilder) builder).addRelatedGap();
               }
               else
               {
                  ((ButtonStackBuilder) builder).addFixed(btn);
                  ((ButtonStackBuilder) builder).addRelatedGap();
               }

               _indexMap.put(new Integer(index),
                             new Integer(getComponentCount()-1));
               index++;
            }

         public void pause()
         {
            if (_horizontalLayout)
            {
               ((ButtonBarBuilder) builder).addUnrelatedGap();
            }
            else
            {
               ((ButtonStackBuilder) builder).addUnrelatedGap();
            }
         }
            public void done() {}
         }).peel(_eo.commands());

      //System.out.println("buttons grafted, source set on cmdAdapter: "+_source);
      JPanel buttonPnl = builder.getPanel();
      buttonPnl.setOpaque(false);

      CellConstraints cc = new CellConstraints();
      add(buttonPnl,  cc.xy(2, 1));
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
               int componentIndex =
                     ((Integer) _indexMap.get(new Integer(i))).intValue();

               Action action = new CommandAdapter(cmd, _eo, _source);
               add(new JButton(action), componentIndex);
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
