package com.u2d.view.swing.map;

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
import net.miginfocom.swing.MigLayout;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 20, 2007
 * Time: 11:01:13 PM
 */
public class MappableView extends JXPanel
      implements ComplexEView
{
   private EObject _eo;
   private transient CommandsContextMenuView _cmdsView;
   
   public MappableView(EObject eo)
   {
      _eo = eo;

      MigLayout layout = new MigLayout("insets 0");
      setLayout(layout);
      
      JButton closeButton = new CloseButton();
      closeButton.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            MappableView.this.setVisible(false);
         }
      });
      JLabel label = new JLabel(_eo.title().toString(), _eo.iconLg(), SwingConstants.LEADING);
      
      add(closeButton, "alignx trailing, wrap");
      add(label, "alignx leading, grow");
      
      _cmdsView = new CommandsContextMenuView();
      _cmdsView.bind(_eo, this);
   }
   
   public EObject getEObject() { return _eo; }
   public boolean isMinimized() { return true; }

   public void propertyChange(PropertyChangeEvent evt) { }
   public void stateChanged(ChangeEvent e) { }

   public void detach()
   {
      _cmdsView.detach();
   }


}
