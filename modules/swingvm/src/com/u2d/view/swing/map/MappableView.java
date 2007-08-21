package com.u2d.view.swing.map;

import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.view.ComplexEView;
import com.u2d.view.swing.list.CommandsContextMenuView;
import org.jdesktop.swingx.JXPanel;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.beans.PropertyChangeEvent;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 20, 2007
 * Time: 11:01:13 PM
 */
public class MappableView extends JXPanel
      implements ComplexEView
{
   private ComplexEObject _ceo;
   private transient CommandsContextMenuView _cmdsView;
   
   public MappableView(ComplexEObject ceo)
   {
      _ceo = ceo;
      Icon icon = _ceo.iconLg();
      String text = _ceo.title().toString();
      JLabel label = new JLabel(text, icon, SwingConstants.LEFT);
      add(label);
      
      _cmdsView = new CommandsContextMenuView();
      _cmdsView.bind(_ceo, this);
   }

   public EObject getEObject() { return _ceo; }
   public boolean isMinimized() { return true; }

   public void propertyChange(PropertyChangeEvent evt) { }
   public void stateChanged(ChangeEvent e) { }

   public void detach()
   {
      _cmdsView.detach();
   }


}
