package com.u2d.view.wings;

import com.u2d.view.ComplexEView;
import com.u2d.view.wings.list.CommandsContextMenuView;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.awt.*;
import org.wings.SLabel;
import org.wings.SImageIcon;
import org.wings.SConstants;
import org.wings.SFont;
import org.wings.border.SEmptyBorder;

/**
 * @author Eitan Suez
 */
public class IconView extends SLabel implements ComplexEView
{
   protected ComplexEObject _ceo;
   private transient CommandsContextMenuView _cmdsView;
//   private MouseListener _defaultActionListener;

   public IconView()
   {
      setHorizontalAlignment(SConstants.CENTER);
      setVerticalAlignment(SConstants.TOP);
      setHorizontalTextPosition(SConstants.CENTER);
      setVerticalTextPosition(SConstants.BOTTOM);
      setBorder(new SEmptyBorder(0, 5, 0, 5));

      SFont font = new SFont();
      font.setSize(10);
      setFont(font);

      _cmdsView = new CommandsContextMenuView();
   }

   public void bind(ComplexEObject ceo)
   {
      _ceo = ceo;
      
      _ceo.addPropertyChangeListener(this);
      _ceo.addChangeListener(this);

      setText(_ceo.title().toString());
      setIcon(new SImageIcon((ImageIcon) _ceo.iconLg()));

      _cmdsView.bind(ceo, this);

      // TODO: NullComplexEObject should have its own iconview.
      //  i shouldn't have to put conditions in like this..
//      if (_ceo instanceof NullComplexEObject)
//      {
//         setTransferHandler(new DropTargetHandler());
//      }
//      else
//      {
//         setTransferHandler(new EOTransferHandler(this));  // setup as drag source
//      }

//      Command defaultCmd = _ceo.defaultCommand();
//      CommandAdapter defaultAction = new CommandAdapter(defaultCmd, _ceo, this);
//      _defaultActionListener = UIUtils.doubleClickActionListener(defaultAction);
//      addMouseListener(_defaultActionListener);
   }

   public void detach()
   {
      _ceo.removeChangeListener(this);
      _ceo.removePropertyChangeListener(this);
      
      _cmdsView.detach();
//      removeMouseListener(_defaultActionListener);
   }

   public EObject getEObject() { return _ceo; }

   public void propertyChange(PropertyChangeEvent evt)
   {
      if ("icon".equals(evt.getPropertyName()))
      {
         setIcon(new SImageIcon((ImageIcon) _ceo.iconLg()));
      }
   }

   public void stateChanged(javax.swing.event.ChangeEvent evt)
   {
      setText(_ceo.title().toString());
   }

   private Insets _insets = new Insets(3, 4, 3, 4);
   public Insets getInsets() { return _insets; }

   public boolean isMinimized() { return true; }

}
