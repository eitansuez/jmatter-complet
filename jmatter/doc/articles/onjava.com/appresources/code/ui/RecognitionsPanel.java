package org.jmatter.j1mgr.ui;

import com.u2d.model.AbstractListEO;
import com.u2d.model.ComplexEObject;
import com.u2d.model.EObject;
import com.u2d.pattern.Block;
import com.u2d.view.EView;
import javax.swing.*;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ChangeEvent;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: May 24, 2007
 * Time: 12:56:45 PM
 */
public class RecognitionsPanel extends JPanel implements EView, ListDataListener
{
   private AbstractListEO _leo;
   
   public RecognitionsPanel()
   {
      setLayout(new FlowLayout(FlowLayout.LEFT));
      setOpaque(false);
   }
   
   public RecognitionsPanel(AbstractListEO leo)
   {
      this();
      bind(leo);
   }
   
   public void bind(AbstractListEO leo)
   {
      _leo = leo;
      _leo.addListDataListener(this);
      buildUI();
   }
   
   public void intervalAdded(ListDataEvent e) { buildUI(); }
   public void intervalRemoved(ListDataEvent e) { buildUI(); } 
   public void contentsChanged(ListDataEvent e) { buildUI(); }
   
   private void buildUI()
   {
      removeAll();
      _leo.forEach(new Block()
      {
         public void each(ComplexEObject ceo)
         {
            JLabel label = new JLabel(ceo.iconSm());
            label.setToolTipText(ceo.toString());
            add(label);
         }
      });
   }

   public EObject getEObject() { return _leo; }
   public void detach()
   {
      _leo.removeListDataListener(this);
      removeAll();
   }

   public void stateChanged(ChangeEvent e) { }
}
