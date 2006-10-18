package com.u2d.view.wings;

import com.u2d.view.ComplexEView;
import com.u2d.view.EView;
import com.u2d.view.wings.list.CommandsContextMenuView;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import java.beans.PropertyChangeEvent;
import java.awt.Insets;
import org.wings.SLabel;
import org.wings.SFont;
import org.wings.SImageIcon;
import org.wings.SConstants;
import javax.swing.ImageIcon;

/**
 * @author Eitan Suez
 */
public class TitleView extends SLabel implements ComplexEView
{
   protected ComplexEObject _ceo;
   private transient CommandsContextMenuView _cmdsView;

   private static SFont TITLE_FONT;
   static
   {
      TITLE_FONT = new SFont();
      TITLE_FONT.setStyle(SFont.BOLD);
      TITLE_FONT.setSize(16);
   }

   public TitleView(ComplexEObject ceo, EView parentView)
   {
      super();
      _ceo = ceo;
      _ceo.addPropertyChangeListener(this);
      _ceo.addChangeListener(this);

      _cmdsView = new CommandsContextMenuView();
      _cmdsView.bind(_ceo, this, parentView);

//      setFocusable(true);
      setHorizontalAlignment(SConstants.LEFT);
      setVerticalAlignment(SConstants.CENTER);
      setHorizontalTextPosition(SConstants.RIGHT);
      setVerticalTextPosition(SConstants.CENTER);
//      setOpaque(false);

      // TODO:  assign fonts and colors from preferences
      setFont(TITLE_FONT);

      if (_ceo.isTransientState())
      {
         if (_ceo.field() == null)
         {
            setText("New "+_ceo.type().getNaturalName());
         }
         else
         {
            setText(_ceo.field().label());
         }
      }
      else
      {
         setText(_ceo.title().toString());
      }
      setIcon(new SImageIcon((ImageIcon) _ceo.iconLg()));
      setBackground(_ceo.type().colorCode());

//      setTransferHandler(new EOTransferHandler(this));
   }

   public void stateChanged(javax.swing.event.ChangeEvent evt)
   {
      if (_ceo.getState() == null) return;
      if (!_ceo.isEditableState())
         setText(_ceo.title().toString());
   }

   public void propertyChange(PropertyChangeEvent evt)
   {
      if ("icon".equals(evt.getPropertyName()))
      {
         setIcon(new SImageIcon((ImageIcon)_ceo.iconLg()));
      }
   }

   public EObject getEObject() { return _ceo; }

//   protected void paintBorder(java.awt.Graphics g)
//   {
//      super.paintBorder(g);
//      Graphics2D g2 = (Graphics2D) g;
//      g2.setStroke(new BasicStroke(1f));
//      int y = getLocation().y + getSize().height - 1;
//      int x1 = getLocation().x;
//      int x2 = x1 + getSize().width;
//      g2.drawLine(x1, y, x2, y);
//   }

   public boolean isMinimized() { return false; }

   public void detach()
   {
      _cmdsView.detach();
      _ceo.removeChangeListener(this);
      _ceo.removePropertyChangeListener(this);
   }

}
