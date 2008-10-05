package com.u2d.view.echo.list;

import com.u2d.model.EObject;
import com.u2d.view.ListEView;
import com.u2d.view.EView;
import com.u2d.pattern.Onion;
import com.u2d.pattern.OnionPeeler;
import com.u2d.pattern.Processor;
import com.u2d.element.Command;
import nextapp.echo.app.Component;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.extras.app.ContextMenu;
import nextapp.echo.extras.app.menu.DefaultMenuModel;
import nextapp.echo.extras.app.menu.SeparatorModel;
import nextapp.echo.extras.app.menu.DefaultOptionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListDataEvent;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Oct 4, 2008
 * Time: 8:04:48 PM
 */
public class CommandsContextMenuView implements ListEView, ActionListener
{
   private ContextMenu _contextMenu;
   private EObject _eo;
   private DefaultMenuModel _model = new DefaultMenuModel();
   private Onion _commands;
   private Map<String, Command> _cmdMap = new HashMap<String, Command>();
   private EView _source;
   private Component _target;

   public CommandsContextMenuView()
   {
   }
   
   public void bind(EObject eo, EView source)
   {
      bind(eo, (Component) source, source);
   }

   public void bind(EObject eo, Component target, EView source)
   {
      _eo = eo;
      _source = source;
      _target = target;
      
      _commands = _eo.filteredCommands();
      new OnionPeeler(new Processor()
         {
            int index = 0;
            public void process(Object obj)
            {
               Command cmd = (Command) obj;
               if (cmd.isOpenInNonMinimizedContext(_source)) return;

               String key = cmd.fullPath();
               _cmdMap.put(key, cmd);
               _model.addItem(new DefaultOptionModel(key, cmd.label(), new ResourceImageReference(cmd.iconLgResourceRef())));

               index++;
            }
            public void pause()
            {
               _model.addItem(new SeparatorModel());
            }
            public void done() {}
         }).peel(_commands);

      _commands.addListDataListener(this);

      _contextMenu = new ContextMenu(_target, _model);
      _contextMenu.addActionListener(this);
   }

   public Component getContextMenu()
   {
      return _contextMenu;
   }

   public void actionPerformed(ActionEvent e)
   {
      Command cmd = _cmdMap.get(e.getActionCommand());
      try
      {
         cmd.execute(_eo, _source);
      }
      catch (InvocationTargetException e1)
      {
         e1.printStackTrace();
      }
   }

   public void detach()
   {
      _cmdMap.clear();
      _commands.removeListDataListener(this);
      _commands = null;
      _contextMenu.setApplyTo(null);
      _contextMenu = null;
      _eo = null;
      _source = null;
      _target = null;
   }

   public EObject getEObject() { return _eo; }
   public boolean isMinimized() { return false; }

   // tbd..
   public void stateChanged(ChangeEvent e) { }
   public void intervalAdded(ListDataEvent e) { }
   public void intervalRemoved(ListDataEvent e) { }
   public void contentsChanged(ListDataEvent e) { }

}

