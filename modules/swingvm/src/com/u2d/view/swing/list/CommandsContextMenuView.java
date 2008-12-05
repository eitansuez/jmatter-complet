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
import com.u2d.ui.Platform;
import javax.swing.*;
import javax.swing.event.ListDataEvent;
import java.awt.event.*;
import java.awt.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Locale;

import org.jdesktop.swingx.painter.Painter;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Oct 14, 2005
 * Time: 11:03:09 AM
 */
public class CommandsContextMenuView extends JPopupMenu implements ListEView
{
   private EObject _eo;
   private Onion _commands;
   private EView _source;
   private JComponent _target;
   private Map<Integer, Integer> _indexMap = new HashMap<Integer, Integer>();
   private ContextMouseListener _listener = new ContextMouseListener();

   public CommandsContextMenuView()
   {
      setLightWeightPopupEnabled(true);
   }

   public void bind(final EObject eo, final JComponent target, final EView source)
   {
      _eo = eo;
      _eo.addChangeListener(this);
      _source = source;
      _target = target;
      _target.addMouseListener(_listener);
      
      _target.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
            put(KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, Platform.mask()), "popup-contextmenu");
      _target.getActionMap().put("popup-contextmenu", new AbstractAction()
         {
            public void actionPerformed(ActionEvent e)
            {
               JComponent focusOwner = (JComponent)
                     KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
               if (focusOwner instanceof JList)
               {
                  JList list = (JList) focusOwner;
                  int index = list.getSelectedIndex();
                  Point p = list.indexToLocation(index);

                  double factor = (getComponentOrientation().isLeftToRight()) ? 0.75 : 0.25;
                  Dimension targetSize = _target.getPreferredSize();
                  Dimension offset = new Dimension((int) (targetSize.width * factor), (int) (targetSize.height * 0.75));

                  int x, y;
                  if (getComponentOrientation().isLeftToRight())
                  {
                     x = p.x+offset.width;
                     y = p.y+offset.height;
                  }
                  else
                  {
                     x = p.x+offset.width-getPreferredSize().width;
                     y = p.y+offset.height;
                  }

                  show(focusOwner, x, y);
                  selectFirst();
               }
            }
         });
      
      setup();
      applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
   }

   /**
    * Undoing a JDK6u10 change that causes the JPopupMenu to not select the first element in the
    *  menu by default.
    */
   private void selectFirst()
   {
       MenuElement me[] = new MenuElement[2];
       me[0]= this;
       me[1]= getSubElements()[0];
       MenuSelectionManager.defaultManager().setSelectedPath(me);
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
               item.setOpaque(false);
               ComponentStyle.addClass(item, "command");
               add(item);
               
               _indexMap.put(index, getComponentCount() - 1);
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
               int componentIndex = _indexMap.get(i);

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

   class ContextMouseListener extends MouseAdapter
   {
      // for non-microsoft platforms:
      public void mousePressed(MouseEvent evt) { showIt(evt); }
      // for microsoft platform:
      public void mouseReleased(MouseEvent evt) { showIt(evt); }

      private void showIt(MouseEvent evt)
      {
         if (evt.isPopupTrigger() && isEnabled())
         {
            Component component = evt.getComponent();
            if (component.getComponentOrientation().isLeftToRight())
            {
               show(evt.getComponent(), evt.getX(), evt.getY());
            }
            else
            {
               show(evt.getComponent(), evt.getX()-getPreferredSize().width, evt.getY());
            }
         }
      }
   }



   Painter bgPainter;
   public void setBackgroundPainter(Painter p)
   {
      this.bgPainter = p;
   }

   @Override
   public boolean isOpaque() { return false; }

   @Override
   protected void paintComponent(Graphics g)
   {
      if (bgPainter != null)
      {
         bgPainter.paint((Graphics2D) g, this, getWidth(), getHeight());
      }
      super.paintComponent(g);
   }

   
}

