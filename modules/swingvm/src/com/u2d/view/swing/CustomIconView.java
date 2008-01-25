/*
 * Created on Jan 26, 2004
 */
package com.u2d.view.swing;

import java.beans.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.view.*;
import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.BadLocationException;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

/**
 * @author Eitan Suez
 */
public class CustomIconView
      extends JLabel implements ComplexEView
{
   protected ComplexEObject _ceo;
   
   public CustomIconView()
   {
      setHorizontalAlignment(SwingConstants.CENTER);
      setVerticalAlignment(SwingConstants.CENTER);

      setHorizontalTextPosition(SwingConstants.CENTER);
      setVerticalTextPosition(SwingConstants.BOTTOM);

      setAlignmentX(0.5f);
      setAlignmentY(0.5f);
   }

   public void bind(ComplexEObject ceo)
   {
      _ceo = ceo;
      _ceo.addPropertyChangeListener(this);
      _ceo.addChangeListener(this);

      setText(_ceo.title().toString());
      setIcon(_ceo.iconLg());
   }

   public void detach()
   {
      if (_ceo == null) return;
      _ceo.removeChangeListener(this);
      _ceo.removePropertyChangeListener(this);
   }

   public EObject getEObject() { return _ceo; }

   public void propertyChange(PropertyChangeEvent evt)
   {
      if ("icon".equals(evt.getPropertyName()))
      {
         setIcon(_ceo.iconLg());
      }
   }

   public void stateChanged(javax.swing.event.ChangeEvent evt)
   {
      SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               setText(_ceo.title().toString());
            }
         });
   }

   public boolean isMinimized() { return true; }

   
   private Document _filterDoc;
   
   public void setFilterDoc(Document filterDoc)
   {
      _filterDoc = filterDoc;
      _filterDoc.addDocumentListener(new DocumentListener()
      {
         public void insertUpdate(DocumentEvent e) { repaint(); }
         public void removeUpdate(DocumentEvent e) { repaint(); }
         public void changedUpdate(DocumentEvent e) { repaint(); }
      });
   }

   private String getFilterDocText()
   {
      String txt;
      try {
          txt = _filterDoc.getText(0, _filterDoc.getLength());
      } catch (BadLocationException e) {
          txt = null;
      }
      return txt;
   }
   
   private Color highlightColor = new Color(128, 128, 255);

   protected void paintComponent(Graphics g)
   {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D) g;
      
      String lblText = getText();
      FontMetrics fm = g2.getFontMetrics(g2.getFont());
      Rectangle textR = new Rectangle();
      SwingUtilities.layoutCompoundLabel(this, fm, lblText, getIcon(), getVerticalAlignment(),
                                         getHorizontalAlignment(), getVerticalTextPosition(),
                                         getHorizontalTextPosition(), getBounds(), new Rectangle(), textR,
                                         getIconTextGap());
      
      
      int x = (int) textR.getX() - getX();
      int y = (int) textR.getY() - getY();

      g2.setColor(highlightColor);
      
      String matchText = getFilterDocText().toLowerCase();
      lblText = lblText.toLowerCase();
      for (int i=0; i<lblText.length(); i++)
      {
         Rectangle2D r = fm.getStringBounds(lblText, i, i+1, g2);
         if (matchText.contains(lblText.substring(i, i+1)))
         {
            g2.fillRect(x, y, (int) r.getWidth(), (int) r.getHeight());
         }
         x += r.getWidth();
      }
      
      g2.setColor(getForeground());
      x = (int) textR.getX() - getX();
      y = (int) textR.getY() + g2.getFontMetrics().getAscent() - getY();
      g2.drawString(getText(), x, y);
      
   }
}