package com.u2d.view.wings.atom;

import com.u2d.view.ListEView;
import com.u2d.view.EView;
import com.u2d.view.wings.CommandAdapter;
import com.u2d.view.wings.LockedButton;
import com.u2d.view.wings.DefaultButton;
import com.u2d.view.wings.NormalButton;
import com.u2d.model.EObject;
import com.u2d.pattern.Onion;
import com.u2d.pattern.OnionPeeler;
import com.u2d.pattern.Processor;
import com.u2d.element.Command;
import javax.swing.event.ListDataEvent;
import java.util.Map;
import java.util.HashMap;
import java.awt.Color;
import org.wings.*;
import org.wings.border.SBorder;
import org.wings.border.SEtchedBorder;
import javax.swing.Action;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Oct 14, 2005
 * Time: 11:02:34 AM
 */
public class CommandsButtonView extends SPanel implements ListEView
{
   private EObject _eo;
   private Onion _commands;
   private SContainer _parent;
   private EView _source;
   private Map _indexMap = new HashMap();
   private boolean _horizontalLayout;

   public CommandsButtonView()
   {
      super();
      setBackground(new Color(0xf8f8ff));
      SBorder border = new SEtchedBorder(SEtchedBorder.LOWERED);
      setBorder(border);
   }

   public void bind(EObject eo, SContainer parent, Object constraints, EView source)
   {
      _eo = eo;
      _eo.addChangeListener(this);

      _parent = parent;
      _horizontalLayout = (constraints == SBorderLayout.NORTH ||
                           constraints == SBorderLayout.SOUTH);
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
      if (_horizontalLayout)
      {
         setLayout(new SFlowLayout());
      }
      else
      {
         setLayout(new SFlowDownLayout());
      }

      new OnionPeeler(new Processor()
         {
            int index = 0;
            boolean firstPass = true;
            public void process(Object obj)
            {
               Command cmd = (Command) obj;
               if (cmd.isOpenInNonMinimizedContext(_source)) return;
               if (cmd.isMinorCommand(_source)) return;

               CommandAdapter cmdAdapter = new CommandAdapter(cmd, _eo, _source);
               SButton btn;
               if (cmd.sensitive())
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

               add(btn);

               _indexMap.put(new Integer(index),
                             new Integer(getComponentCount()-1));
               index++;
            }

         public void pause() { }

         public void done() {}
         }).peel(_eo.filteredCommands());

      //System.out.println("buttons grafted, source set on cmdAdapter: "+_source);

      _commands.addListDataListener(this);
   }


   /*
    * this method and the next need a lot of work..
    */
   public void intervalAdded(final ListDataEvent e)
   {
      for (int i=e.getIndex0(); i<=e.getIndex1(); i++)
      {
         Command cmd = (Command) _eo.commands().get(i);
         int componentIndex =
               ((Integer) _indexMap.get(new Integer(i))).intValue();

         Action action = new CommandAdapter(cmd, _eo, _source);
         add(new SButton(action), componentIndex);
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

}
