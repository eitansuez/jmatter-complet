package com.u2d.view.swing.map;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.ui.CloseButton;
import com.u2d.view.ComplexEView;
import com.u2d.view.swing.list.CommandsContextMenuView;
import org.jdesktop.swingx.JXPanel;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
      
      FormLayout formLayout = new FormLayout("5px, left:pref:grow, right:pref",
                                             "pref, pref:grow, 5px");
      CellConstraints cc = new CellConstraints();
      setLayout(formLayout);
      
      JButton closeButton = new CloseButton();
      closeButton.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            MappableView.this.setVisible(false);
         }
      });
      JLabel label = new JLabel(_ceo.title().toString(), _ceo.iconLg(), SwingConstants.LEFT);
      
      add(closeButton, cc.rc(1, 3));
      add(label, cc.rc(2,2));
      
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
