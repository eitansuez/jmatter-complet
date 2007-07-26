package com.u2d.view.swing;

import com.u2d.type.composite.Folder;
import com.u2d.pattern.Block;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.view.swing.list.CommandsMenuView;
import com.u2d.view.EView;
import javax.swing.*;
import javax.swing.event.ChangeEvent;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Jun 4, 2007
 * Time: 3:35:23 PM
 */
public class ClassMenu extends JMenu implements EView
{
   private Folder _classBar;
   private JMenuBar _menuBar;
   
   public ClassMenu()
   {
      super("Types");
      setMnemonic('y');
   }
   
   public void bind(Folder classBar, final JMenuBar menuBar, final int index)
   {
      _classBar = classBar;
      _menuBar = menuBar;
      _classBar.getItems().forEach(new Block()
      {
         public void each(ComplexEObject ceo)
         {
            if (ceo instanceof Folder)
            {
               bind((Folder) ceo);
               addSeparator();
            }
            else
            {
               CommandsMenuView menu = new CommandsMenuView();
               menu.bind(ceo, ClassMenu.this, ClassMenu.this);
               add(menu);
            }
         }
      });
      menuBar.add(this, index);
   }
   private void bind(Folder folder)
   {
      folder.getItems().forEach(new Block()
      {
         public void each(ComplexEObject ceo)
         {
            if (ceo instanceof Folder)
            {
               bind((Folder) ceo);
            }
            else
            {
               CommandsMenuView menu = new CommandsMenuView();
               menu.bind(ceo, ClassMenu.this, ClassMenu.this);
               add(menu);
            }
         }
      });
   }
   
   public void detach()
   {
      for (int i=0; i<getMenuComponentCount(); i++)
      {
         if (getMenuComponent(i) instanceof CommandsMenuView)
         {
            CommandsMenuView menu = (CommandsMenuView) getMenuComponent(i);
            menu.detach(false);
         }
      }
      removeAll();
      _menuBar.remove(this);
   }

   public EObject getEObject() { return _classBar; }
   public void stateChanged(ChangeEvent e) { }
}
