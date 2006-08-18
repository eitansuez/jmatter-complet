package com.u2d.view.wings;

import com.u2d.view.ComplexEView;
import com.u2d.view.wings.list.CommandsContextMenuView;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import org.wings.SLabel;
import org.wings.SConstants;
import org.wings.SImageIcon;
import org.wings.SPopupMenu;

import javax.swing.ImageIcon;
import java.awt.Insets;

/**
 * @author Eitan Suez
 */
public class ListItemView extends SLabel implements ComplexEView
{
   protected ComplexEObject _ceo;
   private transient CommandsContextMenuView _cmdsView;
//   private MouseListener _defaultActionListener;

   public ListItemView()
   {
      setHorizontalAlignment(SConstants.LEFT);
      setVerticalAlignment(SConstants.CENTER);
      setHorizontalTextPosition(SConstants.RIGHT);

//      setTransferHandler(new EOTransferHandler(this));  // set up as drag source
      _cmdsView = new CommandsContextMenuView();
   }

   public void bind(ComplexEObject ceo)
   {
      _ceo = ceo;
      setText(_ceo.title().toString());
      setIcon(new SImageIcon((ImageIcon) _ceo.iconSm()));
      _cmdsView.bind(ceo, this);

      _ceo.addPropertyChangeListener(this);
      _ceo.addChangeListener(this);

//      Command defaultCmd = _ceo.defaultCommand();
//      CommandAdapter defaultAction = new CommandAdapter(defaultCmd, _ceo, this);
//      _defaultActionListener = UIUtils.doubleClickActionListener(defaultAction);
//      addMouseListener(_defaultActionListener);
   }
   
   public void detach()
   {
      _cmdsView.detach();
      _ceo.removeChangeListener(this);
      _ceo.removePropertyChangeListener(this);
//      removeMouseListener(_defaultActionListener);
   }

   public EObject getEObject() { return _ceo; }

   public void propertyChange(java.beans.PropertyChangeEvent evt)
   {
      if ("icon".equals(evt.getPropertyName()))
      {
         setIcon(new SImageIcon((ImageIcon) _ceo.iconSm()));
      }
   }

   public void stateChanged(javax.swing.event.ChangeEvent evt)
   {
      setText(_ceo.title().toString());
   }

   private Insets _insets = new Insets(2, 5, 2, 8);
   public Insets getInsets() { return _insets; }

   public boolean isMinimized() { return true; }

}
